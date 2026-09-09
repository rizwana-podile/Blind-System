package org.sightguide.core.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.sightguide.core.storage.entity.AuditTrailEntity

@Dao
interface AuditTrailDao {

    @Query("SELECT * FROM audit_trail ORDER BY timestampMillis DESC")
    fun getAllLogs(): Flow<List<AuditTrailEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditTrailEntity): Long

    @Query("DELETE FROM audit_trail WHERE timestampMillis < :olderThanMillis")
    suspend fun deleteLogsOlderThan(olderThanMillis: Long): Int
}
