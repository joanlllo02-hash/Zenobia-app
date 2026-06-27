/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.push.impl.notifications.factories.action

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import dev.zacsweers.metro.Inject
import com.zenobia.app.appconfig.NotificationConfig
import com.zenobia.app.libraries.androidutils.uri.createIgnoredUri
import com.zenobia.app.libraries.designsystem.icons.CompoundDrawables
import com.zenobia.app.libraries.di.annotations.ApplicationContext
import com.zenobia.app.libraries.matrix.api.core.ThreadId
import com.zenobia.app.libraries.push.impl.R
import com.zenobia.app.libraries.push.impl.notifications.NotificationActionIds
import com.zenobia.app.libraries.push.impl.notifications.NotificationBroadcastReceiver
import com.zenobia.app.libraries.push.impl.notifications.RoomEventGroupInfo
import com.zenobia.app.services.toolbox.api.strings.StringProvider
import com.zenobia.app.services.toolbox.api.systemclock.SystemClock

@Inject
class MarkAsReadActionFactory(
    @ApplicationContext private val context: Context,
    private val actionIds: NotificationActionIds,
    private val stringProvider: StringProvider,
    private val clock: SystemClock,
) {
    fun create(roomInfo: RoomEventGroupInfo, threadId: ThreadId?): NotificationCompat.Action? {
        if (!NotificationConfig.SHOW_MARK_AS_READ_ACTION) return null
        val sessionId = roomInfo.sessionId.value
        val roomId = roomInfo.roomId.value
        val intent = Intent(context, NotificationBroadcastReceiver::class.java)
        intent.action = actionIds.markRoomRead
        intent.data = createIgnoredUri("markRead/$sessionId/$roomId" + threadId?.let { "/$it" }.orEmpty())
        intent.putExtra(NotificationBroadcastReceiver.KEY_SESSION_ID, sessionId)
        intent.putExtra(NotificationBroadcastReceiver.KEY_ROOM_ID, roomId)
        threadId?.let { intent.putExtra(NotificationBroadcastReceiver.KEY_THREAD_ID, threadId.value) }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            clock.epochMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Action.Builder(
            CompoundDrawables.ic_compound_mark_as_read,
            stringProvider.getString(R.string.notification_room_action_mark_as_read),
            pendingIntent
        )
            .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_MARK_AS_READ)
            .setShowsUserInterface(false)
            .build()
    }
}
