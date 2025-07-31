package com.abhyasa.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.abhyasa.data.entities.PracticeSession
import kotlinx.coroutines.flow.Flow

@Dao
interface PracticeSessionDao {
    @Insert
    suspend fun insert(session: PracticeSession): Long

    @Query("SELECT * FROM practice_sessions")
    suspend fun getAllSessions(): List<PracticeSession>

    @Query("SELECT * FROM practice_sessions")
    fun observeAllSessions(): Flow<List<PracticeSession>>

    @Query("DELETE FROM practice_sessions WHERE sessionId = :sessionId")
    suspend fun deleteSessionById(sessionId: Int)

    @Query("UPDATE practice_sessions SET name = :name, isTimed = :isTimed, totalDuration = :totalDuration WHERE sessionId = :sessionId")
    suspend fun updateSession(sessionId: Int, name: String, isTimed: Boolean, totalDuration: Int?)

    @Query("SELECT COUNT(*) FROM practice_sessions")
    suspend fun getSessionCount(): Int
}
