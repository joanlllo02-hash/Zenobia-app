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
import com.zenobia.app.libraries.push.impl.notifications.NotificationActionIds
import com.zenobia.app.libraries.push.impl.notifications.NotificationBroadcastReceiver
import com.zenobia.app.libraries.push.impl.notifications.model.InviteNotifiableEvent
import com.zenobia.app.libraries.ui.strings.CommonStrings
import com.zenobia.app.services.toolbox.api.strings.StringProvider
import com.zenobia.app.services.toolbox.api.systemclock.SystemClock

@Inject
class AcceptInvitationActionFactory(
    @ApplicationContext private val context: Context,
    private val actionIds: NotificationActionIds,
    private val stringProvider: StringProvider,
    private val clock: SystemClock,
) {
    fun create(inviteNotifiableEvent: InviteNotifiableEvent): NotificationCompat.Action? {
        if (!NotificationConfig.SHOW_ACCEPT_AND_DECLINE_INVITE_ACTIONS) return null
        val sessionId = inviteNotifiableEvent.sessionId.value
        val roomId = inviteNotifiableEvent.roomId.value
        val intent = Intent(context, NotificationBroadcastReceiver::class.java)
        intent.action = actionIds.join
        intent.data = createIgnoredUri("acceptInvite/$sessionId/$roomId")
        intent.putExtra(NotificationBroadcastReceiver.KEY_SESSION_ID, sessionId)
        intent.putExtra(NotificationBroadcastReceiver.KEY_ROOM_ID, roomId)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            clock.epochMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Action.Builder(
            CompoundDrawables.ic_compound_check,
            stringProvider.getString(CommonStrings.action_accept),
            pendingIntent
        ).build()
    }
}
