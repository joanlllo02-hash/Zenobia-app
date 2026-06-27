/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.timeline.groups

import com.zenobia.app.features.messages.impl.timeline.model.TimelineItem
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemAudioContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemEncryptedContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemFileContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemImageContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemLegacyCallInviteContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemLocationContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemPollContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemProfileChangeContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemRedactedContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemRoomMembershipContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemRtcNotificationContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemStateEventContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemStickerContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemTextBasedContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemUnknownContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemVideoContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemVoiceContent
import com.zenobia.app.libraries.matrix.api.timeline.MatrixTimelineItem
import com.zenobia.app.libraries.matrix.api.timeline.item.event.CallNotifyContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.FailedToParseMessageLikeContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.FailedToParseStateContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.LegacyCallInviteContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.LiveLocationContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.MessageContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.PollContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.ProfileChangeContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.RedactedContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.RoomMembershipContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.StateContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.StickerContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.UnableToDecryptContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.UnknownContent

/**
 * Return true if the Event can be grouped in a collapse/expand block
 * When [canBeGrouped] returns a value, [canBeDisplayedInBubbleBlock] MUST return the opposite value.
 * Since the receiving type are not the same, the two functions exist.
 */
internal fun TimelineItem.Event.canBeGrouped(): Boolean {
    return when (content) {
        is TimelineItemTextBasedContent,
        is TimelineItemEncryptedContent,
        is TimelineItemImageContent,
        is TimelineItemStickerContent,
        is TimelineItemFileContent,
        is TimelineItemVideoContent,
        is TimelineItemAudioContent,
        is TimelineItemLocationContent,
        is TimelineItemPollContent,
        is TimelineItemVoiceContent,
        TimelineItemRedactedContent,
        TimelineItemUnknownContent,
        is TimelineItemLegacyCallInviteContent,
        is TimelineItemRtcNotificationContent -> false
        is TimelineItemProfileChangeContent,
        is TimelineItemRoomMembershipContent,
        is TimelineItemStateEventContent -> true
    }
}

/**
 * Return true if the Event can be grouped in a block of message bubbles.
 * When [canBeDisplayedInBubbleBlock] returns a value, [canBeGrouped] MUST return the opposite value.
 * Since the receiving type are not the same, the two functions exist.
 */
internal fun MatrixTimelineItem.Event.canBeDisplayedInBubbleBlock(): Boolean {
    return when (event.content) {
        // Can be grouped
        is FailedToParseMessageLikeContent,
        is MessageContent,
        RedactedContent,
        is StickerContent,
        is PollContent,
        is UnableToDecryptContent,
        is LiveLocationContent -> true
        // Can't be grouped
        is FailedToParseStateContent,
        is ProfileChangeContent,
        is RoomMembershipContent,
        UnknownContent,
        is LegacyCallInviteContent,
        is CallNotifyContent,
        is StateContent -> false
    }
}
