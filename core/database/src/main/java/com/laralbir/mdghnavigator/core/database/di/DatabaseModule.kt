package com.laralbir.mdghnavigator.core.database.di

import android.content.Context
import androidx.room.Room
import com.laralbir.mdghnavigator.core.database.AppDatabase
import com.laralbir.mdghnavigator.core.database.dao.AnnotationDao
import com.laralbir.mdghnavigator.core.database.dao.DocumentDao
import com.laralbir.mdghnavigator.core.database.dao.RepositoryDao
import com.laralbir.mdghnavigator.core.database.dao.TocDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "md_gh_navigator.db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideRepositoryDao(database: AppDatabase): RepositoryDao = database.repositoryDao()

    @Provides
    fun provideDocumentDao(database: AppDatabase): DocumentDao = database.documentDao()

    @Provides
    fun provideAnnotationDao(database: AppDatabase): AnnotationDao = database.annotationDao()

    @Provides
    fun provideTocDao(database: AppDatabase): TocDao = database.tocDao()
}
