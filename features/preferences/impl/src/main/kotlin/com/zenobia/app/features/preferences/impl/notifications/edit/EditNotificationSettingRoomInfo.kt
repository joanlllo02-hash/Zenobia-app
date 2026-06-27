/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.preferences.impl.notifications.edit

import com.zenobia.app.libraries.designsystem.components.avatar.AvatarData
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.room.RoomNotificationMode
import kotlinx.collections.immutable.ImmutableList

data class EditNotificationSettingRoomInfo(
    val roomId: RoomId,
    val name: String?,
    val heroesAvatar: ImmutableList<AvatarData>,
    val avatarData: AvatarData,
    val notificationMode: RoomNotificationMode?
)
