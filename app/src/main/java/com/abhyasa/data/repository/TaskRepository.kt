package com.abhyasa.data.repository

import com.abhyasa.data.entities.Task

interface TaskRepository {
    suspend fun insertTask(task: Task): Long
}
