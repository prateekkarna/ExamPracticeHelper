package com.example.exampractisehelper.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.exampractisehelper.data.entities.Exam
import com.example.exampractisehelper.data.entities.ExamType
import com.example.exampractisehelper.data.entities.PracticeSession
import com.example.exampractisehelper.data.entities.Task
import com.example.exampractisehelper.data.entities.Subtask
import com.example.exampractisehelper.data.dao.ExamDao
import com.example.exampractisehelper.data.dao.ExamTypeDao
import com.example.exampractisehelper.data.dao.PracticeSessionDao
import com.example.exampractisehelper.data.dao.TaskDao
import com.example.exampractisehelper.data.dao.SubTaskDao

@Database(
    entities = [Exam::class, ExamType::class, PracticeSession::class, Task::class, Subtask::class],
    version = 2
)
abstract class PracticeDatabase : RoomDatabase() {
    abstract fun examDao(): ExamDao
    abstract fun examTypeDao(): ExamTypeDao
    abstract fun practiceSessionDao(): PracticeSessionDao
    abstract fun taskDao(): TaskDao
    abstract fun subTaskDao(): SubTaskDao
}
