package com.example.exampractisehelper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exampractisehelper.data.entities.PracticeSession
import com.example.exampractisehelper.data.entities.Task
import com.example.exampractisehelper.data.entities.Subtask
import com.example.exampractisehelper.data.repository.SessionCreationRepository
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

