/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.fixtures

import com.zenobia.app.features.messages.impl.timeline.aTimelineItemDebugInfo
import com.zenobia.app.features.messages.impl.timeline.aTimelineItemReactions
import com.zenobia.app.features.messages.impl.timeline.model.ReadReceiptData
import com.zenobia.app.features.messages.impl.timeline.model.TimelineItem
import com.zenobia.app.features.messages.impl.timeline.model.TimelineItemReadReceipts
import com.zenobia.app.features.messages.impl.timeline.model.TimelineItemThreadInfo
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemEventContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemTextContent
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarData
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarSize
import com.zenobia.app.libraries.matrix.api.core.EventId
import com.zenobia.app.libraries.matrix.api.core.TransactionId
import com.zenobia.app.libraries.matrix.api.core.UniqueId
import com.zenobia.app.libraries.matrix.api.timeline.item.event.LocalEventSendState
import com.zenobia.app.libraries.matrix.api.timeline.item.event.MessageShieldProvider
import com.zenobia.app.libraries.matrix.api.timeline.item.event.SendHandleProvider
import com.zenobia.app.libraries.matrix.api.timeline.item.event.TimelineItemDebugInfoProvider
import com.zenobia.app.libraries.matrix.test.AN_EVENT_ID
import com.zenobia.app.libraries.matrix.test.A_MESSAGE
import com.zenobia.app.libraries.matrix.test.A_USER_ID
import com.zenobia.app.libraries.matrix.test.A_USER_NAME
import com.zenobia.app.libraries.matrix.test.core.FakeSendHandle
import com.zenobia.app.libraries.matrix.ui.messages.reply.InReplyToDetails
import com.zenobia.app.libraries.matrix.ui.messages.reply.aProfileDetailsReady
import kotlinx.collections.immutable.toImmutableList

internal fun aMessageEvent(
    eventId: EventId? = AN_EVENT_ID,
    transactionId: TransactionId? = null,
    isMine: Boolean = true,
    isEditable: Boolean = true,
    canBeRepliedTo: Boolean = true,
    content: TimelineItemEventContent = TimelineItemTextContent(body = A_MESSAGE, htmlDocument = null, formattedBody = A_MESSAGE, isEdited = false),
    inReplyTo: InReplyToDetails? = null,
    threadInfo: TimelineItemThreadInfo? = null,
    sendState: LocalEventSendState = LocalEventSendState.Sent(AN_EVENT_ID),
    debugInfoProvider: TimelineItemDebugInfoProvider = TimelineItemDebugInfoProvider { aTimelineItemDebugInfo() },
    messageShieldProvider: MessageShieldProvider = MessageShieldProvider { null },
    sendHandleProvider: SendHandleProvider = SendHandleProvider { FakeSendHandle() }
) = TimelineItem.Event(
    id = UniqueId(eventId?.value.orEmpty()),
    eventId = eventId,
    transactionId = transactionId,
    senderId = A_USER_ID,
    senderProfile = aProfileDetailsReady(displayName = A_USER_NAME),
    senderAvatar = AvatarData(A_USER_ID.value, A_USER_NAME, size = AvatarSize.TimelineSender),
    content = content,
    sentTime = "",
    isMine = isMine,
    isEditable = isEditable,
    canBeRepliedTo = canBeRepliedTo,
    reactionsState = aTimelineItemReactions(count = 0),
    readReceiptState = TimelineItemReadReceipts(emptyList<ReadReceiptData>().toImmutableList()),
    localSendState = sendState,
    inReplyTo = inReplyTo,
    threadInfo = threadInfo,
    origin = null,
    timelineItemDebugInfoProvider = debugInfoProvider,
    messageShieldProvider = messageShieldProvider,
    sendHandleProvider = sendHandleProvider,
    forwarder = null,
    forwarderProfile = null,
)
