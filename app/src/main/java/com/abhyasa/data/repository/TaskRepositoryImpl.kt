package com.abhyasa.data.repository

import com.abhyasa.data.entities.Task
import com.abhyasa.data.dao.TaskDao

class TaskRepositoryImpl(private val dao: TaskDao) : TaskRepository {
    override suspend fun insertTask(task: Task): Long = dao.insert(task)
}
