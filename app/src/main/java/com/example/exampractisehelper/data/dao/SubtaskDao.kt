package com.example.exampractisehelper.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.exampractisehelper.data.entities.Subtask

@Dao
interface SubTaskDao {
    @Insert
    suspend fun insert(subtask: Subtask): Long

    @Query("SELECT * FROM subtasks WHERE taskId = :taskId")
    suspend fun getSubtasksForTask(taskId: Int): List<Subtask>
}

