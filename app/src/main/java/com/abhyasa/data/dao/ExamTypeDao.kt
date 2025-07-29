package com.abhyasa.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abhyasa.data.entities.ExamType

@Dao
interface ExamTypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamType(examType: ExamType): Long

    @Query("SELECT * FROM exam_types WHERE examId = :examId")
    suspend fun getExamTypesForExam(examId: Int): List<ExamType>

    @Query("DELETE FROM exam_types WHERE examId = :examId")
    suspend fun deleteExamTypesForExam(examId: Int)
}
