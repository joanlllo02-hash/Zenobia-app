/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.voicemessages.timeline

import androidx.compose.runtime.Composable
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoMap
import com.zenobia.app.features.messages.impl.timeline.di.TimelineItemEventContentKey
import com.zenobia.app.features.messages.impl.timeline.di.TimelineItemPresenterFactory
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemVoiceContent
import com.zenobia.app.libraries.architecture.Presenter
import com.zenobia.app.libraries.di.RoomScope
import com.zenobia.app.libraries.voiceplayer.api.VoiceMessagePresenterFactory
import com.zenobia.app.libraries.voiceplayer.api.VoiceMessageState

@BindingContainer
@ContributesTo(RoomScope::class)
interface VoiceMessagePresenterModule {
    @Binds
    @IntoMap
    @TimelineItemEventContentKey(TimelineItemVoiceContent::class)
    fun bindVoiceMessagePresenterFactory(factory: VoiceMessagePresenter.Factory): TimelineItemPresenterFactory<*, *>
}

@AssistedInject
class VoiceMessagePresenter(
    voiceMessagePresenterFactory: VoiceMessagePresenterFactory,
    @Assisted private val content: TimelineItemVoiceContent,
) : Presenter<VoiceMessageState> {
    @AssistedFactory
    fun interface Factory : TimelineItemPresenterFactory<TimelineItemVoiceContent, VoiceMessageState> {
        override fun create(content: TimelineItemVoiceContent): VoiceMessagePresenter
    }

    private val presenter = voiceMessagePresenterFactory.createVoiceMessagePresenter(
        eventId = content.eventId,
        mediaSource = content.mediaSource,
        mimeType = content.mimeType,
        filename = content.filename,
        duration = content.duration,
    )

    @Composable
    override fun present(): VoiceMessageState {
        return presenter.present()
    }
}
