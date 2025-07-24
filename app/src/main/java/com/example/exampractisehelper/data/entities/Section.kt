package com.example.exampractisehelper.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sections")
data class Section(
    @PrimaryKey(autoGenerate = true) val sectionId: Int = 0,
    val questionId: Int,
    val name: String,
    val duration: Int
)
