package com.laralbir.mdghnavigator.feature.tts

import java.util.Locale

/**
 * Status of TTS audio playback.
 */
enum class TtsPlaybackStatus {
    IDLE,
    PLAYING,
    PAUSED,
    STOPPED,
    ERROR
}

/**
 * State representing the active Text-to-Speech playback session.
 */
data class TtsState(
    val status: TtsPlaybackStatus = TtsPlaybackStatus.IDLE,
    val currentSentenceIndex: Int = 0,
    val sentences: List<String> = emptyList(),
    val speechRate: Float = 1.0f,
    val pitch: Float = 1.0f,
    val isInitialized: Boolean = false,
    val errorMessage: String? = null,
    val availableLanguages: List<Locale> = emptyList(),
    val selectedLanguage: Locale? = null,
    val availableVoices: List<TtsVoiceInfo> = emptyList(),
    val selectedVoice: String? = null
) {
    val currentSentence: String?
        get() = sentences.getOrNull(currentSentenceIndex)

    val totalSentences: Int
        get() = sentences.size

    val progress: Float
        get() = if (totalSentences > 0) (currentSentenceIndex + 1).toFloat() / totalSentences.toFloat() else 0f

    val isPlaying: Boolean
        get() = status == TtsPlaybackStatus.PLAYING

    val isPaused: Boolean
        get() = status == TtsPlaybackStatus.PAUSED

    val isVisible: Boolean
        get() = status != TtsPlaybackStatus.IDLE && status != TtsPlaybackStatus.STOPPED
}
