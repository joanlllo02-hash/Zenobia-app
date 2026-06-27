/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.test.timeline.voicemessages.composer

import com.zenobia.app.features.messages.impl.voicemessages.composer.DefaultVoiceMessageComposerPresenter
import com.zenobia.app.features.messages.impl.voicemessages.composer.VoiceMessageComposerPlayer
import com.zenobia.app.features.messages.test.FakeMessageComposerContext
import com.zenobia.app.libraries.matrix.api.timeline.Timeline
import com.zenobia.app.libraries.mediaplayer.test.FakeAudioFocus
import com.zenobia.app.libraries.mediaplayer.test.FakeMediaPlayer
import com.zenobia.app.libraries.mediaupload.api.MediaSender
import com.zenobia.app.libraries.mediaupload.test.FakeMediaSender
import com.zenobia.app.libraries.permissions.test.FakePermissionsPresenterFactory
import com.zenobia.app.libraries.voicerecorder.test.FakeVoiceRecorder
import com.zenobia.app.services.analytics.test.FakeAnalyticsService
import kotlinx.coroutines.CoroutineScope

class FakeDefaultVoiceMessageComposerPresenterFactory(
    private val sessionCoroutineScope: CoroutineScope,
    private val mediaSender: MediaSender = FakeMediaSender(),
) : DefaultVoiceMessageComposerPresenter.Factory {
    override fun create(timelineMode: Timeline.Mode): DefaultVoiceMessageComposerPresenter {
        return DefaultVoiceMessageComposerPresenter(
            sessionCoroutineScope = sessionCoroutineScope,
            timelineMode = timelineMode,
            voiceRecorder = FakeVoiceRecorder(),
            analyticsService = FakeAnalyticsService(),
            audioFocus = FakeAudioFocus(
                requestAudioFocusResult = { _, _ -> },
                releaseAudioFocusResult = { },
            ),
            mediaSenderFactory = { mediaSender },
            player = VoiceMessageComposerPlayer(
                mediaPlayer = FakeMediaPlayer(),
                sessionCoroutineScope = sessionCoroutineScope,
            ),
            messageComposerContext = FakeMessageComposerContext(),
            permissionsPresenterFactory = FakePermissionsPresenterFactory(),
        )
    }
}
