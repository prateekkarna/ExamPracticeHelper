package com.example.exampractisehelper.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val taskId: Int = 0,
    val sessionId: Int,
    val text: String,
    val hasSubtasks: Boolean,
    val taskDuration: Int?,
    val typeLabel: String
)
