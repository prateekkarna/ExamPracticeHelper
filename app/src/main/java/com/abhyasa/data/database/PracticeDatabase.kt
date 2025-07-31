package com.abhyasa.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.abhyasa.data.dao.ExamDao
import com.abhyasa.data.dao.ExamTypeDao
import com.abhyasa.data.dao.PracticeSessionDao
import com.abhyasa.data.dao.TaskDao
import com.abhyasa.data.dao.SubTaskDao
import com.abhyasa.data.dao.SettingsDao
import com.abhyasa.data.entities.Exam
import com.abhyasa.data.entities.ExamType
import com.abhyasa.data.entities.PracticeSession
import com.abhyasa.data.entities.Task
import com.abhyasa.data.entities.Subtask
import com.abhyasa.data.entities.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

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

// Data classes for JSON parsing
private data class PredefinedSubtask(val subtaskName: String, val duration: Int)
private data class PredefinedTask(
    val taskName: String,
    val hasSubtasks: Boolean,
    val taskDuration: Int?,
    val typeLabel: String?,
    val subtasks: List<PredefinedSubtask>
)
private data class PredefinedSession(
    val sessionName: String,
    val isTimed: Boolean,
    val totalDuration: Int?,
    val tasks: List<PredefinedTask>
)

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

    companion object {
        fun buildDatabase(context: Context): PracticeDatabase {
            return androidx.room.Room.databaseBuilder(
                context.applicationContext,
                PracticeDatabase::class.java,
                "exam_practise_helper_db_v2"
            )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Insert predefined sessions in background
                    CoroutineScope(Dispatchers.IO).launch {
                        val database = getInstance(context)
                        val sessionDao = database.practiceSessionDao()
                        val taskDao = database.taskDao()
                        val subTaskDao = database.subTaskDao()
                        val sessions = loadPredefinedSessions(context)
                        sessions.forEach { session ->
                            val sessionEntity = PracticeSession(
                                name = session.sessionName,
                                isTimed = session.isTimed,
                                totalDuration = session.totalDuration
                            )
                            val sessionId = sessionDao.insert(sessionEntity).toInt()
                            val sessionWithId = sessionEntity.copy(sessionId = sessionId)
                            session.tasks.forEach { task ->
                                val taskEntity = Task(
                                    sessionId = sessionId,
                                    text = task.taskName,
                                    hasSubtasks = task.hasSubtasks,
                                    taskDuration = task.taskDuration,
                                    typeLabel = task.typeLabel ?: ""
                                )
                                val taskId = taskDao.insert(taskEntity).toInt()
                                val taskWithId = taskEntity.copy(taskId = taskId)
                                task.subtasks.forEach { subtask ->
                                    val subtaskEntity = Subtask(
                                        taskId = taskId,
                                        name = subtask.subtaskName,
                                        duration = subtask.duration
                                    )
                                    val subtaskId = subTaskDao.insert(subtaskEntity)
                                    val subtaskWithId = subtaskEntity.copy(subtaskId = subtaskId.toInt())
                                }
                            }
                        }
                    }
                }
            })
            .build()
        }
        @Volatile private var INSTANCE: PracticeDatabase? = null
        fun getInstance(context: Context): PracticeDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }
        private fun loadPredefinedSessions(context: Context): List<PredefinedSession> {
            val sessions = mutableListOf<PredefinedSession>()
            try {
                context.assets.open("predefined_sessions.json").use { inputStream ->
                    val json = inputStream.bufferedReader().use { it.readText() }
                    val arr = JSONArray(json)
                    for (i in 0 until arr.length()) {
                        val obj = arr.getJSONObject(i)
                        val tasksArr = obj.optJSONArray("tasks") ?: JSONArray()
                        val tasks = mutableListOf<PredefinedTask>()
                        for (j in 0 until tasksArr.length()) {
                            val taskObj = tasksArr.getJSONObject(j)
                            val subtasksArr = taskObj.optJSONArray("subtasks") ?: JSONArray()
                            val subtasks = mutableListOf<PredefinedSubtask>()
                            for (k in 0 until subtasksArr.length()) {
                                val subObj = subtasksArr.getJSONObject(k)
                                subtasks.add(
                                    PredefinedSubtask(
                                        subtaskName = subObj.getString("subtaskName"),
                                        duration = subObj.optInt("duration", 0)
                                    )
                                )
                            }
                            tasks.add(
                                PredefinedTask(
                                    taskName = taskObj.getString("taskName"),
                                    hasSubtasks = taskObj.optBoolean("hasSubtasks", false),
                                    taskDuration = if (taskObj.isNull("taskDuration")) null else taskObj.optInt("taskDuration"),
                                    typeLabel = taskObj.optString("typeLabel", null),
                                    subtasks = subtasks
                                )
                            )
                        }
                        sessions.add(
                            PredefinedSession(
                                sessionName = obj.getString("sessionName"),
                                isTimed = obj.optBoolean("isTimed", false),
                                totalDuration = if (obj.isNull("totalDuration")) null else obj.optInt("totalDuration"),
                                tasks = tasks
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return sessions
        }

        suspend fun ensurePreloadedData(context: Context) {
            val db = getInstance(context)
            val sessionDao = db.practiceSessionDao()
            val taskDao = db.taskDao()
            val subTaskDao = db.subTaskDao()
            if (sessionDao.getSessionCount() == 0) {
                val sessions = loadPredefinedSessions(context)
                sessions.forEach { session ->
                    val sessionEntity = PracticeSession(
                        name = session.sessionName,
                        isTimed = session.isTimed,
                        totalDuration = session.totalDuration
                    )
                    val sessionId = sessionDao.insert(sessionEntity).toInt()
                    session.tasks.forEach { task ->
                        val taskEntity = Task(
                            sessionId = sessionId,
                            text = task.taskName,
                            hasSubtasks = task.hasSubtasks,
                            taskDuration = task.taskDuration,
                            typeLabel = task.typeLabel ?: ""
                        )
                        val taskId = taskDao.insert(taskEntity).toInt()
                        task.subtasks.forEach { subtask ->
                            val subtaskEntity = Subtask(
                                taskId = taskId,
                                name = subtask.subtaskName,
                                duration = subtask.duration
                            )
                            subTaskDao.insert(subtaskEntity)
                        }
                    }
                }
            }
        }
    }
}
