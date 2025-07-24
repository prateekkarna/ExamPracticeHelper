package com.example.exampractisehelper.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey(autoGenerate = true) val questionId: Int = 0,
    val sessionId: Int,
    val text: String,
    val hasSections: Boolean,
    val questionDuration: Int?,
    val typeLabel: String
)
