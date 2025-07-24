package com.example.exampractisehelper.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.exampractisehelper.data.entities.Exam

@Dao
interface ExamDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: Exam): Long

    @Query("SELECT * FROM exams")
    suspend fun getAllExams(): List<Exam>

    @Query("SELECT * FROM exams WHERE examId = :examId LIMIT 1")
    suspend fun getExamById(examId: Int): Exam?

    @Update
    suspend fun updateExam(exam: Exam)

    @Delete
    suspend fun deleteExam(exam: Exam)

    @Query("UPDATE exams SET name = :newName WHERE examId = :examId")
    fun updateExamName(examId: Int, newName: String)
}
