/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.mediaviewer.impl.details

import com.zenobia.app.libraries.matrix.api.core.EventId
import com.zenobia.app.libraries.matrix.api.media.MediaSource
import com.zenobia.app.libraries.mediaviewer.api.MediaInfo

sealed interface MediaBottomSheetState {
    data object Hidden : MediaBottomSheetState

    data class Details(
        val fromGallery: Boolean,
        val eventId: EventId?,
        val canDelete: Boolean,
        val mediaInfo: MediaInfo,
        val thumbnailSource: MediaSource?,
    ) : MediaBottomSheetState

    data class DeleteConfirmation(
        val eventId: EventId,
        val mediaInfo: MediaInfo,
        val thumbnailSource: MediaSource?,
    ) : MediaBottomSheetState
}
