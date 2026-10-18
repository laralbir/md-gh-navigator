package com.laralbir.mdghnavigator.core.common.event

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

interface EventBus {
    val events: SharedFlow<AppEvent>
    suspend fun publish(event: AppEvent)
}

class DefaultEventBus(
    extraBufferCapacity: Int = 64
) : EventBus {
    private val _events = MutableSharedFlow<AppEvent>(
        replay = 0,
        extraBufferCapacity = extraBufferCapacity
    )

    override val events: SharedFlow<AppEvent> = _events.asSharedFlow()

    override suspend fun publish(event: AppEvent) {
        _events.emit(event)
    }
}

