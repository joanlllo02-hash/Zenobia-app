/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl

import com.zenobia.app.features.messages.impl.attachments.Attachment
import com.zenobia.app.libraries.matrix.api.core.EventId
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.core.ThreadId
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.api.timeline.item.TimelineItemDebugInfo
import kotlinx.collections.immutable.ImmutableList

interface MessagesNavigator {
    fun navigateToEventDebugInfo(eventId: EventId?, debugInfo: TimelineItemDebugInfo)
    fun forwardEvent(eventId: EventId)
    fun navigateToReportMessage(eventId: EventId, senderId: UserId)
    fun navigateToEditPoll(eventId: EventId)
    fun navigateToPreviewAttachments(attachments: ImmutableList<Attachment>, inReplyToEventId: EventId?)
    fun navigateToRoom(roomId: RoomId, eventId: EventId?, serverNames: List<String>)
    fun navigateToMember(userId: UserId)
    fun navigateToThread(threadRootId: ThreadId, focusedEventId: EventId?)
    fun navigateToDeveloperSettings()
    fun navigateToCurrentLiveLocation()
    fun close()
}
