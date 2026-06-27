/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.mediaviewer.impl.gallery.di

import com.zenobia.app.libraries.architecture.Presenter
import com.zenobia.app.libraries.mediaviewer.impl.model.MediaItem
import com.zenobia.app.libraries.voiceplayer.api.VoiceMessageState
import com.zenobia.app.libraries.voiceplayer.api.aVoiceMessageState

/**
 * A fake [MediaItemPresenterFactories] for screenshot tests.
 */
fun aFakeMediaItemPresenterFactories() = MediaItemPresenterFactories(
    mapOf(
        Pair(
            MediaItem.Voice::class,
            MediaItemPresenterFactory<MediaItem.Voice, VoiceMessageState> { Presenter { aVoiceMessageState() } },
        ),
    )
)
