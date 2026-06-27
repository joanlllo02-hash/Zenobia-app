/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.previewutils.room

import com.zenobia.app.libraries.designsystem.preview.SPACE_NAME
import com.zenobia.app.libraries.matrix.api.core.RoomAlias
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.room.CurrentUserMembership
import com.zenobia.app.libraries.matrix.api.room.RoomType
import com.zenobia.app.libraries.matrix.api.room.join.JoinRule
import com.zenobia.app.libraries.matrix.api.spaces.SpaceRoom
import com.zenobia.app.libraries.matrix.api.user.MatrixUser
import kotlinx.collections.immutable.toImmutableList

fun aSpaceRoom(
    rawName: String? = null,
    displayName: String = SPACE_NAME,
    avatarUrl: String? = null,
    canonicalAlias: RoomAlias? = null,
    childrenCount: Int = 0,
    guestCanJoin: Boolean = false,
    heroes: List<MatrixUser> = emptyList(),
    joinRule: JoinRule? = null,
    numJoinedMembers: Int = 0,
    roomId: RoomId = RoomId("!roomId:example.com"),
    roomType: RoomType = RoomType.Space,
    state: CurrentUserMembership? = null,
    topic: String? = null,
    worldReadable: Boolean = false,
    isDirect: Boolean? = null,
    isDm: Boolean? = null,
    via: List<String> = emptyList(),
) = SpaceRoom(
    rawName = rawName,
    displayName = displayName,
    avatarUrl = avatarUrl,
    canonicalAlias = canonicalAlias,
    childrenCount = childrenCount,
    guestCanJoin = guestCanJoin,
    heroes = heroes.toImmutableList(),
    joinRule = joinRule,
    numJoinedMembers = numJoinedMembers,
    roomId = roomId,
    roomType = roomType,
    state = state,
    topic = topic,
    worldReadable = worldReadable,
    via = via.toImmutableList(),
    isDirect = isDirect,
    isDm = isDm,
)
