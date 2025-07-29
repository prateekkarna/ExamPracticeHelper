package com.abhyasa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhyasa.data.entities.PracticeSession
import com.abhyasa.data.entities.Task
import com.abhyasa.data.entities.Subtask
import com.abhyasa.data.repository.SessionCreationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateSessionViewModel @Inject constructor(
    private val sessionCreationRepository: SessionCreationRepository
) : ViewModel() {
    fun createSession(
        session: PracticeSession,
        tasksWithSubtasks: List<Pair<Task, List<Subtask>>>,
        onSuccess: (Long) -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val sessionId = sessionCreationRepository.createFullSession(session, tasksWithSubtasks)
                onSuccess(sessionId)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}

