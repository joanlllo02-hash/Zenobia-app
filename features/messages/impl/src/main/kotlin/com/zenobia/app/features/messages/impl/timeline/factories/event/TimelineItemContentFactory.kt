/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.timeline.factories.event

import dev.zacsweers.metro.Inject
import com.zenobia.app.features.location.api.Location
import com.zenobia.app.features.messages.impl.timeline.model.event.RtcNotificationState
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemEventContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemLegacyCallInviteContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemLocationContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemRtcNotificationContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemUnknownContent
import com.zenobia.app.libraries.dateformatter.api.DateFormatter
import com.zenobia.app.libraries.dateformatter.api.DateFormatterMode
import com.zenobia.app.libraries.matrix.api.core.EventId
import com.zenobia.app.libraries.matrix.api.core.SessionId
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.api.timeline.item.event.CallNotifyContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.EventContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.EventTimelineItem
import com.zenobia.app.libraries.matrix.api.timeline.item.event.FailedToParseMessageLikeContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.FailedToParseStateContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.LegacyCallInviteContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.LiveLocationContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.MessageContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.PollContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.ProfileChangeContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.ProfileDetails
import com.zenobia.app.libraries.matrix.api.timeline.item.event.RedactedContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.RoomMembershipContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.StateContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.StickerContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.UnableToDecryptContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.UnknownContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.getDisambiguatedDisplayName
import com.zenobia.app.libraries.ui.strings.CommonStrings
import com.zenobia.app.services.toolbox.api.strings.StringProvider

@Inject
class TimelineItemContentFactory(
    private val messageFactory: TimelineItemContentMessageFactory,
    private val redactedMessageFactory: TimelineItemContentRedactedFactory,
    private val stickerFactory: TimelineItemContentStickerFactory,
    private val pollFactory: TimelineItemContentPollFactory,
    private val utdFactory: TimelineItemContentUTDFactory,
    private val roomMembershipFactory: TimelineItemContentRoomMembershipFactory,
    private val profileChangeFactory: TimelineItemContentProfileChangeFactory,
    private val stateFactory: TimelineItemContentStateFactory,
    private val failedToParseMessageFactory: TimelineItemContentFailedToParseMessageFactory,
    private val failedToParseStateFactory: TimelineItemContentFailedToParseStateFactory,
    private val sessionId: SessionId,
    private val dateFormatter: DateFormatter,
    private val stringProvider: StringProvider,
) {
    suspend fun create(eventTimelineItem: EventTimelineItem): TimelineItemEventContent {
        return create(
            itemContent = eventTimelineItem.content,
            eventId = eventTimelineItem.eventId,
            isEditable = eventTimelineItem.isEditable,
            sender = eventTimelineItem.sender,
            senderProfile = eventTimelineItem.senderProfile,
        )
    }

    suspend fun create(
        itemContent: EventContent,
        eventId: EventId?,
        isEditable: Boolean,
        sender: UserId,
        senderProfile: ProfileDetails,
    ): TimelineItemEventContent {
        val isOutgoing = sessionId == sender
        return when (itemContent) {
            is FailedToParseMessageLikeContent -> failedToParseMessageFactory.create(itemContent)
            is FailedToParseStateContent -> failedToParseStateFactory.create(itemContent)
            is MessageContent -> {
                messageFactory.create(
                    senderId = sender,
                    senderProfile = senderProfile,
                    content = itemContent,
                    eventId = eventId,
                )
            }
            is ProfileChangeContent -> {
                val senderDisambiguatedDisplayName = senderProfile.getDisambiguatedDisplayName(sender)
                profileChangeFactory.create(itemContent, isOutgoing, sender, senderDisambiguatedDisplayName)
            }
            is RedactedContent -> redactedMessageFactory.create(itemContent)
            is RoomMembershipContent -> {
                val senderDisambiguatedDisplayName = senderProfile.getDisambiguatedDisplayName(sender)
                roomMembershipFactory.create(itemContent, isOutgoing, sender, senderDisambiguatedDisplayName)
            }
            is LegacyCallInviteContent -> TimelineItemLegacyCallInviteContent
            is StateContent -> {
                val senderDisambiguatedDisplayName = senderProfile.getDisambiguatedDisplayName(sender)
                stateFactory.create(itemContent, isOutgoing, sender, senderDisambiguatedDisplayName)
            }
            is StickerContent -> stickerFactory.create(itemContent)
            is PollContent -> pollFactory.create(eventId, isEditable, isOutgoing, itemContent)
            is UnableToDecryptContent -> utdFactory.create(itemContent)
            is CallNotifyContent -> TimelineItemRtcNotificationContent(
                callIntent = itemContent.callIntent,
                state = if (itemContent.declinedBy.isEmpty()) {
                    RtcNotificationState.Started
                } else {
                    RtcNotificationState.Declined(itemContent.declinedBy.any { it == sessionId })
                }
            )
            is UnknownContent -> TimelineItemUnknownContent
            is LiveLocationContent -> {
                val lastKnownLocation = itemContent.locations.mapNotNull { beacon ->
                    Location.fromGeoUri(beacon.geoUri)
                }.lastOrNull()

                val endsAt = dateFormatter.format(
                    timestamp = itemContent.endTimestamp,
                    mode = DateFormatterMode.TimeOnly
                )
                // Always create content, location can be null for "loading/waiting" state
                TimelineItemLocationContent(
                    description = itemContent.description?.trimEnd(),
                    assetType = itemContent.assetType,
                    senderId = sender,
                    senderProfile = senderProfile,
                    mode = TimelineItemLocationContent.Mode.Live(
                        lastKnownLocation = lastKnownLocation,
                        isActive = itemContent.isLive,
                        endsAt = stringProvider.getString(CommonStrings.common_ends_at, endsAt),
                        endTimestamp = itemContent.endTimestamp,
                        isOwnUser = sessionId == sender
                    ),
                )
            }
        }
    }
}
