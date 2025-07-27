package com.example.exampractisehelper.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.exampractisehelper.data.entities.PracticeSession

@Dao
interface PracticeSessionDao {
    @Insert
    suspend fun insert(session: PracticeSession): Long

    @Query("SELECT * FROM practice_sessions")
    suspend fun getAllSessions(): List<PracticeSession>

    @Query("DELETE FROM practice_sessions WHERE sessionId = :sessionId")
    suspend fun deleteSessionById(sessionId: Int)
}
