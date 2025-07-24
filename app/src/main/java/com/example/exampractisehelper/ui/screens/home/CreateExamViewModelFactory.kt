package com.example.exampractisehelper.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.exampractisehelper.data.repository.ExamRepository

class CreateExamViewModelFactory(private val repository: ExamRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateExamViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateExamViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

