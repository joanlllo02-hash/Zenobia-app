/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.mediaviewer.impl.viewer

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.designsystem.utils.snackbar.SnackbarMessage
import com.zenobia.app.libraries.matrix.api.core.EventId
import com.zenobia.app.libraries.matrix.api.media.MediaSource
import com.zenobia.app.libraries.matrix.api.timeline.Timeline
import com.zenobia.app.libraries.mediaviewer.api.MediaInfo
import com.zenobia.app.libraries.mediaviewer.api.local.LocalMedia
import com.zenobia.app.libraries.mediaviewer.impl.details.MediaBottomSheetState
import kotlinx.collections.immutable.ImmutableList

data class MediaViewerState(
    val initiallySelectedEventId: EventId?,
    val listData: ImmutableList<MediaViewerPageData>,
    val currentIndex: Int,
    val snackbarMessage: SnackbarMessage?,
    val canShowInfo: Boolean,
    val mediaBottomSheetState: MediaBottomSheetState,
    val eventSink: (MediaViewerEvent) -> Unit,
)

@Immutable
sealed interface MediaViewerPageData {
    val pagerKey: Long

    data class Failure(
        val throwable: Throwable,
        override val pagerKey: Long = 0,
    ) : MediaViewerPageData

    data class Loading(
        val direction: Timeline.PaginationDirection,
        val timestamp: Long,
        override val pagerKey: Long,
    ) : MediaViewerPageData

    data class MediaViewerData(
        val eventId: EventId?,
        val mediaInfo: MediaInfo,
        val mediaSource: MediaSource,
        val thumbnailSource: MediaSource?,
        val downloadedMedia: State<AsyncData<LocalMedia>>,
        override val pagerKey: Long,
    ) : MediaViewerPageData
}
