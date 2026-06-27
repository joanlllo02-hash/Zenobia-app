/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.mediaviewer.impl.viewer

import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.core.mimetype.MimeTypes.isMimeTypeAudio
import com.zenobia.app.libraries.core.mimetype.MimeTypes.isMimeTypeImage
import com.zenobia.app.libraries.core.mimetype.MimeTypes.isMimeTypeVideo
import com.zenobia.app.libraries.matrix.api.core.EventId
import com.zenobia.app.libraries.matrix.api.core.UniqueId
import com.zenobia.app.libraries.matrix.api.timeline.Timeline
import com.zenobia.app.libraries.mediaviewer.api.MediaViewerEntryPoint
import com.zenobia.app.libraries.mediaviewer.impl.datasource.MediaGalleryDataSource
import com.zenobia.app.libraries.mediaviewer.impl.model.GroupedMediaItems
import com.zenobia.app.libraries.mediaviewer.impl.model.MediaItem
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf

class SingleMediaGalleryDataSource(
    private val data: GroupedMediaItems,
) : MediaGalleryDataSource {
    override fun start(coroutineScope: CoroutineScope) = Unit
    override fun groupedMediaItemsFlow() = flowOf(AsyncData.Success(data))
    override fun getLastData(): AsyncData<GroupedMediaItems> = AsyncData.Success(data)

    override val isReady: Boolean = true

    override suspend fun loadMore(direction: Timeline.PaginationDirection) = Unit
    override suspend fun deleteItem(eventId: EventId) = Unit

    companion object {
        fun createFrom(params: MediaViewerEntryPoint.Params) = SingleMediaGalleryDataSource(
            data = GroupedMediaItems(
                // Always use imageAndVideoItems, in Single mode, this is the data that will be used
                imageAndVideoItems = persistentListOf(params.toMediaItem()),
                fileItems = persistentListOf(),
            )
        )
    }
}

fun MediaViewerEntryPoint.Params.toMediaItem() = when {
    mediaInfo.mimeType.isMimeTypeImage() -> {
        MediaItem.Image(
            id = UniqueId("dummy"),
            eventId = eventId,
            mediaInfo = mediaInfo,
            mediaSource = mediaSource,
            thumbnailSource = thumbnailSource,
        )
    }
    mediaInfo.mimeType.isMimeTypeVideo() -> {
        MediaItem.Video(
            id = UniqueId("dummy"),
            eventId = eventId,
            mediaInfo = mediaInfo,
            mediaSource = mediaSource,
            thumbnailSource = thumbnailSource,
        )
    }
    mediaInfo.mimeType.isMimeTypeAudio() -> {
        if (mediaInfo.waveform == null) {
            MediaItem.Audio(
                id = UniqueId("dummy"),
                eventId = eventId,
                mediaInfo = mediaInfo,
                mediaSource = mediaSource,
            )
        } else {
            MediaItem.Voice(
                id = UniqueId("dummy"),
                eventId = eventId,
                mediaInfo = mediaInfo,
                mediaSource = mediaSource,
            )
        }
    }
    else -> {
        MediaItem.File(
            id = UniqueId("dummy"),
            eventId = eventId,
            mediaInfo = mediaInfo,
            mediaSource = mediaSource,
        )
    }
}
