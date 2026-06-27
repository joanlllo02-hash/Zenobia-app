/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.mediaviewer.impl.gallery.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.zenobia.app.libraries.core.preview.loremIpsum
import com.zenobia.app.libraries.mediaviewer.impl.model.MediaItem
import com.zenobia.app.libraries.mediaviewer.impl.model.aMediaItemVoice

class MediaItemVoiceProvider : PreviewParameterProvider<MediaItem.Voice> {
    override val values: Sequence<MediaItem.Voice>
        get() = sequenceOf(
            aMediaItemVoice(),
            aMediaItemVoice(
                filename = "A long filename that should be truncated.ogg",
                caption = "A caption",
            ),
            aMediaItemVoice(
                caption = loremIpsum,
            ),
            aMediaItemVoice(
                waveform = emptyList(),
            ),
        )
}
