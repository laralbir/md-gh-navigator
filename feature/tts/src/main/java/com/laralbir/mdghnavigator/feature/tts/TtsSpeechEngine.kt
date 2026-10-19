package com.laralbir.mdghnavigator.feature.tts

import java.util.Locale

/**
 * Information describing an individual voice option available in the TTS engine.
 */
data class TtsVoiceInfo(
    val name: String,
    val displayName: String,
    val locale: Locale,
    val requiresNetwork: Boolean = false
)

/**
 * Contract for a Text-to-Speech synthesis engine.
 * Allows decoupling Android's native TextToSpeech engine from presentation and business logic.
 */
interface TtsSpeechEngine {
    /**
     * Initializes the TTS engine.
     *
     * @param onInitSuccess callback invoked when TTS is initialized and ready
     * @param onInitError callback invoked with an error description if initialization fails
     */
    fun initialize(onInitSuccess: () -> Unit, onInitError: (String) -> Unit)

    /**
     * Synthesizes and speaks [text] aloud.
     *
     * @param text content to speak
     * @param utteranceId unique identifier for this speech segment
     * @param speechRate speech speed multiplier (e.g. 1.0f for normal speed)
     * @param pitch speech pitch multiplier (e.g. 1.0f for normal pitch)
     */
    fun speak(text: String, utteranceId: String, speechRate: Float, pitch: Float)

    /**
     * Stops current speech output immediately.
     */
    fun stop()

    /**
     * Shuts down and releases the underlying TTS resources.
     */
    fun shutdown()

    /**
     * Sets progress listener for tracking speech completions or errors.
     */
    fun setUtteranceProgressListener(onDone: (String) -> Unit, onError: (String) -> Unit)

    /**
     * Returns the list of available languages installed or supported by the TTS engine.
     */
    fun getAvailableLanguages(): List<Locale> = emptyList()

    /**
     * Returns the list of available voices for the specified [locale] (or all voices if null).
     */
    fun getAvailableVoices(locale: Locale? = null): List<TtsVoiceInfo> = emptyList()

    /**
     * Sets the active language for speech synthesis.
     *
     * @return true if the language was successfully set or is supported
     */
    fun setLanguage(locale: Locale): Boolean = false

    /**
     * Sets the active voice by its unique identifier [voiceName].
     *
     * @return true if the voice was successfully selected
     */
    fun setVoice(voiceName: String): Boolean = false

    /**
     * Returns the currently configured locale for speech synthesis.
     */
    fun getCurrentLanguage(): Locale? = null

    /**
     * Returns the name of the currently configured voice, if any.
     */
    fun getCurrentVoice(): String? = null
}
