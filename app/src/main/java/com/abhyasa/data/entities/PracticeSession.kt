package com.abhyasa.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "practice_sessions")
data class PracticeSession(
    @PrimaryKey(autoGenerate = true) val sessionId: Int = 0,
    val name: String,
    val isTimed: Boolean,
    val totalDuration: Int?,
    val loopEnabled: Boolean = false,
    val loopCount: Int = 1,
    val isSimpleSession: Boolean = false,
)
