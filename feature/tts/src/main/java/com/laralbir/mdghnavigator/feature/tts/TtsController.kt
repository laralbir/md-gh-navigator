package com.laralbir.mdghnavigator.feature.tts

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.SupervisorJob
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Controller for managing the lifecycle, playback state, and queue of Text-to-Speech synthesis.
 */
@Singleton
class TtsController(
    private val speechEngine: TtsSpeechEngine,
    private val coroutineScope: CoroutineScope
) {

    @Inject
    constructor(speechEngine: TtsSpeechEngine) : this(
        speechEngine = speechEngine,
        coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    )

    private val _uiState = MutableStateFlow(TtsState())
    val uiState: StateFlow<TtsState> = _uiState.asStateFlow()

    init {
        speechEngine.setUtteranceProgressListener(
            onDone = { utteranceId ->
                coroutineScope.launch {
                    handleUtteranceDone(utteranceId)
                }
            },
            onError = { errorMessage ->
                coroutineScope.launch {
                    _uiState.update {
                        it.copy(
                            status = TtsPlaybackStatus.ERROR,
                            errorMessage = errorMessage
                        )
                    }
                }
            }
        )
    }

    /**
     * Parses the given [markdown] text and prepares the sentences queue for speech.
     */
    fun loadContent(markdown: String) {
        speechEngine.stop()
        val sentences = MarkdownSpeechCleaner.extractSpeechSentences(markdown)
        _uiState.update {
            it.copy(
                sentences = sentences,
                currentSentenceIndex = 0,
                status = TtsPlaybackStatus.IDLE,
                errorMessage = null
            )
        }
    }

    /**
     * Ensures the speech engine is initialized and populates available languages and voices.
     */
    fun ensureInitialized(onReady: () -> Unit = {}) {
        if (_uiState.value.isInitialized) {
            onReady()
            return
        }

        speechEngine.initialize(
            onInitSuccess = {
                val languages = speechEngine.getAvailableLanguages()
                val currentLang = speechEngine.getCurrentLanguage() ?: Locale.getDefault()
                val voices = speechEngine.getAvailableVoices(currentLang)
                val currentVoice = speechEngine.getCurrentVoice()
                _uiState.update {
                    it.copy(
                        isInitialized = true,
                        availableLanguages = languages,
                        selectedLanguage = currentLang,
                        availableVoices = voices,
                        selectedVoice = currentVoice
                    )
                }
                onReady()
            },
            onInitError = { errorMsg ->
                _uiState.update {
                    it.copy(
                        status = TtsPlaybackStatus.ERROR,
                        errorMessage = errorMsg
                    )
                }
            }
        )
    }

    /**
     * Starts or resumes speech playback.
     */
    fun play() {
        val currentState = _uiState.value
        if (currentState.sentences.isEmpty()) return

        if (!currentState.isInitialized) {
            ensureInitialized {
                speakCurrentSentence()
            }
        } else {
            speakCurrentSentence()
        }
    }

    /**
     * Pauses speech playback at the current sentence.
     */
    fun pause() {
        speechEngine.stop()
        _uiState.update { it.copy(status = TtsPlaybackStatus.PAUSED) }
    }

    /**
     * Stops speech playback and resets reading position.
     */
    fun stop() {
        speechEngine.stop()
        _uiState.update {
            it.copy(
                status = TtsPlaybackStatus.STOPPED,
                currentSentenceIndex = 0
            )
        }
    }

    /**
     * Advances to the next sentence in the document.
     */
    fun nextSentence() {
        val currentState = _uiState.value
        val nextIndex = currentState.currentSentenceIndex + 1
        if (nextIndex < currentState.sentences.size) {
            _uiState.update { it.copy(currentSentenceIndex = nextIndex) }
            if (currentState.isPlaying) {
                speakCurrentSentence()
            }
        } else {
            stop()
        }
    }

    /**
     * Jumps back to the previous sentence in the document.
     */
    fun previousSentence() {
        val currentState = _uiState.value
        val prevIndex = (currentState.currentSentenceIndex - 1).coerceAtLeast(0)
        _uiState.update { it.copy(currentSentenceIndex = prevIndex) }
        if (currentState.isPlaying) {
            speakCurrentSentence()
        }
    }

    /**
     * Configures the speech playback speed rate.
     */
    fun setSpeechRate(rate: Float) {
        _uiState.update { it.copy(speechRate = rate) }
        if (_uiState.value.isPlaying) {
            speakCurrentSentence()
        }
    }

    /**
     * Configures the active language for speech synthesis and refreshes available voices.
     */
    fun setLanguage(locale: Locale) {
        speechEngine.setLanguage(locale)
        val voices = speechEngine.getAvailableVoices(locale)
        val currentVoice = speechEngine.getCurrentVoice()
        _uiState.update {
            it.copy(
                selectedLanguage = locale,
                availableVoices = voices,
                selectedVoice = currentVoice
            )
        }
        if (_uiState.value.isPlaying) {
            speakCurrentSentence()
        }
    }

    /**
     * Configures the active voice for speech synthesis.
     */
    fun setVoice(voiceName: String) {
        speechEngine.setVoice(voiceName)
        _uiState.update {
            it.copy(selectedVoice = voiceName)
        }
        if (_uiState.value.isPlaying) {
            speakCurrentSentence()
        }
    }

    /**
     * Releases TTS resources.
     */
    fun release() {
        speechEngine.stop()
        speechEngine.shutdown()
        _uiState.update {
            it.copy(
                status = TtsPlaybackStatus.STOPPED,
                isInitialized = false
            )
        }
    }

    private fun speakCurrentSentence() {
        val state = _uiState.value
        val sentence = state.currentSentence ?: return
        val utteranceId = "utt_${state.currentSentenceIndex}"

        _uiState.update { it.copy(status = TtsPlaybackStatus.PLAYING) }
        speechEngine.speak(
            text = sentence,
            utteranceId = utteranceId,
            speechRate = state.speechRate,
            pitch = state.pitch
        )
    }

    private fun handleUtteranceDone(utteranceId: String) {
        val currentState = _uiState.value
        if (!currentState.isPlaying) return

        val nextIndex = currentState.currentSentenceIndex + 1
        if (nextIndex < currentState.sentences.size) {
            _uiState.update { it.copy(currentSentenceIndex = nextIndex) }
            speakCurrentSentence()
        } else {
            _uiState.update { it.copy(status = TtsPlaybackStatus.STOPPED) }
        }
    }
}
