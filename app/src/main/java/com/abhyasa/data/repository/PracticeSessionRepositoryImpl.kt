package com.abhyasa.data.repository

import com.abhyasa.data.entities.PracticeSession
import com.abhyasa.data.dao.PracticeSessionDao

class PracticeSessionRepositoryImpl(private val dao: PracticeSessionDao) : PracticeSessionRepository {
    override suspend fun insertSession(session: PracticeSession): Long = dao.insert(session)
    override suspend fun getAllSessions(): List<PracticeSession> = dao.getAllSessions()
    override suspend fun deleteSessionById(sessionId: Int) = dao.deleteSessionById(sessionId)
}
