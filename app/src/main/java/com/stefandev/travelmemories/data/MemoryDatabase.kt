package com.stefandev.travelmemories.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Memory::class], version = 1, exportSchema = false)
abstract class MemoryDatabase: RoomDatabase() {
    abstract fun memoryDao(): MemoryDao
    companion object{
        @Volatile
        private var INSTANCE: MemoryDatabase? = null
        fun getDatabase(context: Context): MemoryDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MemoryDatabase::class.java,
                    "memory_table"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}