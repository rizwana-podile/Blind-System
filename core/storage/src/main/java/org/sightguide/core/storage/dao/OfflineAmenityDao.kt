package org.sightguide.core.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.sightguide.core.storage.entity.OfflineAmenityEntity

@Dao
interface OfflineAmenityDao {

    @Query("SELECT * FROM offline_amenities WHERE geohash LIKE :geohashPrefix || '%'")
    suspend fun getAmenitiesByGeohashPrefix(geohashPrefix: String): List<OfflineAmenityEntity>

    @Query("SELECT * FROM offline_amenities WHERE category = :category AND geohash LIKE :geohashPrefix || '%'")
    suspend fun getAmenitiesByCategoryAndGeohash(
        category: String,
        geohashPrefix: String
    ): List<OfflineAmenityEntity>

    @Query("SELECT * FROM offline_amenities WHERE category = :category LIMIT :limit")
    suspend fun getAmenitiesByCategory(category: String, limit: Int = 20): List<OfflineAmenityEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAmenities(amenities: List<OfflineAmenityEntity>)

    @Query("SELECT COUNT(*) FROM offline_amenities")
    suspend fun getCount(): Int
}
