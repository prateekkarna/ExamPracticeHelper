package com.example.exampractisehelper.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exampractisehelper.data.repository.ExamRepository
import com.example.exampractisehelper.data.entities.Exam
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ExamRepository) : ViewModel() {
    private val _exams = MutableStateFlow<List<Exam>>(emptyList())
    val exams: StateFlow<List<Exam>> = _exams.asStateFlow()

    fun loadExams() {
        viewModelScope.launch {
            _exams.value = repository.getAllExams()
        }
    }
}

