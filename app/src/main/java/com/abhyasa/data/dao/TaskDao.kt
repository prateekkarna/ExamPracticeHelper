package com.abhyasa.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.abhyasa.data.entities.Task

@Dao
interface TaskDao {
    @Insert
    suspend fun insert(task: Task): Long

    @Query("SELECT * FROM tasks WHERE sessionId = :sessionId")
    suspend fun getTasksForSession(sessionId: Int): List<Task>

    @Query("DELETE FROM tasks WHERE taskId = :taskId")
    suspend fun deleteTaskById(taskId: Int)

    @Query("DELETE FROM tasks WHERE sessionId = :sessionId")
    suspend fun deleteTasksBySessionId(sessionId: Int)

    @Query("UPDATE tasks SET text = :text, hasSubtasks = :hasSubtasks, taskDuration = :taskDuration, typeLabel = :typeLabel WHERE taskId = :taskId")
    suspend fun updateTask(taskId: Int, text: String, hasSubtasks: Boolean, taskDuration: Int?, typeLabel: String)
}
