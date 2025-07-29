package com.abhyasa.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.abhyasa.data.entities.Subtask

@Dao
interface SubTaskDao {
    @Insert
    suspend fun insert(subtask: Subtask): Long

    @Query("SELECT * FROM subtasks WHERE taskId = :taskId")
    suspend fun getSubtasksForTask(taskId: Int): List<Subtask>

    @Query("DELETE FROM subtasks WHERE subtaskId = :subtaskId")
    suspend fun deleteSubtaskById(subtaskId: Int)

    @Query("DELETE FROM subtasks WHERE taskId = :taskId")
    suspend fun deleteSubtasksByTaskId(taskId: Int)

    @Query("UPDATE subtasks SET name = :name, duration = :duration WHERE subtaskId = :subtaskId")
    suspend fun updateSubtask(subtaskId: Int, name: String, duration: Int)
}
