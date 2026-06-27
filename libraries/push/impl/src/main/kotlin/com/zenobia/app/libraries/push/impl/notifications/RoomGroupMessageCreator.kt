/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2021-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.push.impl.notifications

import android.app.Notification
import android.graphics.Bitmap
import coil3.ImageLoader
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarData
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarSize
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.core.ThreadId
import com.zenobia.app.libraries.push.api.notifications.NotificationBitmapLoader
import com.zenobia.app.libraries.push.impl.R
import com.zenobia.app.libraries.push.impl.notifications.factories.NotificationAccountParams
import com.zenobia.app.libraries.push.impl.notifications.factories.NotificationCreator
import com.zenobia.app.libraries.push.impl.notifications.factories.isSmartReplyError
import com.zenobia.app.libraries.push.impl.notifications.model.NotifiableMessageEvent
import com.zenobia.app.services.toolbox.api.strings.StringProvider

interface RoomGroupMessageCreator {
    suspend fun createRoomMessage(
        notificationAccountParams: NotificationAccountParams,
        events: List<NotifiableMessageEvent>,
        roomId: RoomId,
        threadId: ThreadId?,
        imageLoader: ImageLoader,
        existingNotification: Notification?,
    ): Notification
}

@ContributesBinding(AppScope::class)
class DefaultRoomGroupMessageCreator(
    private val bitmapLoader: NotificationBitmapLoader,
    private val stringProvider: StringProvider,
    private val notificationCreator: NotificationCreator,
) : RoomGroupMessageCreator {
    override suspend fun createRoomMessage(
        notificationAccountParams: NotificationAccountParams,
        events: List<NotifiableMessageEvent>,
        roomId: RoomId,
        threadId: ThreadId?,
        imageLoader: ImageLoader,
        existingNotification: Notification?,
    ): Notification {
        val lastKnownRoomEvent = events.last()
        val roomName = lastKnownRoomEvent.roomName ?: lastKnownRoomEvent.senderDisambiguatedDisplayName ?: "Room name (${roomId.value.take(8)}…)"
        val roomIsGroup = !lastKnownRoomEvent.roomIsDm

        val tickerText = if (roomIsGroup) {
            stringProvider.getString(R.string.notification_ticker_text_group, roomName, events.last().senderDisambiguatedDisplayName, events.last().description)
        } else {
            stringProvider.getString(R.string.notification_ticker_text_dm, events.last().senderDisambiguatedDisplayName, events.last().description)
        }

        val largeBitmap = getRoomBitmap(events, imageLoader)

        val lastMessageTimestamp = events.last().timestamp
        val smartReplyErrors = events.filter { it.isSmartReplyError() }
        val roomIsDm = !roomIsGroup
        return notificationCreator.createMessagesListNotification(
            notificationAccountParams = notificationAccountParams,
            roomInfo = RoomEventGroupInfo(
                sessionId = notificationAccountParams.user.userId,
                roomId = roomId,
                roomDisplayName = roomName,
                isDm = roomIsDm,
                hasSmartReplyError = smartReplyErrors.isNotEmpty(),
                shouldBing = events.any { it.noisy },
                customSound = events.last().soundName,
                isUpdated = events.last().let { it.isUpdated || it.outGoingMessage },
            ),
            threadId = threadId,
            largeIcon = largeBitmap,
            lastMessageTimestamp = lastMessageTimestamp,
            tickerText = tickerText,
            existingNotification = existingNotification,
            imageLoader = imageLoader,
            events = events,
        )
    }

    private suspend fun getRoomBitmap(
        events: List<NotifiableMessageEvent>,
        imageLoader: ImageLoader,
    ): Bitmap? {
        // Use the last event (most recent?)
        val event = events.reversed().firstOrNull { it.roomAvatarPath != null }
            ?: events.reversed().firstOrNull()
        return event?.let { event ->
            bitmapLoader.getRoomBitmap(
                avatarData = AvatarData(
                    id = event.roomId.value,
                    name = event.roomName,
                    url = event.roomAvatarPath,
                    size = AvatarSize.RoomDetailsHeader,
                ),
                imageLoader = imageLoader,
            )
        }
    }
}
