package com.example.exampractisehelper.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.exampractisehelper.data.entities.Exam
import com.example.exampractisehelper.data.entities.ExamType
import com.example.exampractisehelper.data.dao.ExamDao
import com.example.exampractisehelper.data.dao.ExamTypeDao

@Database(entities = [Exam::class, ExamType::class], version = 1)
abstract class PracticeDatabase : RoomDatabase() {
    abstract fun examDao(): ExamDao
    abstract fun examTypeDao(): ExamTypeDao
}

