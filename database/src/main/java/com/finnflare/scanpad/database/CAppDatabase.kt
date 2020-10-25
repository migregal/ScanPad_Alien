package com.finnflare.scanpad.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.finnflare.scanpad.database.dao.CScanResultDao
import com.finnflare.scanpad.database.entity.CEntityScanResults

@Database(
    entities = [
        CEntityScanResults::class
    ],
    version = 1
)

abstract class CAppDatabase : RoomDatabase() {
    abstract fun scanResults(): CScanResultDao

    companion object {
        @Volatile
        private var instance: CAppDatabase? = null

        fun getInstance(context: Context): CAppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(
                    context
                ).also { instance = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                CAppDatabase::class.java,
                "honeywell_scanpad_database.db"
            ).build()
    }
}