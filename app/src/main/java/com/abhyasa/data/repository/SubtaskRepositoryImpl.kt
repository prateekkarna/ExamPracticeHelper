package com.abhyasa.data.repository

import com.abhyasa.data.entities.Subtask
import com.abhyasa.data.dao.SubTaskDao

class SubtaskRepositoryImpl(private val dao: SubTaskDao) : SubtaskRepository {
    override suspend fun insertSubtask(subtask: Subtask): Long = dao.insert(subtask)
}
