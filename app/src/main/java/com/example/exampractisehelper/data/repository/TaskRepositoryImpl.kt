package com.example.exampractisehelper.data.repository

import com.example.exampractisehelper.data.entities.Task
import com.example.exampractisehelper.data.dao.TaskDao

class TaskRepositoryImpl(private val dao: TaskDao) : TaskRepository {
    override suspend fun insertTask(task: Task): Long = dao.insert(task)
}
