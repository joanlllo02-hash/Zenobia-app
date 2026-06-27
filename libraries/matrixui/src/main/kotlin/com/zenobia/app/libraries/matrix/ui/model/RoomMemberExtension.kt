/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.ui.model

import com.zenobia.app.libraries.designsystem.components.avatar.AvatarData
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarSize
import com.zenobia.app.libraries.matrix.api.room.RoomMember

fun RoomMember.getAvatarData(size: AvatarSize) = AvatarData(
    id = userId.value,
    name = displayName,
    url = avatarUrl,
    size = size,
)
