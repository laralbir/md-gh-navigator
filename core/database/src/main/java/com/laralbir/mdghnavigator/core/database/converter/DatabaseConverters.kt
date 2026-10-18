package com.laralbir.mdghnavigator.core.database.converter

import androidx.room.TypeConverter
import com.laralbir.mdghnavigator.core.database.entity.AnnotationType
import com.laralbir.mdghnavigator.core.database.entity.RepositoryType

class DatabaseConverters {

    @TypeConverter
    fun fromRepositoryType(type: RepositoryType): String = type.name

    @TypeConverter
    fun toRepositoryType(name: String): RepositoryType {
        return runCatching { RepositoryType.valueOf(name) }.getOrDefault(RepositoryType.GITHUB)
    }

    @TypeConverter
    fun fromAnnotationType(type: AnnotationType): String = type.name

    @TypeConverter
    fun toAnnotationType(name: String): AnnotationType {
        return runCatching { AnnotationType.valueOf(name) }.getOrDefault(AnnotationType.HIGHLIGHT)
    }
}
