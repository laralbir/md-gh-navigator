package com.laralbir.mdghnavigator.core.database.converter

import androidx.room.TypeConverter
import com.laralbir.mdghnavigator.core.database.entity.RepositoryType

class DatabaseConverters {

    @TypeConverter
    fun fromRepositoryType(type: RepositoryType): String = type.name

    @TypeConverter
    fun toRepositoryType(name: String): RepositoryType {
        return runCatching { RepositoryType.valueOf(name) }.getOrDefault(RepositoryType.GITHUB)
    }
}
