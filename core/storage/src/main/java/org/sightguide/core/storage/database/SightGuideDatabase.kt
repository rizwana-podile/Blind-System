package org.sightguide.core.storage.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.sightguide.core.storage.dao.AuditTrailDao
import org.sightguide.core.storage.dao.EmergencyContactDao
import org.sightguide.core.storage.dao.OfflineAmenityDao
import org.sightguide.core.storage.dao.SavedPlaceDao
import org.sightguide.core.storage.entity.AuditTrailEntity
import org.sightguide.core.storage.entity.EmergencyContactEntity
import org.sightguide.core.storage.entity.OfflineAmenityEntity
import org.sightguide.core.storage.entity.SavedPlaceEntity

/**
 * Primary local Room Database for SIGHTGUIDE offline resilience.
 */
@Database(
    entities = [
        EmergencyContactEntity::class,
        SavedPlaceEntity::class,
        OfflineAmenityEntity::class,
        AuditTrailEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SightGuideDatabase : RoomDatabase() {

    abstract fun emergencyContactDao(): EmergencyContactDao
    abstract fun savedPlaceDao(): SavedPlaceDao
    abstract fun offlineAmenityDao(): OfflineAmenityDao
    abstract fun auditTrailDao(): AuditTrailDao

    companion object {
        private const val DATABASE_NAME = "sightguide_database.db"

        @Volatile
        private var INSTANCE: SightGuideDatabase? = null

        fun getInstance(context: Context): SightGuideDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SightGuideDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
