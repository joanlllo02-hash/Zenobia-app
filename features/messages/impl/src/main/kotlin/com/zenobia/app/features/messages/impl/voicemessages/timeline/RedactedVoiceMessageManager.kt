/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.voicemessages.timeline

import dev.zacsweers.metro.ContributesBinding
import com.zenobia.app.libraries.core.coroutine.CoroutineDispatchers
import com.zenobia.app.libraries.di.RoomScope
import com.zenobia.app.libraries.matrix.api.timeline.MatrixTimelineItem
import com.zenobia.app.libraries.matrix.api.timeline.item.event.RedactedContent
import com.zenobia.app.libraries.mediaplayer.api.MediaPlayer
import kotlinx.coroutines.withContext

interface RedactedVoiceMessageManager {
    suspend fun onEachMatrixTimelineItem(timelineItems: List<MatrixTimelineItem>)
}

@ContributesBinding(RoomScope::class)
class DefaultRedactedVoiceMessageManager(
    private val dispatchers: CoroutineDispatchers,
    private val mediaPlayer: MediaPlayer,
) : RedactedVoiceMessageManager {
    override suspend fun onEachMatrixTimelineItem(timelineItems: List<MatrixTimelineItem>) {
        withContext(dispatchers.computation) {
            mediaPlayer.state.value.let { playerState ->
                if (playerState.isPlaying && playerState.mediaId != null) {
                    val needsToPausePlayer = timelineItems.any {
                        it is MatrixTimelineItem.Event &&
                            playerState.mediaId == it.eventId?.value &&
                            it.event.content is RedactedContent
                    }
                    if (needsToPausePlayer) {
                        withContext(dispatchers.main) { mediaPlayer.pause() }
                    }
                }
            }
        }
    }
}
