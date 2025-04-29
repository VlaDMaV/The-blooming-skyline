package com.example.thebloomingskyline.catalogue.config

import com.example.thebloomingskyline.catalogue.entity.Flower
import com.example.thebloomingskyline.catalogue.dao.FlowerDao


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Flower::class],
    version = 1,
    exportSchema = false  // Можно отключить если не нужна история миграций
)
abstract class FlowerDatabase : RoomDatabase() {
    abstract fun flowerDao(): FlowerDao

    companion object {
        @Volatile
        private var INSTANCE: FlowerDatabase? = null

        fun getDatabase(context: Context): FlowerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlowerDatabase::class.java,
                    "flower_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}