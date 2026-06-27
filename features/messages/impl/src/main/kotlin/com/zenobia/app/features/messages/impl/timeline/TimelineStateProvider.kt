/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.timeline

import com.zenobia.app.features.messages.impl.crypto.sendfailure.resolve.ResolveVerifiedUserSendFailureState
import com.zenobia.app.features.messages.impl.crypto.sendfailure.resolve.aResolveVerifiedUserSendFailureState
import com.zenobia.app.features.messages.impl.timeline.components.MessageShieldData
import com.zenobia.app.features.messages.impl.timeline.components.receipt.aReadReceiptData
import com.zenobia.app.features.messages.impl.timeline.model.NewEventState
import com.zenobia.app.features.messages.impl.timeline.model.ReadReceiptData
import com.zenobia.app.features.messages.impl.timeline.model.TimelineItem
import com.zenobia.app.features.messages.impl.timeline.model.TimelineItemGroupPosition
import com.zenobia.app.features.messages.impl.timeline.model.TimelineItemReactions
import com.zenobia.app.features.messages.impl.timeline.model.TimelineItemReadReceipts
import com.zenobia.app.features.messages.impl.timeline.model.TimelineItemThreadInfo
import com.zenobia.app.features.messages.impl.timeline.model.anAggregatedReaction
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemEventContent
import com.zenobia.app.features.messages.impl.timeline.model.event.aTimelineItemStateEventContent
import com.zenobia.app.features.messages.impl.timeline.model.event.aTimelineItemTextContent
import com.zenobia.app.features.messages.impl.timeline.model.virtual.aTimelineItemDaySeparatorModel
import com.zenobia.app.features.messages.impl.typing.TypingNotificationState
import com.zenobia.app.features.messages.impl.typing.aTypingNotificationState
import com.zenobia.app.features.roomcall.api.aStandByCallState
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarData
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarSize
import com.zenobia.app.libraries.designsystem.preview.ROOM_NAME
import com.zenobia.app.libraries.designsystem.preview.USER_NAME_SENDER
import com.zenobia.app.libraries.matrix.api.core.EventId
import com.zenobia.app.libraries.matrix.api.core.TransactionId
import com.zenobia.app.libraries.matrix.api.core.UniqueId
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.api.room.tombstone.PredecessorRoom
import com.zenobia.app.libraries.matrix.api.timeline.Timeline
import com.zenobia.app.libraries.matrix.api.timeline.item.TimelineItemDebugInfo
import com.zenobia.app.libraries.matrix.api.timeline.item.event.LocalEventSendState
import com.zenobia.app.libraries.matrix.api.timeline.item.event.MessageShield
import com.zenobia.app.libraries.matrix.ui.messages.reply.InReplyToDetails
import com.zenobia.app.libraries.matrix.ui.messages.reply.aProfileDetailsReady
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import java.util.UUID
import kotlin.random.Random

fun aTimelineState(
    timelineItems: ImmutableList<TimelineItem> = persistentListOf(),
    timelineMode: Timeline.Mode = Timeline.Mode.Live,
    timelineRoomInfo: TimelineRoomInfo = aTimelineRoomInfo(),
    focusedEventIndex: Int = -1,
    isLive: Boolean = true,
    messageShield: MessageShield? = null,
    resolveVerifiedUserSendFailureState: ResolveVerifiedUserSendFailureState = aResolveVerifiedUserSendFailureState(),
    displayThreadSummaries: Boolean = false,
    eventSink: (TimelineEvent) -> Unit = {},
): TimelineState {
    val focusedEventId = timelineItems.filterIsInstance<TimelineItem.Event>().getOrNull(focusedEventIndex)?.eventId
    val focusRequestState = if (focusedEventId != null) {
        FocusRequestState.Success(focusedEventId, focusedEventIndex)
    } else {
        FocusRequestState.None
    }
    return TimelineState(
        timelineItems = timelineItems,
        timelineMode = timelineMode,
        timelineRoomInfo = timelineRoomInfo,
        newEventState = NewEventState.None,
        isLive = isLive,
        focusRequestState = focusRequestState,
        messageShieldDialogData = messageShield?.let { MessageShieldData(it) },
        resolveVerifiedUserSendFailureState = resolveVerifiedUserSendFailureState,
        displayThreadSummaries = displayThreadSummaries,
        eventSink = eventSink,
    )
}

internal fun aTimelineItemList(content: TimelineItemEventContent): ImmutableList<TimelineItem> {
    return persistentListOf(
        // 3 items (First Middle Last) with isMine = false
        aTimelineItemEvent(
            isMine = false,
            content = content,
            groupPosition = TimelineItemGroupPosition.Last
        ),
        aTimelineItemEvent(
            isMine = false,
            content = content,
            groupPosition = TimelineItemGroupPosition.Middle,
            sendState = LocalEventSendState.Failed.Unknown("Message failed to send"),
        ),
        aTimelineItemEvent(
            isMine = false,
            content = content,
            groupPosition = TimelineItemGroupPosition.First
        ),
        // A state event on top of it
        aTimelineItemEvent(
            isMine = false,
            content = aTimelineItemStateEventContent(),
            groupPosition = TimelineItemGroupPosition.None
        ),
        // 3 items (First Middle Last) with isMine = true
        aTimelineItemEvent(
            isMine = true,
            content = content,
            groupPosition = TimelineItemGroupPosition.Last
        ),
        aTimelineItemEvent(
            isMine = true,
            content = content,
            groupPosition = TimelineItemGroupPosition.Middle,
            sendState = LocalEventSendState.Failed.Unknown("Message failed to send"),
        ),
        aTimelineItemEvent(
            isMine = true,
            content = content,
            groupPosition = TimelineItemGroupPosition.First
        ),
        // A grouped event on top of it
        aGroupedEvents(),
        // A day separator
        aTimelineItemDaySeparator(),
    )
}

fun aTimelineItemDaySeparator(): TimelineItem.Virtual {
    return TimelineItem.Virtual(
        id = UniqueId(UUID.randomUUID().toString()),
        model = aTimelineItemDaySeparatorModel("Today"),
    )
}

internal fun aTimelineItemEvent(
    eventId: EventId = EventId("\$" + Random.nextInt().toString()),
    transactionId: TransactionId? = null,
    isMine: Boolean = false,
    isEditable: Boolean = false,
    canBeRepliedTo: Boolean = false,
    senderDisplayName: String = USER_NAME_SENDER,
    displayNameAmbiguous: Boolean = false,
    content: TimelineItemEventContent = aTimelineItemTextContent(),
    groupPosition: TimelineItemGroupPosition = TimelineItemGroupPosition.None,
    sendState: LocalEventSendState? = null,
    inReplyTo: InReplyToDetails? = null,
    threadInfo: TimelineItemThreadInfo? = null,
    debugInfo: TimelineItemDebugInfo = aTimelineItemDebugInfo(),
    timelineItemReactions: TimelineItemReactions = aTimelineItemReactions(),
    readReceiptState: TimelineItemReadReceipts = aTimelineItemReadReceipts(),
    messageShield: MessageShield? = null,
): TimelineItem.Event {
    return TimelineItem.Event(
        id = UniqueId(UUID.randomUUID().toString()),
        eventId = eventId,
        transactionId = transactionId,
        senderId = UserId("@senderId:domain"),
        senderAvatar = AvatarData("@senderId:domain", USER_NAME_SENDER, size = AvatarSize.TimelineSender),
        content = content,
        reactionsState = timelineItemReactions,
        readReceiptState = readReceiptState,
        sentTime = "12:34",
        isMine = isMine,
        isEditable = isEditable,
        canBeRepliedTo = canBeRepliedTo,
        senderProfile = aProfileDetailsReady(
            displayName = senderDisplayName,
            displayNameAmbiguous = displayNameAmbiguous,
        ),
        groupPosition = groupPosition,
        localSendState = sendState,
        inReplyTo = inReplyTo,
        threadInfo = threadInfo,
        origin = null,
        timelineItemDebugInfoProvider = { debugInfo },
        messageShieldProvider = { messageShield },
        sendHandleProvider = { null },
        forwarder = null,
        forwarderProfile = null,
    )
}

fun aTimelineItemReactions(
    count: Int = 1,
    isHighlighted: Boolean = false,
): TimelineItemReactions {
    val emojis = arrayOf("👍️", "😀️", "😁️", "😆️", "😅️", "🤣️", "🥰️", "😇️", "😊️", "😉️", "🙃️", "🙂️", "😍️", "🤗️", "🤭️")
    return TimelineItemReactions(
        reactions = buildList {
            repeat(count) { index ->
                val key = emojis[index % emojis.size]
                add(
                    anAggregatedReaction(
                        key = key,
                        count = index + 1,
                        isHighlighted = isHighlighted
                    )
                )
            }
        }.toImmutableList()
    )
}

internal fun aTimelineItemDebugInfo(
    model: String = "Rust(Model())",
    originalJson: String? = null,
    latestEditedJson: String? = null,
) = TimelineItemDebugInfo(
    model,
    originalJson,
    latestEditedJson
)

internal fun aTimelineItemReadReceipts(
    receipts: List<ReadReceiptData> = emptyList(),
): TimelineItemReadReceipts {
    return TimelineItemReadReceipts(
        receipts = receipts.toImmutableList(),
    )
}

internal fun aGroupedEvents(
    id: UniqueId = UniqueId("0"),
    withReadReceipts: Boolean = false,
): TimelineItem.GroupedEvents {
    val event1 = aTimelineItemEvent(
        isMine = true,
        content = aTimelineItemStateEventContent(),
        groupPosition = TimelineItemGroupPosition.None,
        readReceiptState = TimelineItemReadReceipts(
            receipts = (if (withReadReceipts) listOf(aReadReceiptData(0)) else emptyList()).toImmutableList()
        ),
    )
    val event2 = aTimelineItemEvent(
        isMine = true,
        content = aTimelineItemStateEventContent(body = "Another state event"),
        groupPosition = TimelineItemGroupPosition.None,
        readReceiptState = TimelineItemReadReceipts(
            receipts = (if (withReadReceipts) listOf(aReadReceiptData(1)) else emptyList()).toImmutableList()
        ),
    )
    val events = listOf(event1, event2)
    return TimelineItem.GroupedEvents(
        id = id,
        events = events.toImmutableList(),
        aggregatedReadReceipts = events.flatMap { it.readReceiptState.receipts }.toImmutableList(),
    )
}

internal fun aTimelineRoomInfo(
    name: String = ROOM_NAME,
    isDm: Boolean = false,
    userHasPermissionToSendMessage: Boolean = true,
    pinnedEventIds: List<EventId> = emptyList(),
    typingNotificationState: TypingNotificationState = aTypingNotificationState(),
    predecessorRoom: PredecessorRoom? = null,
) = TimelineRoomInfo(
    isDm = isDm,
    name = name,
    userHasPermissionToSendMessage = userHasPermissionToSendMessage,
    userHasPermissionToSendReaction = true,
    roomCallState = aStandByCallState(),
    pinnedEventIds = pinnedEventIds.toImmutableList(),
    typingNotificationState = typingNotificationState,
    predecessorRoom = predecessorRoom,
)
