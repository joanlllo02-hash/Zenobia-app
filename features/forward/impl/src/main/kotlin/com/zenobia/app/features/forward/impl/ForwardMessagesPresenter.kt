/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.forward.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.architecture.Presenter
import com.zenobia.app.libraries.architecture.runCatchingUpdatingState
import com.zenobia.app.libraries.di.annotations.SessionCoroutineScope
import com.zenobia.app.libraries.matrix.api.core.EventId
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.timeline.TimelineProvider
import com.zenobia.app.libraries.matrix.api.timeline.getActiveTimeline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@AssistedInject
class ForwardMessagesPresenter(
    @Assisted eventId: String,
    @Assisted private val timelineProvider: TimelineProvider,
    @SessionCoroutineScope
    private val sessionCoroutineScope: CoroutineScope,
) : Presenter<ForwardMessagesState> {
    private val eventId: EventId = EventId(eventId)

    @AssistedFactory
    fun interface Factory {
        fun create(eventId: String, timelineProvider: TimelineProvider): ForwardMessagesPresenter
    }

    private val forwardingActionState: MutableState<AsyncAction<List<RoomId>>> = mutableStateOf(AsyncAction.Uninitialized)

    fun onRoomSelected(roomIds: List<RoomId>) {
        sessionCoroutineScope.forwardEvent(eventId, roomIds)
    }

    @Composable
    override fun present(): ForwardMessagesState {
        fun handleEvent(event: ForwardMessagesEvents) {
            when (event) {
                ForwardMessagesEvents.ClearError -> forwardingActionState.value = AsyncAction.Uninitialized
            }
        }

        return ForwardMessagesState(
            forwardAction = forwardingActionState.value,
            eventSink = ::handleEvent,
        )
    }

    private fun CoroutineScope.forwardEvent(
        eventId: EventId,
        roomIds: List<RoomId>,
    ) = launch {
        suspend {
            timelineProvider.getActiveTimeline().forwardEvent(eventId, roomIds)
                .onFailure {
                    Timber.e(it, "Error while forwarding event")
                }
                .getOrThrow()
            roomIds
        }.runCatchingUpdatingState(forwardingActionState)
    }
}
