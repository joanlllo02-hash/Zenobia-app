/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.mediaviewer.impl.local.audio

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.zenobia.app.libraries.designsystem.components.media.WaveFormSamples
import com.zenobia.app.libraries.mediaviewer.api.MediaInfo
import com.zenobia.app.libraries.mediaviewer.api.anAudioMediaInfo

open class MediaInfoAudioProvider : PreviewParameterProvider<MediaInfo> {
    override val values: Sequence<MediaInfo>
        get() = sequenceOf(
            anAudioMediaInfo(),
            anAudioMediaInfo(
                waveForm = WaveFormSamples.realisticWaveForm,
            ),
        )
}
