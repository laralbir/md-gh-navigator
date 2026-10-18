package com.laralbir.mdghnavigator.core.common.event

enum class LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR
}

sealed interface AppEvent {
    data class FileChanged(
        val sourceId: String,
        val filePath: String
    ) : AppEvent

    data class SyncProgress(
        val sourceId: String,
        val progress: Float,
        val statusMessage: String
    ) : AppEvent

    data class LogMessage(
        val level: LogLevel,
        val tag: String,
        val message: String
    ) : AppEvent

    data class AuthStateChanged(
        val isAuthenticated: Boolean,
        val userLogin: String?
    ) : AppEvent
}

