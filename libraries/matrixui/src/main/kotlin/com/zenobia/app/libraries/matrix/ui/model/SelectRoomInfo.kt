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
import com.zenobia.app.libraries.matrix.api.core.RoomAlias
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.room.RoomInfo
import com.zenobia.app.libraries.matrix.api.roomlist.RoomSummary
import com.zenobia.app.libraries.matrix.api.user.MatrixUser
import kotlinx.collections.immutable.ImmutableList

data class SelectRoomInfo(
    val roomId: RoomId,
    val name: String?,
    val canonicalAlias: RoomAlias?,
    val avatarUrl: String?,
    val heroes: ImmutableList<MatrixUser>,
    val isTombstoned: Boolean,
) {
    fun getAvatarData(size: AvatarSize) = AvatarData(
        id = roomId.value,
        name = name,
        url = avatarUrl,
        size = size,
    )
}

fun RoomSummary.toSelectRoomInfo() = info.toSelectRoomInfo()

fun RoomInfo.toSelectRoomInfo() = SelectRoomInfo(
    roomId = id,
    name = name,
    avatarUrl = avatarUrl,
    heroes = heroes,
    canonicalAlias = canonicalAlias,
    isTombstoned = successorRoom != null,
)
