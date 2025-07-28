package com.example.exampractisehelper.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.exampractisehelper.data.entities.Exam
import com.example.exampractisehelper.data.entities.ExamType
import com.example.exampractisehelper.data.entities.PracticeSession
import com.example.exampractisehelper.data.entities.Task
import com.example.exampractisehelper.data.entities.Subtask
import com.example.exampractisehelper.data.entities.Settings
import com.example.exampractisehelper.data.dao.ExamDao
import com.example.exampractisehelper.data.dao.ExamTypeDao
import com.example.exampractisehelper.data.dao.PracticeSessionDao
import com.example.exampractisehelper.data.dao.TaskDao
import com.example.exampractisehelper.data.dao.SubTaskDao
import com.example.exampractisehelper.data.dao.SettingsDao
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `settings` (
                `id` INTEGER NOT NULL PRIMARY KEY,
                `alertType` TEXT NOT NULL
            )
        """)
    }
}

@Database(
    entities = [Exam::class, ExamType::class, PracticeSession::class, Task::class, Subtask::class, Settings::class],
    version = 3 // Incremented version for schema change
)
abstract class PracticeDatabase : RoomDatabase() {
    abstract fun examDao(): ExamDao
    abstract fun examTypeDao(): ExamTypeDao
    abstract fun practiceSessionDao(): PracticeSessionDao
    abstract fun taskDao(): TaskDao
    abstract fun subTaskDao(): SubTaskDao
    abstract fun settingsDao(): SettingsDao
}
