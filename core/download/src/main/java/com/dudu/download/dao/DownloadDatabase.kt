package com.dudu.download.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dudu.common.util.ContextManager

/**
 * 功能介绍
 * Created by Dzc on 2023/6/28.
 */
@Database(
    version = 1,
    entities = [DownloadTaskData::class],
    exportSchema = false
)
abstract class DownloadDatabase : RoomDatabase() {
    abstract fun downloadTaskDao(): DownloadTaskDao

    companion object{
        private var instance: DownloadDatabase? = null

        @Synchronized
        fun getInstance(): DownloadDatabase {
            return instance?: Room.databaseBuilder(
                ContextManager.getContext(),
                DownloadDatabase::class.java,
                "download_database"
            ).build().apply {
                instance = this
            }
        }

    }
}