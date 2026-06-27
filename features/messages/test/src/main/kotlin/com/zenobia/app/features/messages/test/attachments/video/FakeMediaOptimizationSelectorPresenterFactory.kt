/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.test.attachments.video

import com.zenobia.app.features.messages.impl.attachments.video.MediaOptimizationSelectorPresenter
import com.zenobia.app.features.messages.impl.attachments.video.MediaOptimizationSelectorState
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.mediaviewer.api.local.LocalMedia

class FakeMediaOptimizationSelectorPresenterFactory(
    private val fakePresenter: MediaOptimizationSelectorPresenter = MediaOptimizationSelectorPresenter {
        MediaOptimizationSelectorState(
            maxUploadSize = AsyncData.Uninitialized,
            videoSizeEstimations = AsyncData.Uninitialized,
            isImageOptimizationEnabled = null,
            selectedVideoPreset = null,
            displayMediaSelectorViews = null,
            displayVideoPresetSelectorDialog = false,
            eventSink = {},
        )
    }
) : MediaOptimizationSelectorPresenter.Factory {
    override fun create(localMedia: LocalMedia, sendAsFile: Boolean): MediaOptimizationSelectorPresenter {
        return fakePresenter
    }
}
