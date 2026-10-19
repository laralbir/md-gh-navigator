package com.laralbir.mdghnavigator.feature.tts

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class TtsControllerTest {

    private lateinit var fakeEngine: FakeTtsSpeechEngine
    private lateinit var controller: TtsController
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @BeforeEach
    fun setup() {
        fakeEngine = FakeTtsSpeechEngine()
        controller = TtsController(
            speechEngine = fakeEngine,
            coroutineScope = testScope
        )
    }

    @Test
    @DisplayName("loadContent parses markdown into sentences and sets initial state")
    fun testLoadContent() = testScope.runTest {
        val markdown = "# Hello\nThis is a test. Second line here."
        controller.loadContent(markdown)

        val state = controller.uiState.value
        assertEquals(3, state.totalSentences)
        assertEquals("Hello", state.currentSentence)
        assertEquals(TtsPlaybackStatus.IDLE, state.status)
        assertFalse(state.isPlaying)
    }

    @Test
    @DisplayName("play initializes engine and speaks the first sentence")
    fun testPlay() = testScope.runTest {
        controller.loadContent("First sentence. Second sentence.")
        controller.play()

        val state = controller.uiState.value
        assertEquals(TtsPlaybackStatus.PLAYING, state.status)
        assertTrue(state.isPlaying)
        assertEquals("First sentence.", fakeEngine.lastSpokenText)
        assertEquals(0, state.currentSentenceIndex)
    }

    @Test
    @DisplayName("completion of sentence advances to next sentence automatically")
    fun testUtteranceCompletionAdvancesSentence() = testScope.runTest {
        controller.loadContent("Sentence 1. Sentence 2.")
        controller.play()

        assertEquals("Sentence 1.", fakeEngine.lastSpokenText)

        // Simulate utterance completion
        fakeEngine.simulateUtteranceDone("utt_0")
        testScheduler.advanceUntilIdle()

        val state = controller.uiState.value
        assertEquals(1, state.currentSentenceIndex)
        assertEquals("Sentence 2.", fakeEngine.lastSpokenText)
        assertEquals(TtsPlaybackStatus.PLAYING, state.status)
    }

    @Test
    @DisplayName("completion of last sentence stops playback")
    fun testCompletionOfLastSentenceStops() = testScope.runTest {
        controller.loadContent("Only sentence.")
        controller.play()

        fakeEngine.simulateUtteranceDone("utt_0")
        testScheduler.advanceUntilIdle()

        val state = controller.uiState.value
        assertEquals(TtsPlaybackStatus.STOPPED, state.status)
        assertFalse(state.isPlaying)
    }

    @Test
    @DisplayName("nextSentence jumps forward and speaks")
    fun testNextSentence() = testScope.runTest {
        controller.loadContent("One. Two. Three.")
        controller.play()

        controller.nextSentence()

        val state = controller.uiState.value
        assertEquals(1, state.currentSentenceIndex)
        assertEquals("Two.", fakeEngine.lastSpokenText)
    }

    @Test
    @DisplayName("previousSentence jumps backward and speaks")
    fun testPreviousSentence() = testScope.runTest {
        controller.loadContent("One. Two. Three.")
        controller.play()
        controller.nextSentence() // at index 1

        controller.previousSentence()

        val state = controller.uiState.value
        assertEquals(0, state.currentSentenceIndex)
        assertEquals("One.", fakeEngine.lastSpokenText)
    }

    @Test
    @DisplayName("pause stops engine speech and transitions to PAUSED")
    fun testPause() = testScope.runTest {
        controller.loadContent("Playing something.")
        controller.play()
        controller.pause()

        val state = controller.uiState.value
        assertEquals(TtsPlaybackStatus.PAUSED, state.status)
        assertTrue(state.isPaused)
        assertTrue(fakeEngine.isStopped)
    }

    @Test
    @DisplayName("setSpeechRate updates rate and applies to subsequent speech")
    fun testSetSpeechRate() = testScope.runTest {
        controller.loadContent("Speech rate test.")
        controller.setSpeechRate(1.5f)

        assertEquals(1.5f, controller.uiState.value.speechRate)

        controller.play()
        assertEquals(1.5f, fakeEngine.lastSpeechRate)
    }

    @Test
    @DisplayName("stop resets state to STOPPED")
    fun testStop() = testScope.runTest {
        controller.loadContent("Testing stop.")
        controller.play()
        controller.stop()

        val state = controller.uiState.value
        assertEquals(TtsPlaybackStatus.STOPPED, state.status)
        assertTrue(fakeEngine.isStopped)
    }

    @Test
    @DisplayName("ensureInitialized populates available languages and voices")
    fun testEnsureInitializedPopulatesLanguagesAndVoices() = testScope.runTest {
        controller.ensureInitialized()

        val state = controller.uiState.value
        assertTrue(state.isInitialized)
        assertEquals(3, state.availableLanguages.size)
        assertEquals(Locale.US, state.selectedLanguage)
        assertEquals(1, state.availableVoices.size)
    }

    @Test
    @DisplayName("setLanguage updates speech engine and uiState with matching voices")
    fun testSetLanguage() = testScope.runTest {
        controller.loadContent("Texto en español.")
        controller.ensureInitialized()

        controller.setLanguage(Locale("es", "ES"))

        assertEquals(Locale("es", "ES"), fakeEngine.activeLanguage)
        val state = controller.uiState.value
        assertEquals(Locale("es", "ES"), state.selectedLanguage)
        assertEquals(1, state.availableVoices.size)
        assertEquals("es-voice-1", state.availableVoices.first().name)
    }

    @Test
    @DisplayName("setVoice updates speech engine and uiState")
    fun testSetVoice() = testScope.runTest {
        controller.loadContent("Voice selection test.")
        controller.ensureInitialized()

        controller.setVoice("es-voice-1")

        assertEquals("es-voice-1", fakeEngine.activeVoice)
        assertEquals("es-voice-1", controller.uiState.value.selectedVoice)
    }

    @Test
    @DisplayName("changing language or voice while playing restarts current sentence")
    fun testChangingLanguageWhilePlayingRestartsSpeech() = testScope.runTest {
        controller.loadContent("Sentence 1. Sentence 2.")
        controller.play()
        assertTrue(controller.uiState.value.isPlaying)

        controller.setLanguage(Locale("es", "ES"))

        assertTrue(controller.uiState.value.isPlaying)
        assertEquals("Sentence 1.", fakeEngine.lastSpokenText)
    }
}

class FakeTtsSpeechEngine : TtsSpeechEngine {
    var isInitialized = false
    var lastSpokenText: String? = null
    var lastSpeechRate: Float = 1.0f
    var lastPitch: Float = 1.0f
    var isStopped = false
    var isShutdown = false
    var activeLanguage: Locale? = Locale.US
    var activeVoice: String? = "en-voice-1"

    var stubbedLanguages: List<Locale> = listOf(Locale("es", "ES"), Locale.US, Locale.FRANCE)
    var stubbedVoices: List<TtsVoiceInfo> = listOf(
        TtsVoiceInfo("es-voice-1", "Voz 1", Locale("es", "ES")),
        TtsVoiceInfo("en-voice-1", "Voice 1", Locale.US)
    )

    private var onDoneCallback: ((String) -> Unit)? = null
    private var onErrorCallback: ((String) -> Unit)? = null

    override fun initialize(onInitSuccess: () -> Unit, onInitError: (String) -> Unit) {
        isInitialized = true
        onInitSuccess()
    }

    override fun speak(text: String, utteranceId: String, speechRate: Float, pitch: Float) {
        lastSpokenText = text
        lastSpeechRate = speechRate
        lastPitch = pitch
        isStopped = false
    }

    override fun stop() {
        isStopped = true
    }

    override fun shutdown() {
        isShutdown = true
    }

    override fun setUtteranceProgressListener(onDone: (String) -> Unit, onError: (String) -> Unit) {
        this.onDoneCallback = onDone
        this.onErrorCallback = onError
    }

    override fun getAvailableLanguages(): List<Locale> = stubbedLanguages

    override fun getAvailableVoices(locale: Locale?): List<TtsVoiceInfo> {
        return if (locale != null) {
            stubbedVoices.filter { it.locale.language.equals(locale.language, ignoreCase = true) }
        } else {
            stubbedVoices
        }
    }

    override fun setLanguage(locale: Locale): Boolean {
        activeLanguage = locale
        return true
    }

    override fun setVoice(voiceName: String): Boolean {
        activeVoice = voiceName
        return true
    }

    override fun getCurrentLanguage(): Locale? = activeLanguage

    override fun getCurrentVoice(): String? = activeVoice

    fun simulateUtteranceDone(utteranceId: String) {
        onDoneCallback?.invoke(utteranceId)
    }

    fun simulateUtteranceError(utteranceId: String) {
        onErrorCallback?.invoke(utteranceId)
    }
}
