package com.laralbir.mdghnavigator.feature.tts

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production implementation of [TtsSpeechEngine] wrapping Android's native [TextToSpeech] engine.
 */
@Singleton
class AndroidTtsSpeechEngine @Inject constructor(
    @ApplicationContext private val context: Context
) : TtsSpeechEngine {

    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

    private var onDoneListener: ((String) -> Unit)? = null
    private var onErrorListener: ((String) -> Unit)? = null

    override fun initialize(onInitSuccess: () -> Unit, onInitError: (String) -> Unit) {
        if (isInitialized && textToSpeech != null) {
            onInitSuccess()
            return
        }

        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                val tts = textToSpeech
                if (tts != null) {
                    val defaultLocale = Locale.getDefault()
                    val langResult = tts.setLanguage(defaultLocale)
                    if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // Fallback to English
                        tts.setLanguage(Locale.ENGLISH)
                    }

                    tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            // Utterance started
                        }

                        override fun onDone(utteranceId: String?) {
                            utteranceId?.let { onDoneListener?.invoke(it) }
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {
                            onError(utteranceId, TextToSpeech.ERROR)
                        }

                        override fun onError(utteranceId: String?, errorCode: Int) {
                            onErrorListener?.invoke("TTS Error ($errorCode) for utterance: $utteranceId")
                        }
                    })
                }
                onInitSuccess()
            } else {
                isInitialized = false
                onInitError("TextToSpeech initialization failed with status: $status")
            }
        }
    }

    override fun speak(text: String, utteranceId: String, speechRate: Float, pitch: Float) {
        val tts = textToSpeech ?: return
        if (!isInitialized) return

        tts.setSpeechRate(speechRate)
        tts.setPitch(pitch)

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    override fun stop() {
        textToSpeech?.stop()
    }

    override fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        isInitialized = false
    }

    override fun setUtteranceProgressListener(onDone: (String) -> Unit, onError: (String) -> Unit) {
        this.onDoneListener = onDone
        this.onErrorListener = onError
    }
}
