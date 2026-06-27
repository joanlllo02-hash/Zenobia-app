/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.roomdetailsedit.api

import com.zenobia.app.libraries.matrix.api.room.StateEventType
import com.zenobia.app.libraries.matrix.api.room.powerlevels.RoomPermissions

data class RoomDetailsEditPermissions(
    val canEditName: Boolean,
    val canEditTopic: Boolean,
    val canEditAvatar: Boolean,
) {
    val hasAny = canEditName ||
        canEditTopic ||
        canEditAvatar

    companion object {
        val DEFAULT = RoomDetailsEditPermissions(
            canEditName = false,
            canEditTopic = false,
            canEditAvatar = false,
        )
    }
}

fun RoomPermissions.roomDetailsEditPermissions(): RoomDetailsEditPermissions {
    return RoomDetailsEditPermissions(
        canEditName = canOwnUserSendState(StateEventType.RoomName),
        canEditTopic = canOwnUserSendState(StateEventType.RoomTopic),
        canEditAvatar = canOwnUserSendState(StateEventType.RoomAvatar),
    )
}
