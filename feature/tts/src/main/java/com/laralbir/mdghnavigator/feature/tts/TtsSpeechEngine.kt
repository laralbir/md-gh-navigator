package com.laralbir.mdghnavigator.feature.tts

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
}
