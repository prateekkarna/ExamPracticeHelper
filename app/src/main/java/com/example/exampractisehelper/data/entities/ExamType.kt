package com.example.exampractisehelper.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exam_types")
data class ExamType(
    @PrimaryKey(autoGenerate = true) val typeId: Int = 0,
    val examId: Int,
    val name: String,
    val defaultDuration: Int?,
    val userOverrideDuration: Int?
)
