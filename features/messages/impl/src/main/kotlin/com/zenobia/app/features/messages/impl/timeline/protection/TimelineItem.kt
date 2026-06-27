/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.timeline.protection

import com.zenobia.app.features.messages.impl.timeline.model.TimelineItem
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemAudioContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemEmoteContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemEncryptedContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemFileContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemImageContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemLegacyCallInviteContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemLocationContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemNoticeContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemPollContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemProfileChangeContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemRedactedContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemRoomMembershipContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemRtcNotificationContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemStateEventContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemStickerContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemTextContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemUnknownContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemVideoContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemVoiceContent

/**
 * Return true if the event must be hidden by default when the setting to hide images and videos is enabled.
 */
fun TimelineItem.mustBeProtected(): Boolean {
    return when (this) {
        is TimelineItem.Event -> {
            if (isMine) {
                false
            } else {
                when (content) {
                    is TimelineItemImageContent,
                    is TimelineItemVideoContent,
                    is TimelineItemStickerContent -> true
                    is TimelineItemAudioContent,
                    is TimelineItemRtcNotificationContent,
                    is TimelineItemEncryptedContent,
                    is TimelineItemFileContent,
                    TimelineItemLegacyCallInviteContent,
                    is TimelineItemLocationContent,
                    is TimelineItemPollContent,
                    TimelineItemRedactedContent,
                    is TimelineItemProfileChangeContent,
                    is TimelineItemRoomMembershipContent,
                    is TimelineItemStateEventContent,
                    is TimelineItemEmoteContent,
                    is TimelineItemNoticeContent,
                    is TimelineItemTextContent,
                    TimelineItemUnknownContent,
                    is TimelineItemVoiceContent -> false
                }
            }
        }
        is TimelineItem.Virtual -> false
        is TimelineItem.GroupedEvents -> false
    }
}
