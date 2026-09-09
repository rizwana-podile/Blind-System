package org.sightguide.feature.nearby.repository

import org.sightguide.core.common.geometry.GeoUtils
import org.sightguide.core.common.model.Coordinates
import org.sightguide.core.storage.dao.OfflineAmenityDao
import org.sightguide.core.storage.entity.AmenityCategory
import org.sightguide.core.storage.entity.OfflineAmenityEntity
import org.sightguide.core.storage.util.GeohashUtils
import org.sightguide.feature.nearby.model.NearbyPlaceItem

/**
 * Repository providing offline-first amenity queries and geohashed spatial search.
 */
class NearbyPlacesRepository(
    private val amenityDao: OfflineAmenityDao
) {

    suspend fun findNearby(
        currentCoords: Coordinates,
        currentHeading: Float,
        categoryFilter: AmenityCategory? = null
    ): List<NearbyPlaceItem> {
        ensureSampleDataSeeded(currentCoords)

        val prefix = GeohashUtils.encode(currentCoords.latitude, currentCoords.longitude, precision = 4)
        val entities = if (categoryFilter != null) {
            amenityDao.getAmenitiesByCategoryAndGeohash(categoryFilter.name, prefix)
        } else {
            amenityDao.getAmenitiesByGeohashPrefix(prefix)
        }

        return entities.map { entity ->
            val placeCoords = Coordinates(entity.latitude, entity.longitude)
            val dist = GeoUtils.distanceMeters(currentCoords, placeCoords)
            val bearing = GeoUtils.initialBearingDegrees(currentCoords, placeCoords)
            val clock = GeoUtils.relativeClockDirection(currentHeading, bearing)

            NearbyPlaceItem(
                id = entity.id,
                name = entity.name,
                category = AmenityCategory.fromString(entity.category),
                distanceMeters = dist,
                clockPosition = clock,
                coordinates = placeCoords,
                address = entity.streetAddress,
                openingHours = entity.openingHours
            )
        }.sortedBy { it.distanceMeters }
    }

    private suspend fun ensureSampleDataSeeded(center: Coordinates) {
        if (amenityDao.getCount() == 0) {
            val lat = center.latitude
            val lng = center.longitude

            val seeds = listOf(
                OfflineAmenityEntity(
                    name = "Community Pharmacy",
                    category = AmenityCategory.PHARMACY.name,
                    latitude = lat + 0.0012,
                    longitude = lng + 0.0008,
                    geohash = GeohashUtils.encode(lat + 0.0012, lng + 0.0008, precision = 7),
                    streetAddress = "142 Health Ave",
                    openingHours = "Open 24 Hours"
                ),
                OfflineAmenityEntity(
                    name = "City Central Hospital & Urgent Care",
                    category = AmenityCategory.HOSPITAL.name,
                    latitude = lat + 0.0035,
                    longitude = lng - 0.0015,
                    geohash = GeohashUtils.encode(lat + 0.0035, lng - 0.0015, precision = 7),
                    streetAddress = "500 Civic Center Blvd",
                    openingHours = "Emergency 24/7"
                ),
                OfflineAmenityEntity(
                    name = "Metro Transit Station",
                    category = AmenityCategory.TRANSIT.name,
                    latitude = lat - 0.0018,
                    longitude = lng + 0.0022,
                    geohash = GeohashUtils.encode(lat - 0.0018, lng + 0.0022, precision = 7),
                    streetAddress = "Corner of 4th & Market",
                    openingHours = "5:00 AM - 1:00 AM"
                ),
                OfflineAmenityEntity(
                    name = "Accessible ATM & Bank",
                    category = AmenityCategory.BANK.name,
                    latitude = lat + 0.0007,
                    longitude = lng - 0.0009,
                    geohash = GeohashUtils.encode(lat + 0.0007, lng - 0.0009, precision = 7),
                    streetAddress = "220 Commerce Way",
                    openingHours = "ATM 24 Hours"
                ),
                OfflineAmenityEntity(
                    name = "Neighborhood Fresh Grocery",
                    category = AmenityCategory.GROCERY.name,
                    latitude = lat - 0.0025,
                    longitude = lng - 0.0018,
                    geohash = GeohashUtils.encode(lat - 0.0025, lng - 0.0018, precision = 7),
                    streetAddress = "88 Green Street",
                    openingHours = "7:00 AM - 10:00 PM"
                )
            )
            amenityDao.insertAmenities(seeds)
        }
    }
}
