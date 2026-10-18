package com.laralbir.mdghnavigator.core.common.event

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EventBusTest {

    private lateinit var eventBus: EventBus

    @BeforeEach
    fun setUp() {
        eventBus = DefaultEventBus()
    }

    @Test
    fun `should emit and collect AppEvent FileChanged correctly`() = runTest {
        val event = AppEvent.FileChanged(sourceId = "repo-1", filePath = "docs/README.md")

        eventBus.events.test {
            eventBus.publish(event)
            val received = awaitItem()
            assertEquals(event, received)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit and collect AppEvent LogMessage correctly`() = runTest {
        val event = AppEvent.LogMessage(level = LogLevel.INFO, tag = "Sync", message = "Clone complete")

        eventBus.events.test {
            eventBus.publish(event)
            val received = awaitItem()
            assertEquals(event, received)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit multiple events sequentially without loss`() = runTest {
        val event1 = AppEvent.SyncProgress(sourceId = "repo-1", progress = 0.5f, statusMessage = "Downloading")
        val event2 = AppEvent.SyncProgress(sourceId = "repo-1", progress = 1.0f, statusMessage = "Finished")

        eventBus.events.test {
            eventBus.publish(event1)
            eventBus.publish(event2)

            assertEquals(event1, awaitItem())
            assertEquals(event2, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}

