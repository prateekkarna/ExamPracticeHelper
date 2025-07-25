package com.example.exampractisehelper.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subtasks")
data class Subtask(
    @PrimaryKey(autoGenerate = true) val subtaskId: Int = 0,
    val taskId: Int,
    val name: String,
    val duration: Int
)
