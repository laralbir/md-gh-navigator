package com.laralbir.mdghnavigator.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.laralbir.mdghnavigator.core.database.entity.AnnotationEntity
import com.laralbir.mdghnavigator.core.database.entity.AnnotationType
import kotlinx.coroutines.flow.Flow

@Dao
interface AnnotationDao {

    @Query("SELECT * FROM annotations WHERE documentId = :documentId ORDER BY scrollY ASC, startOffset ASC")
    fun getAnnotationsForDocument(documentId: String): Flow<List<AnnotationEntity>>

    @Query("SELECT * FROM annotations WHERE repoId = :repoId ORDER BY updatedAt DESC")
    fun getAnnotationsForRepo(repoId: String): Flow<List<AnnotationEntity>>

    @Query("SELECT * FROM annotations WHERE documentId = :documentId AND type = :type ORDER BY scrollY ASC, startOffset ASC")
    fun getAnnotationsForDocumentByType(documentId: String, type: AnnotationType): Flow<List<AnnotationEntity>>

    @Upsert
    suspend fun upsertAnnotation(annotation: AnnotationEntity)

    @Query("DELETE FROM annotations WHERE id = :id")
    suspend fun deleteAnnotation(id: String)

    @Query("DELETE FROM annotations WHERE documentId = :documentId")
    suspend fun deleteAnnotationsForDocument(documentId: String)
}
