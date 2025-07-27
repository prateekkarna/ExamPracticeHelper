package com.example.exampractisehelper.data.repository

import com.example.exampractisehelper.data.entities.PracticeSession
import com.example.exampractisehelper.data.dao.PracticeSessionDao

class PracticeSessionRepositoryImpl(private val dao: PracticeSessionDao) : PracticeSessionRepository {
    override suspend fun insertSession(session: PracticeSession): Long = dao.insert(session)
    override suspend fun getAllSessions(): List<PracticeSession> = dao.getAllSessions()
    override suspend fun deleteSessionById(sessionId: Int) = dao.deleteSessionById(sessionId)
}
