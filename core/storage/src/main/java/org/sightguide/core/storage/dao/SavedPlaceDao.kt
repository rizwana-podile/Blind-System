package org.sightguide.core.storage.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.sightguide.core.storage.entity.SavedPlaceEntity

@Dao
interface SavedPlaceDao {

    @Query("SELECT * FROM saved_places ORDER BY frequencyCount DESC, lastVisitedMillis DESC")
    fun getAllSavedPlaces(): Flow<List<SavedPlaceEntity>>

    @Query("SELECT * FROM saved_places WHERE customVoiceTag LIKE '%' || :query || '%' OR label LIKE '%' || :query || '%' LIMIT 5")
    suspend fun findPlacesByVoiceTag(query: String): List<SavedPlaceEntity>

    @Query("SELECT * FROM saved_places WHERE id = :id")
    suspend fun getPlaceById(id: Long): SavedPlaceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: SavedPlaceEntity): Long

    @Update
    suspend fun updatePlace(place: SavedPlaceEntity)

    @Delete
    suspend fun deletePlace(place: SavedPlaceEntity)

    @Query("UPDATE saved_places SET frequencyCount = frequencyCount + 1, lastVisitedMillis = :visitedAt WHERE id = :id")
    suspend fun incrementVisit(id: Long, visitedAt: Long = System.currentTimeMillis())
}
