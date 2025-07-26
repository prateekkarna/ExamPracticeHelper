package com.example.exampractisehelper.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.exampractisehelper.data.entities.Task

@Dao
interface TaskDao {
    @Insert
    suspend fun insert(task: Task): Long

    @Query("SELECT * FROM tasks WHERE sessionId = :sessionId")
    suspend fun getTasksForSession(sessionId: Int): List<Task>
}
