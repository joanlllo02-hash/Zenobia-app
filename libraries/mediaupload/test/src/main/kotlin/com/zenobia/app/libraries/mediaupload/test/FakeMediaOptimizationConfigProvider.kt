/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.mediaupload.test

import com.zenobia.app.libraries.mediaupload.api.MediaOptimizationConfig
import com.zenobia.app.libraries.mediaupload.api.MediaOptimizationConfigProvider
import com.zenobia.app.libraries.preferences.api.store.VideoCompressionPreset

class FakeMediaOptimizationConfigProvider(
    val config: MediaOptimizationConfig = MediaOptimizationConfig(
        compressImages = true,
        videoCompressionPreset = VideoCompressionPreset.STANDARD,
    )
) : MediaOptimizationConfigProvider {
    override suspend fun get(): MediaOptimizationConfig = config
}
