package org.sightguide.core.storage.util

/**
 * High-performance Geohash encoder/decoder for offline spatial POI indexing.
 * Standard base-32 geohashing enables fast prefix-based geographic proximity lookups in SQLite/Room.
 */
object GeohashUtils {

    private const val BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz"
    private val BITS = intArrayOf(16, 8, 4, 2, 1)

    /**
     * Encodes latitude and longitude into a geohash of specified [precision] length.
     * Precision 6 corresponds to ~1.2km bounding box; Precision 7 ~150m; Precision 8 ~38m.
     */
    fun encode(latitude: Double, longitude: Double, precision: Int = 7): String {
        var latMin = -90.0
        var latMax = 90.0
        var lonMin = -180.0
        var lonMax = 180.0

        val geohash = StringBuilder()
        var isEven = true
        var bit = 0
        var ch = 0

        while (geohash.length < precision) {
            if (isEven) {
                val mid = (lonMin + lonMax) / 2
                if (longitude >= mid) {
                    ch = ch or BITS[bit]
                    lonMin = mid
                } else {
                    lonMax = mid
                }
            } else {
                val mid = (latMin + latMax) / 2
                if (latitude >= mid) {
                    ch = ch or BITS[bit]
                    latMin = mid
                } else {
                    latMax = mid
                }
            }

            isEven = !isEven
            if (bit < 4) {
                bit++
            } else {
                geohash.append(BASE32[ch])
                bit = 0
                ch = 0
            }
        }
        return geohash.toString()
    }
}
