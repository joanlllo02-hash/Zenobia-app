/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.attachments.video

import com.zenobia.app.libraries.architecture.Presenter
import com.zenobia.app.libraries.mediaviewer.api.local.LocalMedia

fun interface MediaOptimizationSelectorPresenter : Presenter<MediaOptimizationSelectorState> {
    interface Factory {
        fun create(
            localMedia: LocalMedia,
            sendAsFile: Boolean,
        ): MediaOptimizationSelectorPresenter
    }
}
