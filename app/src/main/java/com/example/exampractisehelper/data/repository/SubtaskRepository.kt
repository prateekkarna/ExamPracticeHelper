package com.example.exampractisehelper.data.repository

import com.example.exampractisehelper.data.entities.Subtask

interface SubtaskRepository {
    suspend fun insertSubtask(subtask: Subtask): Long
}
