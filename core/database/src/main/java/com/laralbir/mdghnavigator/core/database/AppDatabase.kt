package com.laralbir.mdghnavigator.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.laralbir.mdghnavigator.core.database.converter.DatabaseConverters
import com.laralbir.mdghnavigator.core.database.dao.AnnotationDao
import com.laralbir.mdghnavigator.core.database.dao.DocumentDao
import com.laralbir.mdghnavigator.core.database.dao.RepositoryDao
import com.laralbir.mdghnavigator.core.database.dao.TocDao
import com.laralbir.mdghnavigator.core.database.entity.AnnotationEntity
import com.laralbir.mdghnavigator.core.database.entity.DocumentEntity
import com.laralbir.mdghnavigator.core.database.entity.RepositoryEntity
import com.laralbir.mdghnavigator.core.database.entity.TocItemEntity

@Database(
    entities = [
        RepositoryEntity::class,
        DocumentEntity::class,
        AnnotationEntity::class,
        TocItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repositoryDao(): RepositoryDao
    abstract fun documentDao(): DocumentDao
    abstract fun annotationDao(): AnnotationDao
    abstract fun tocDao(): TocDao
}
