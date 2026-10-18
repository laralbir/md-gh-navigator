# Key APIs and Interface Reference — MD GitHub Navigator

This document compiles the core contracts and interfaces connecting the domain, data, and presentation layers.

---

## 1. Domain Layer: Repository Contracts

### `RepositorySourceRepository`
```kotlin
package com.laralbir.mdghnavigator.domain.repository

import com.laralbir.mdghnavigator.domain.model.RepositorySource
import com.laralbir.mdghnavigator.domain.model.DocumentNode
import kotlinx.coroutines.flow.Flow

interface RepositorySourceRepository {
    fun getTrackedSources(): Flow<List<RepositorySource>>
    suspend fun addRemoteSource(owner: String, repo: String, branch: String?, startPath: String): Result<RepositorySource>
    suspend fun addLocalSource(uri: String, displayName: String, startPath: String): Result<RepositorySource>
    suspend fun removeSource(id: String): Result<Unit>
    suspend fun getDocumentTree(sourceId: String): Result<DocumentNode>
}
```

### `DocumentContentRepository`
```kotlin
package com.laralbir.mdghnavigator.domain.repository

import com.laralbir.mdghnavigator.domain.model.DocumentContent
import kotlinx.coroutines.flow.Flow

interface DocumentContentRepository {
    suspend fun getDocument(sourceId: String, relativePath: String): Result<DocumentContent>
    suspend fun saveReadingProgress(sourceId: String, documentPath: String, scrollPercentage: Float)
    fun observeReadingProgress(sourceId: String, documentPath: String): Flow<Float?>
}
```

---

## 2. Reactive Event Bus (Event-Driven)

```kotlin
package com.laralbir.mdghnavigator.core.common.event

import kotlinx.coroutines.flow.SharedFlow

sealed interface AppEvent {
    data class FileChanged(val sourceId: String, val filePath: String) : AppEvent
    data class SyncProgress(val sourceId: String, val progress: Float, val statusMessage: String) : AppEvent
    data class LogMessage(val level: LogLevel, val tag: String, val message: String) : AppEvent
    data class AuthStateChanged(val isAuthenticated: Boolean, val userLogin: String?) : AppEvent
}

interface EventBus {
    val events: SharedFlow<AppEvent>
    suspend fun publish(event: AppEvent)
}
```

---

## 3. Rendering Engine Contracts

```kotlin
package com.laralbir.mdghnavigator.core.renderer

interface MarkdownRenderer {
    fun renderToHtml(markdownSource: String, baseAssetsUrl: String, options: RenderOptions): String
}

data class RenderOptions(
    val theme: ReaderTheme,
    val fontSizeSp: Int,
    val enableMermaid: Boolean = true,
    val enableGraphviz: Boolean = true,
    val enableD2: Boolean = true,
    val enableExcalidraw: Boolean = true,
    val enableKaTeX: Boolean = true
)
```
