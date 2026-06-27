/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.mediaviewer.impl.model

import com.zenobia.app.libraries.designsystem.components.media.WaveFormSamples
import com.zenobia.app.libraries.matrix.api.core.EventId
import com.zenobia.app.libraries.matrix.api.core.UniqueId
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.api.media.MediaSource
import com.zenobia.app.libraries.matrix.api.timeline.Timeline
import com.zenobia.app.libraries.mediaviewer.api.aPdfMediaInfo
import com.zenobia.app.libraries.mediaviewer.api.aVideoMediaInfo
import com.zenobia.app.libraries.mediaviewer.api.aVoiceMediaInfo
import com.zenobia.app.libraries.mediaviewer.api.anAudioMediaInfo
import com.zenobia.app.libraries.mediaviewer.api.anImageMediaInfo

fun aMediaItemImage(
    id: UniqueId = UniqueId("imageId"),
    eventId: EventId? = null,
    senderId: UserId? = null,
    mediaSourceUrl: String = "",
): MediaItem.Image {
    return MediaItem.Image(
        id = id,
        eventId = eventId,
        mediaInfo = anImageMediaInfo(
            senderId = senderId,
        ),
        mediaSource = MediaSource(mediaSourceUrl),
        thumbnailSource = null,
    )
}

fun aMediaItemVideo(
    id: UniqueId = UniqueId("videoId"),
    mediaSource: MediaSource = MediaSource(""),
    duration: String? = "1:23",
): MediaItem.Video {
    return MediaItem.Video(
        id = id,
        eventId = null,
        mediaInfo = aVideoMediaInfo(
            duration = duration
        ),
        mediaSource = mediaSource,
        thumbnailSource = null,
    )
}

fun aMediaItemFile(
    id: UniqueId = UniqueId("fileId"),
    eventId: EventId? = null,
    filename: String = "filename",
    caption: String? = null,
): MediaItem.File {
    return MediaItem.File(
        id = id,
        eventId = eventId,
        mediaInfo = aPdfMediaInfo(
            filename = filename,
            caption = caption,
        ),
        mediaSource = MediaSource(""),
    )
}

fun aMediaItemAudio(
    id: UniqueId = UniqueId("fileId"),
    eventId: EventId? = null,
    filename: String = "filename",
    caption: String? = null,
): MediaItem.Audio {
    return MediaItem.Audio(
        id = id,
        eventId = eventId,
        mediaInfo = anAudioMediaInfo(
            filename = filename,
            caption = caption,
        ),
        mediaSource = MediaSource(""),
    )
}

fun aMediaItemVoice(
    id: UniqueId = UniqueId("fileId"),
    filename: String = "filename.ogg",
    caption: String? = null,
    duration: String? = "1:23",
    waveform: List<Float> = WaveFormSamples.realisticWaveForm,
): MediaItem.Voice {
    return MediaItem.Voice(
        id = id,
        eventId = null,
        mediaInfo = aVoiceMediaInfo(
            filename = filename,
            caption = caption,
            duration = duration,
            waveForm = waveform,
        ),
        mediaSource = MediaSource(""),
    )
}

fun aMediaItemDateSeparator(
    id: UniqueId = UniqueId("dateId"),
    formattedDate: String = "October 2024",
): MediaItem.DateSeparator {
    return MediaItem.DateSeparator(
        id = id,
        formattedDate = formattedDate,
    )
}

fun aMediaItemLoadingIndicator(
    id: UniqueId = UniqueId("loadingId"),
    direction: Timeline.PaginationDirection = Timeline.PaginationDirection.BACKWARDS,
): MediaItem.LoadingIndicator {
    return MediaItem.LoadingIndicator(
        id = id,
        direction = direction,
        timestamp = 123,
    )
}
