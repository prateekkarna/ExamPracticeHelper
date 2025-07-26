package com.example.exampractisehelper.data.repository

import com.example.exampractisehelper.data.entities.Task

interface TaskRepository {
    suspend fun insertTask(task: Task): Long
}
