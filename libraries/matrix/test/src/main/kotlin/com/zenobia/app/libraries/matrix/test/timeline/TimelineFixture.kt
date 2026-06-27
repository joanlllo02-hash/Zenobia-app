/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.test.timeline

import com.zenobia.app.libraries.matrix.api.core.EventId
import com.zenobia.app.libraries.matrix.api.core.TransactionId
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.api.media.ImageInfo
import com.zenobia.app.libraries.matrix.api.media.MediaSource
import com.zenobia.app.libraries.matrix.api.poll.PollAnswer
import com.zenobia.app.libraries.matrix.api.poll.PollKind
import com.zenobia.app.libraries.matrix.api.timeline.item.EventThreadInfo
import com.zenobia.app.libraries.matrix.api.timeline.item.TimelineItemDebugInfo
import com.zenobia.app.libraries.matrix.api.timeline.item.event.EventContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.EventReaction
import com.zenobia.app.libraries.matrix.api.timeline.item.event.EventTimelineItem
import com.zenobia.app.libraries.matrix.api.timeline.item.event.InReplyTo
import com.zenobia.app.libraries.matrix.api.timeline.item.event.LocalEventSendState
import com.zenobia.app.libraries.matrix.api.timeline.item.event.MessageContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.MessageShieldProvider
import com.zenobia.app.libraries.matrix.api.timeline.item.event.MessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.PollContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.ProfileChangeContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.ProfileDetails
import com.zenobia.app.libraries.matrix.api.timeline.item.event.Receipt
import com.zenobia.app.libraries.matrix.api.timeline.item.event.SendHandleProvider
import com.zenobia.app.libraries.matrix.api.timeline.item.event.StickerContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.TextMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.TimelineItemDebugInfoProvider
import com.zenobia.app.libraries.matrix.test.AN_EVENT_ID
import com.zenobia.app.libraries.matrix.test.A_USER_ID
import com.zenobia.app.libraries.matrix.test.A_USER_NAME
import com.zenobia.app.libraries.matrix.test.core.FakeSendHandle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

fun anEventTimelineItem(
    eventId: EventId = AN_EVENT_ID,
    transactionId: TransactionId? = null,
    isEditable: Boolean = false,
    canBeRepliedTo: Boolean = false,
    isOwn: Boolean = false,
    isRemote: Boolean = false,
    localSendState: LocalEventSendState? = null,
    reactions: ImmutableList<EventReaction> = persistentListOf(),
    receipts: ImmutableList<Receipt> = persistentListOf(),
    sender: UserId = A_USER_ID,
    senderProfile: ProfileDetails = aProfileDetails(),
    timestamp: Long = 0L,
    content: EventContent = aProfileChangeMessageContent(),
    debugInfoProvider: TimelineItemDebugInfoProvider = TimelineItemDebugInfoProvider { aTimelineItemDebugInfo() },
    messageShieldProvider: MessageShieldProvider = MessageShieldProvider { null },
    sendHandleProvider: SendHandleProvider = SendHandleProvider { FakeSendHandle() }
) = EventTimelineItem(
    eventId = eventId,
    transactionId = transactionId,
    isEditable = isEditable,
    canBeRepliedTo = canBeRepliedTo,
    isOwn = isOwn,
    isRemote = isRemote,
    localSendState = localSendState,
    reactions = reactions,
    receipts = receipts,
    sender = sender,
    senderProfile = senderProfile,
    timestamp = timestamp,
    content = content,
    origin = null,
    timelineItemDebugInfoProvider = debugInfoProvider,
    messageShieldProvider = messageShieldProvider,
    sendHandleProvider = sendHandleProvider,
    forwarder = null,
    forwarderProfile = null,
)

fun aProfileDetails(
    displayName: String? = A_USER_NAME,
    displayNameAmbiguous: Boolean = false,
    avatarUrl: String? = null
): ProfileDetails = ProfileDetails.Ready(
    displayName = displayName,
    displayNameAmbiguous = displayNameAmbiguous,
    avatarUrl = avatarUrl,
)

fun aProfileChangeMessageContent(
    displayName: String? = null,
    prevDisplayName: String? = null,
    avatarUrl: String? = null,
    prevAvatarUrl: String? = null,
) = ProfileChangeContent(
    displayName = displayName,
    prevDisplayName = prevDisplayName,
    avatarUrl = avatarUrl,
    prevAvatarUrl = prevAvatarUrl,
)

fun aMessageContent(
    body: String = "body",
    inReplyTo: InReplyTo? = null,
    isEdited: Boolean = false,
    threadInfo: EventThreadInfo? = null,
    messageType: MessageType = TextMessageType(
        body = body,
        formatted = null
    )
) = MessageContent(
    body = body,
    inReplyTo = inReplyTo,
    isEdited = isEdited,
    threadInfo = threadInfo,
    type = messageType
)

fun aStickerContent(
    filename: String = "filename",
    info: ImageInfo,
    mediaSource: MediaSource,
    body: String? = null,
    threadInfo: EventThreadInfo? = null,
) = StickerContent(
    filename = filename,
    body = body,
    info = info,
    source = mediaSource,
    threadInfo = threadInfo,
)

fun aTimelineItemDebugInfo(
    model: String = "Rust(Model())",
    originalJson: String? = null,
    latestEditedJson: String? = null,
) = TimelineItemDebugInfo(
    model,
    originalJson,
    latestEditedJson
)

fun aPollContent(
    question: String = "Do you like polls?",
    answers: ImmutableList<PollAnswer> = persistentListOf(PollAnswer("1", "Yes"), PollAnswer("2", "No")),
    kind: PollKind = PollKind.Disclosed,
    maxSelections: ULong = 1u,
    votes: ImmutableMap<String, ImmutableList<UserId>> = persistentMapOf(),
    endTime: ULong? = null,
    isEdited: Boolean = false,
    threadInfo: EventThreadInfo? = null,
) = PollContent(
    question = question,
    kind = kind,
    maxSelections = maxSelections,
    answers = answers,
    votes = votes,
    endTime = endTime,
    isEdited = isEdited,
    threadInfo = threadInfo,
)
