/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.ui.messages.reply

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.zenobia.app.libraries.designsystem.preview.USER_NAME_SENDER
import com.zenobia.app.libraries.matrix.api.core.EventId
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.api.media.MediaSource
import com.zenobia.app.libraries.matrix.api.poll.PollKind
import com.zenobia.app.libraries.matrix.api.timeline.item.EventThreadInfo
import com.zenobia.app.libraries.matrix.api.timeline.item.event.AudioMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.EmoteMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.EventContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.FileMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.ImageMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.LocationMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.MessageContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.MessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.NoticeMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.PollContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.ProfileDetails
import com.zenobia.app.libraries.matrix.api.timeline.item.event.RedactedContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.StickerMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.TextMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.UnableToDecryptContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.VideoMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.VoiceMessageType
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

open class InReplyToDetailsProvider : PreviewParameterProvider<InReplyToDetails> {
    override val values: Sequence<InReplyToDetails>
        get() = sequenceOf(
            aMessageContent(
                body = "Message which are being replied.",
                type = TextMessageType("Message which are being replied.", null)
            ),
            aMessageContent(
                body = "Message which are being replied, and which was long enough to be displayed on two lines (only!).",
                type = TextMessageType("Message which are being replied, and which was long enough to be displayed on two lines (only!).", null)
            ),
            aMessageContent(
                body = "Video",
                type = VideoMessageType("Video", null, null, MediaSource("url"), null),
            ),
            aMessageContent(
                body = "Audio",
                type = AudioMessageType("Audio", null, null, MediaSource("url"), null),
            ),
            aMessageContent(
                body = "Voice",
                type = VoiceMessageType("Voice", null, null, MediaSource("url"), null, null),
            ),
            aMessageContent(
                body = "Image",
                type = ImageMessageType("Image", null, null, MediaSource("url"), null),
            ),
            aMessageContent(
                body = "Sticker",
                type = StickerMessageType("Image", null, null, MediaSource("url"), null),
            ),
            aMessageContent(
                body = "File",
                type = FileMessageType("File", null, null, MediaSource("url"), null),
            ),
            aMessageContent(
                body = "Location",
                type = LocationMessageType("Location", "geo:1,2", null, assetType = null),
            ),
            aMessageContent(
                body = "Notice",
                type = NoticeMessageType("Notice", null),
            ),
            aMessageContent(
                body = "Emote",
                type = EmoteMessageType("Emote", null),
            ),
            PollContent(
                question = "Poll which are being replied.",
                kind = PollKind.Disclosed,
                maxSelections = 1u,
                answers = persistentListOf(),
                votes = persistentMapOf(),
                endTime = null,
                isEdited = false,
                threadInfo = null,
            ),
        ).map {
            aInReplyToDetails(
                eventContent = it,
            )
        }
}

class InReplyToDetailsDisambiguatedProvider : InReplyToDetailsProvider() {
    override val values: Sequence<InReplyToDetails>
        get() = sequenceOf(
            aMessageContent(
                body = "Message which are being replied.",
                type = TextMessageType("Message which are being replied.", null)
            ),
        ).map {
            aInReplyToDetails(
                displayNameAmbiguous = true,
                eventContent = it,
            )
        }
}

class InReplyToDetailsInformativeProvider : InReplyToDetailsProvider() {
    override val values: Sequence<InReplyToDetails>
        get() = sequenceOf(
            RedactedContent,
            UnableToDecryptContent(data = UnableToDecryptContent.Data.Unknown, threadInfo = null),
        ).map {
            aInReplyToDetails(
                eventContent = it,
            )
        }
}

class InReplyToDetailsOtherProvider : InReplyToDetailsProvider() {
    override val values: Sequence<InReplyToDetails>
        get() = sequenceOf(
            InReplyToDetails.Loading(eventId = EventId("\$anEventId")),
            InReplyToDetails.Error(eventId = EventId("\$anEventId"), message = "An error message."),
        )
}

private fun aMessageContent(
    body: String,
    type: MessageType,
    threadInfo: EventThreadInfo? = null,
) = MessageContent(
    body = body,
    inReplyTo = null,
    isEdited = false,
    threadInfo = threadInfo,
    type = type,
)

private fun aInReplyToDetails(
    eventContent: EventContent,
    displayNameAmbiguous: Boolean = false,
) = InReplyToDetails.Ready(
    eventId = EventId("\$event"),
    eventContent = eventContent,
    senderId = UserId("@Sender:domain"),
    senderProfile = aProfileDetailsReady(
        displayNameAmbiguous = displayNameAmbiguous,
    ),
    textContent = (eventContent as? MessageContent)?.body.orEmpty(),
)

fun aProfileDetailsReady(
    displayName: String? = USER_NAME_SENDER,
    displayNameAmbiguous: Boolean = false,
    avatarUrl: String? = null,
) = ProfileDetails.Ready(
    displayName = displayName,
    displayNameAmbiguous = displayNameAmbiguous,
    avatarUrl = avatarUrl,
)
