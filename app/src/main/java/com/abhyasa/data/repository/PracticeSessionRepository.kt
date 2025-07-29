package com.abhyasa.data.repository

import com.abhyasa.data.entities.PracticeSession

interface PracticeSessionRepository {
    suspend fun insertSession(session: PracticeSession): Long
    suspend fun getAllSessions(): List<PracticeSession>
    suspend fun deleteSessionById(sessionId: Int)
}
