package com.abhyasa.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exams")
data class Exam(
    @PrimaryKey(autoGenerate = true) val examId: Int = 0,
    val name: String,
    val description: String,
    val isCustom: Boolean = false,
    val iconUrl: String? = null
)

