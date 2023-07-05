package com.dudu.download.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * 功能介绍
 * Created by Dzc on 2023/6/28.
 */
@Dao
interface DownloadTaskDao {

    @Insert
    suspend fun insertTask(task: DownloadTaskData): Long

    @Delete
    suspend fun deleteTask(task: DownloadTaskData)

    @Update
    suspend fun updateTask(task: DownloadTaskData)

    @Query("select * from download_task order by createTime desc")
    suspend fun queryTask(): List<DownloadTaskData>


    @Query("select * from download_task where id = :id")
    fun queryTaskId(id: Long): DownloadTaskData

}