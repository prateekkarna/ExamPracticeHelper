package com.abhyasa.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhyasa.data.repository.ExamRepository
import com.abhyasa.data.repository.PracticeSessionRepository
import com.abhyasa.data.entities.Exam
import com.abhyasa.data.entities.PracticeSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val examRepository: ExamRepository,
    private val sessionRepository: PracticeSessionRepository
) : ViewModel() {
    private val _exams = MutableStateFlow<List<Exam>>(emptyList())
    val exams: StateFlow<List<Exam>> = _exams.asStateFlow()

    private val _sessions = MutableStateFlow<List<PracticeSession>>(emptyList())
    val sessions: StateFlow<List<PracticeSession>> = _sessions.asStateFlow()

    fun loadExams() {
        viewModelScope.launch {
            _exams.value = examRepository.getAllExams()
        }
    }

    fun loadSessions() {
        viewModelScope.launch {
            _sessions.value = sessionRepository.getAllSessions()
        }
    }
}
