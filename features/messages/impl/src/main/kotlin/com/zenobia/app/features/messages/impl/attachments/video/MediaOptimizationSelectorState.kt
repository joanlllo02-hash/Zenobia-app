/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.attachments.video

import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.preferences.api.store.VideoCompressionPreset
import kotlinx.collections.immutable.ImmutableList

data class MediaOptimizationSelectorState(
    val maxUploadSize: AsyncData<Long>,
    val videoSizeEstimations: AsyncData<ImmutableList<VideoUploadEstimation>>,
    val isImageOptimizationEnabled: Boolean?,
    val selectedVideoPreset: VideoCompressionPreset?,
    val displayMediaSelectorViews: Boolean?,
    val displayVideoPresetSelectorDialog: Boolean,
    val eventSink: (MediaOptimizationSelectorEvent) -> Unit
)

data class VideoUploadEstimation(
    val preset: VideoCompressionPreset,
    val sizeInBytes: Long,
    val canUpload: Boolean,
)
