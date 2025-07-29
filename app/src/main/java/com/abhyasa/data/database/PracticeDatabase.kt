package com.abhyasa.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.abhyasa.data.entities.Exam
import com.abhyasa.data.entities.ExamType
import com.abhyasa.data.entities.PracticeSession
import com.abhyasa.data.entities.Task
import com.abhyasa.data.entities.Subtask
import com.abhyasa.data.entities.Settings
import com.abhyasa.data.dao.ExamDao
import com.abhyasa.data.dao.ExamTypeDao
import com.abhyasa.data.dao.PracticeSessionDao
import com.abhyasa.data.dao.TaskDao
import com.abhyasa.data.dao.SubTaskDao
import com.abhyasa.data.dao.SettingsDao
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
