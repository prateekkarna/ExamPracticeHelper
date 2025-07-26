package com.example.exampractisehelper.data.repository

import com.example.exampractisehelper.data.entities.Subtask
import com.example.exampractisehelper.data.dao.SubTaskDao

class SubtaskRepositoryImpl(private val dao: SubTaskDao) : SubtaskRepository {
    override suspend fun insertSubtask(subtask: Subtask): Long = dao.insert(subtask)
}
