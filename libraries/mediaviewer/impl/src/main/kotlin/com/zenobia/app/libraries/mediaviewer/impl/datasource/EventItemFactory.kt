/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.mediaviewer.impl.datasource

import dev.zacsweers.metro.Inject
import com.zenobia.app.libraries.androidutils.filesize.FileSizeFormatter
import com.zenobia.app.libraries.dateformatter.api.DateFormatter
import com.zenobia.app.libraries.dateformatter.api.DateFormatterMode
import com.zenobia.app.libraries.dateformatter.api.toHumanReadableDuration
import com.zenobia.app.libraries.matrix.api.timeline.MatrixTimelineItem
import com.zenobia.app.libraries.matrix.api.timeline.item.event.AudioMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.CallNotifyContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.EmoteMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.FailedToParseMessageLikeContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.FailedToParseStateContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.FileMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.ImageMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.LegacyCallInviteContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.LiveLocationContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.LocationMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.MessageContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.NoticeMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.OtherMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.PollContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.ProfileChangeContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.RedactedContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.RoomMembershipContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.StateContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.StickerContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.StickerMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.TextMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.UnableToDecryptContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.UnknownContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.VideoMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.VoiceMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.getAvatarUrl
import com.zenobia.app.libraries.matrix.api.timeline.item.event.getDisambiguatedDisplayName
import com.zenobia.app.libraries.mediaviewer.api.MediaInfo
import com.zenobia.app.libraries.mediaviewer.api.util.FileExtensionExtractor
import com.zenobia.app.libraries.mediaviewer.impl.model.MediaItem
import timber.log.Timber

@Inject
class EventItemFactory(
    private val fileSizeFormatter: FileSizeFormatter,
    private val fileExtensionExtractor: FileExtensionExtractor,
    private val dateFormatter: DateFormatter,
) {
    fun create(
        currentTimelineItem: MatrixTimelineItem.Event,
    ): MediaItem.Event? {
        val event = currentTimelineItem.event
        val dateSent = dateFormatter.format(
            currentTimelineItem.event.timestamp,
            mode = DateFormatterMode.Day,
        )
        val dateSentFull = dateFormatter.format(
            timestamp = currentTimelineItem.event.timestamp,
            mode = DateFormatterMode.Full,
        )
        return when (val content = event.content) {
            is CallNotifyContent,
            is FailedToParseMessageLikeContent,
            is FailedToParseStateContent,
            LegacyCallInviteContent,
            is PollContent,
            is ProfileChangeContent,
            RedactedContent,
            is RoomMembershipContent,
            is StateContent,
            is StickerContent,
            is UnableToDecryptContent,
            is LiveLocationContent,
            UnknownContent -> {
                Timber.w("Should not happen: ${content.javaClass.simpleName}")
                null
            }
            is MessageContent -> {
                when (val type = content.type) {
                    is EmoteMessageType,
                    is NoticeMessageType,
                    is OtherMessageType,
                    is LocationMessageType,
                    is TextMessageType -> {
                        Timber.w("Should not happen: ${content.type}")
                        null
                    }
                    is AudioMessageType -> MediaItem.Audio(
                        id = currentTimelineItem.uniqueId,
                        eventId = currentTimelineItem.eventId,
                        mediaInfo = MediaInfo(
                            filename = type.filename,
                            fileSize = type.info?.size,
                            caption = type.caption,
                            formattedCaption = type.formattedCaption?.body,
                            mimeType = type.info?.mimetype.orEmpty(),
                            formattedFileSize = type.info?.size?.let { fileSizeFormatter.format(it) }.orEmpty(),
                            fileExtension = fileExtensionExtractor.extractFromName(type.filename),
                            senderId = event.sender,
                            senderName = event.senderProfile.getDisambiguatedDisplayName(event.sender),
                            senderAvatar = event.senderProfile.getAvatarUrl(),
                            dateSent = dateSent,
                            dateSentFull = dateSentFull,
                            waveform = null,
                            duration = null,
                        ),
                        mediaSource = type.source,
                    )
                    is FileMessageType -> MediaItem.File(
                        id = currentTimelineItem.uniqueId,
                        eventId = currentTimelineItem.eventId,
                        mediaInfo = MediaInfo(
                            filename = type.filename,
                            fileSize = type.info?.size,
                            caption = type.caption,
                            formattedCaption = type.formattedCaption?.body,
                            mimeType = type.info?.mimetype.orEmpty(),
                            formattedFileSize = type.info?.size?.let { fileSizeFormatter.format(it) }.orEmpty(),
                            fileExtension = fileExtensionExtractor.extractFromName(type.filename),
                            senderId = event.sender,
                            senderName = event.senderProfile.getDisambiguatedDisplayName(event.sender),
                            senderAvatar = event.senderProfile.getAvatarUrl(),
                            dateSent = dateSent,
                            dateSentFull = dateSentFull,
                            waveform = null,
                            duration = null,
                        ),
                        mediaSource = type.source,
                        // TODO We may want to add a thumbnailSource and set it to type.info?.thumbnailSource
                    )
                    is ImageMessageType -> MediaItem.Image(
                        id = currentTimelineItem.uniqueId,
                        eventId = currentTimelineItem.eventId,
                        mediaInfo = MediaInfo(
                            filename = type.filename,
                            fileSize = type.info?.size,
                            caption = type.caption,
                            formattedCaption = type.formattedCaption?.body,
                            mimeType = type.info?.mimetype.orEmpty(),
                            formattedFileSize = type.info?.size?.let { fileSizeFormatter.format(it) }.orEmpty(),
                            fileExtension = fileExtensionExtractor.extractFromName(type.filename),
                            senderId = event.sender,
                            senderName = event.senderProfile.getDisambiguatedDisplayName(event.sender),
                            senderAvatar = event.senderProfile.getAvatarUrl(),
                            dateSent = dateSent,
                            dateSentFull = dateSentFull,
                            waveform = null,
                            duration = null,
                        ),
                        mediaSource = type.source,
                        thumbnailSource = type.info?.thumbnailSource,
                    )
                    is StickerMessageType -> MediaItem.Image(
                        id = currentTimelineItem.uniqueId,
                        eventId = currentTimelineItem.eventId,
                        mediaInfo = MediaInfo(
                            filename = type.filename,
                            fileSize = type.info?.size,
                            caption = type.caption,
                            formattedCaption = type.formattedCaption?.body,
                            mimeType = type.info?.mimetype.orEmpty(),
                            formattedFileSize = type.info?.size?.let { fileSizeFormatter.format(it) }.orEmpty(),
                            fileExtension = fileExtensionExtractor.extractFromName(type.filename),
                            senderId = event.sender,
                            senderName = event.senderProfile.getDisambiguatedDisplayName(event.sender),
                            senderAvatar = event.senderProfile.getAvatarUrl(),
                            dateSent = dateSent,
                            dateSentFull = dateSentFull,
                            waveform = null,
                            duration = null,
                        ),
                        mediaSource = type.source,
                        thumbnailSource = type.info?.thumbnailSource,
                    )
                    is VideoMessageType -> MediaItem.Video(
                        id = currentTimelineItem.uniqueId,
                        eventId = currentTimelineItem.eventId,
                        mediaInfo = MediaInfo(
                            filename = type.filename,
                            fileSize = type.info?.size,
                            caption = type.caption,
                            formattedCaption = type.formattedCaption?.body,
                            mimeType = type.info?.mimetype.orEmpty(),
                            formattedFileSize = type.info?.size?.let { fileSizeFormatter.format(it) }.orEmpty(),
                            fileExtension = fileExtensionExtractor.extractFromName(type.filename),
                            senderId = event.sender,
                            senderName = event.senderProfile.getDisambiguatedDisplayName(event.sender),
                            senderAvatar = event.senderProfile.getAvatarUrl(),
                            dateSent = dateSent,
                            dateSentFull = dateSentFull,
                            waveform = null,
                            duration = type.info?.duration?.inWholeMilliseconds?.toHumanReadableDuration(),
                        ),
                        mediaSource = type.source,
                        thumbnailSource = type.info?.thumbnailSource,
                    )
                    is VoiceMessageType -> MediaItem.Voice(
                        id = currentTimelineItem.uniqueId,
                        eventId = currentTimelineItem.eventId,
                        mediaInfo = MediaInfo(
                            filename = type.filename,
                            fileSize = type.info?.size,
                            caption = type.caption,
                            formattedCaption = type.formattedCaption?.body,
                            mimeType = type.info?.mimetype.orEmpty(),
                            formattedFileSize = type.info?.size?.let { fileSizeFormatter.format(it) }.orEmpty(),
                            fileExtension = fileExtensionExtractor.extractFromName(type.filename),
                            senderId = event.sender,
                            senderName = event.senderProfile.getDisambiguatedDisplayName(event.sender),
                            senderAvatar = event.senderProfile.getAvatarUrl(),
                            dateSent = dateSent,
                            dateSentFull = dateSentFull,
                            waveform = type.details?.waveform.orEmpty(),
                            duration = type.info?.duration?.inWholeMilliseconds?.toHumanReadableDuration(),
                        ),
                        mediaSource = type.source,
                    )
                }
            }
        }
    }
}
