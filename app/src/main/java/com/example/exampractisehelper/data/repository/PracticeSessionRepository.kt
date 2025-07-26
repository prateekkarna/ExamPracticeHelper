package com.example.exampractisehelper.data.repository

import com.example.exampractisehelper.data.entities.PracticeSession

interface PracticeSessionRepository {
    suspend fun insertSession(session: PracticeSession): Long
    suspend fun getAllSessions(): List<PracticeSession>
}
