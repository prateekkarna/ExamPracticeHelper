package com.example.exampractisehelper.data.repository

import com.example.exampractisehelper.data.entities.Exam
import com.example.exampractisehelper.data.entities.ExamType
import com.example.exampractisehelper.data.dao.ExamDao
import com.example.exampractisehelper.data.dao.ExamTypeDao

class ExamRepository(
    private val examDao: ExamDao,
    private val examTypeDao: ExamTypeDao
) {
    suspend fun insertExam(exam: Exam): Long = examDao.insertExam(exam)
    suspend fun insertExamType(examType: ExamType) = examTypeDao.insertExamType(examType)
    suspend fun getAllExams(): List<Exam> = examDao.getAllExams()
    suspend fun getExamById(examId: Int): Exam? = examDao.getExamById(examId)
    suspend fun updateExam(exam: Exam) = examDao.updateExam(exam)
    suspend fun deleteExam(exam: Exam) = examDao.deleteExam(exam)
    suspend fun getExamTypesForExam(examId: Int): List<ExamType> = examTypeDao.getExamTypesForExam(examId)
    fun updateExamName(examId: Int, newName: String) = examDao.updateExamName(examId, newName)
    suspend fun deleteExamTypesForExam(examId: Int) = examTypeDao.deleteExamTypesForExam(examId)
}
