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

    private var pendingLanguage: Locale? = null
    private var pendingVoiceName: String? = null

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
                    val targetLocale = pendingLanguage ?: Locale.getDefault()
                    val langResult = tts.setLanguage(targetLocale)
                    if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // Fallback to English
                        tts.setLanguage(Locale.ENGLISH)
                    }

                    pendingVoiceName?.let { vName ->
                        runCatching {
                            tts.voices?.find { it.name == vName }?.let { tts.voice = it }
                        }
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

    override fun getAvailableLanguages(): List<Locale> {
        val tts = textToSpeech ?: return emptyList()
        val engineLanguages = runCatching {
            tts.availableLanguages
                ?.filter { it.language.isNotBlank() && it.displayName.isNotBlank() }
                ?.distinctBy { it.language to it.country }
        }.getOrNull()

        if (!engineLanguages.isNullOrEmpty()) {
            return engineLanguages.sortedBy { it.displayName }
        }

        // Fallback: test common languages availability
        val fallbackLocales = listOf(
            Locale.forLanguageTag("es-ES"),
            Locale.forLanguageTag("es-MX"),
            Locale.US,
            Locale.UK,
            Locale.FRANCE,
            Locale.GERMANY,
            Locale.ITALY,
            Locale.forLanguageTag("pt-BR"),
            Locale.forLanguageTag("pt-PT"),
            Locale.CHINA,
            Locale.JAPAN
        )
        return fallbackLocales.filter { loc ->
            runCatching {
                tts.isLanguageAvailable(loc) >= TextToSpeech.LANG_AVAILABLE
            }.getOrDefault(false)
        }.sortedBy { it.displayName }
    }

    override fun getAvailableVoices(locale: Locale?): List<TtsVoiceInfo> {
        val tts = textToSpeech ?: return emptyList()
        return runCatching {
            val allVoices = tts.voices ?: emptySet()
            val filtered = if (locale != null) {
                allVoices.filter { it.locale.language.equals(locale.language, ignoreCase = true) }
            } else {
                allVoices.toList()
            }
            filtered.mapIndexed { index, voice ->
                val simpleName = voice.name
                    .split("-", "_")
                    .filter { it.isNotBlank() && it.length > 2 && it.lowercase() !in listOf("local", "network", "language") }
                    .lastOrNull()
                    ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                    ?: "Voz ${index + 1}"
                val display = if (voice.isNetworkConnectionRequired) "$simpleName (Online)" else simpleName
                TtsVoiceInfo(
                    name = voice.name,
                    displayName = display,
                    locale = voice.locale,
                    requiresNetwork = voice.isNetworkConnectionRequired
                )
            }
        }.getOrNull() ?: emptyList()
    }

    override fun setLanguage(locale: Locale): Boolean {
        pendingLanguage = locale
        val tts = textToSpeech ?: return false
        val result = runCatching { tts.setLanguage(locale) }.getOrDefault(TextToSpeech.LANG_NOT_SUPPORTED)
        return result >= TextToSpeech.LANG_AVAILABLE
    }

    override fun setVoice(voiceName: String): Boolean {
        pendingVoiceName = voiceName
        val tts = textToSpeech ?: return false
        return runCatching {
            val voice = tts.voices?.find { it.name == voiceName } ?: return false
            tts.voice = voice
            true
        }.getOrDefault(false)
    }

    override fun getCurrentLanguage(): Locale? {
        val tts = textToSpeech ?: return pendingLanguage
        return runCatching { tts.voice?.locale ?: tts.language ?: pendingLanguage }.getOrNull()
    }

    override fun getCurrentVoice(): String? {
        val tts = textToSpeech ?: return pendingVoiceName
        return runCatching { tts.voice?.name ?: pendingVoiceName }.getOrNull()
    }
}
