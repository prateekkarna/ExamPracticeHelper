package com.abhyasa.data.repository

import com.abhyasa.data.entities.Subtask

interface SubtaskRepository {
    suspend fun insertSubtask(subtask: Subtask): Long
}
