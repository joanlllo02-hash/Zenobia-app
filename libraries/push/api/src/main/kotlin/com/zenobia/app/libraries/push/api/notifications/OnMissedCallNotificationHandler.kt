/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.push.api.notifications

import com.zenobia.app.libraries.matrix.api.core.EventId
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.core.SessionId

/**
 * Handles missed calls by creating a new notification.
 */
interface OnMissedCallNotificationHandler {
    /**
     * Adds a missed call notification.
     */
    suspend fun addMissedCallNotification(
        sessionId: SessionId,
        roomId: RoomId,
        eventId: EventId,
    )
}
