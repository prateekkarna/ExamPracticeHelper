package com.abhyasa.ui.screens.createexam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abhyasa.data.repository.ExamRepository

class CreateExamViewModelFactory(private val repository: ExamRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateExamViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateExamViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

