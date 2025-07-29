package com.abhyasa.di

import android.content.Context
import androidx.room.Room
import com.abhyasa.data.dao.PracticeSessionDao
import com.abhyasa.data.dao.TaskDao
import com.abhyasa.data.dao.SubTaskDao
import com.abhyasa.data.repository.*
import com.abhyasa.data.database.PracticeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PracticeDatabase =
        Room.databaseBuilder(
            context,
            PracticeDatabase::class.java,
            "exam_practise_helper_db_v2" // Changed name to force new DB file
        )
        .fallbackToDestructiveMigration()
        .fallbackToDestructiveMigrationOnDowngrade()
        .build()

    @Provides
    fun providePracticeSessionDao(db: PracticeDatabase): PracticeSessionDao = db.practiceSessionDao()

    @Provides
    fun provideTaskDao(db: PracticeDatabase): TaskDao = db.taskDao()

    @Provides
    fun provideSubTaskDao(db: PracticeDatabase): SubTaskDao = db.subTaskDao()

    @Provides
    fun providePracticeSessionRepository(dao: PracticeSessionDao): PracticeSessionRepository =
        PracticeSessionRepositoryImpl(dao)

    @Provides
    fun provideTaskRepository(dao: TaskDao): TaskRepository = TaskRepositoryImpl(dao)

    @Provides
    fun provideSubtaskRepository(dao: SubTaskDao): SubtaskRepository = SubtaskRepositoryImpl(dao)

    @Provides
    fun provideSessionCreationRepository(
        sessionRepo: PracticeSessionRepository,
        taskRepo: TaskRepository,
        subtaskRepo: SubtaskRepository
    ): SessionCreationRepository =
        SessionCreationRepository(sessionRepo, taskRepo, subtaskRepo)
}
