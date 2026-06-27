/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.call.impl

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import com.zenobia.app.features.call.api.CallData
import com.zenobia.app.features.call.api.ElementCallEntryPoint
import com.zenobia.app.features.call.impl.notifications.CallNotificationData
import com.zenobia.app.features.call.impl.utils.ActiveCallManager
import com.zenobia.app.features.call.impl.utils.IntentProvider
import com.zenobia.app.libraries.di.annotations.ApplicationContext
import com.zenobia.app.libraries.matrix.api.core.EventId
import com.zenobia.app.libraries.matrix.api.core.UserId

@ContributesBinding(AppScope::class)
class DefaultElementCallEntryPoint(
    @ApplicationContext private val context: Context,
    private val activeCallManager: ActiveCallManager,
) : ElementCallEntryPoint {
    companion object {
        const val EXTRA_CALL_TYPE = "EXTRA_CALL_TYPE"
        const val REQUEST_CODE = 2255
    }

    override fun startCall(callData: CallData) {
        context.startActivity(IntentProvider.createIntent(context, callData))
    }

    override suspend fun handleIncomingCall(
        callData: CallData,
        eventId: EventId,
        senderId: UserId,
        roomName: String?,
        senderName: String?,
        avatarUrl: String?,
        timestamp: Long,
        expirationTimestamp: Long,
        notificationChannelId: String,
        textContent: String?,
    ) {
        val incomingCallNotificationData = CallNotificationData(
            sessionId = callData.sessionId,
            roomId = callData.roomId,
            eventId = eventId,
            senderId = senderId,
            roomName = roomName,
            senderName = senderName,
            avatarUrl = avatarUrl,
            timestamp = timestamp,
            expirationTimestamp = expirationTimestamp,
            notificationChannelId = notificationChannelId,
            textContent = textContent,
            audioOnly = callData.isAudioCall,
        )
        activeCallManager.registerIncomingCall(notificationData = incomingCallNotificationData)
    }
}
