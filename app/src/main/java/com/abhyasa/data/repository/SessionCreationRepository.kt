package com.abhyasa.data.repository

import androidx.room.Transaction
import com.abhyasa.data.entities.PracticeSession
import com.abhyasa.data.entities.Task
import com.abhyasa.data.entities.Subtask

class SessionCreationRepository(
    private val sessionRepo: PracticeSessionRepository,
    private val taskRepo: TaskRepository,
    private val subtaskRepo: SubtaskRepository
) {
    @Transaction
    suspend fun createFullSession(
        session: PracticeSession,
        tasksWithSubtasks: List<Pair<Task, List<Subtask>>>
    ): Long {
        val sessionId = sessionRepo.insertSession(session).toInt()
        for ((task, subtasks) in tasksWithSubtasks) {
            val taskId = taskRepo.insertTask(task.copy(sessionId = sessionId)).toInt()
            for (subtask in subtasks) {
                subtaskRepo.insertSubtask(subtask.copy(taskId = taskId))
            }
        }
        return sessionId.toLong()
    }
}

