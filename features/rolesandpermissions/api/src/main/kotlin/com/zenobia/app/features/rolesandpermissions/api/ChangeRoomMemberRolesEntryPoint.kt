/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.rolesandpermissions.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.zenobia.app.libraries.architecture.FeatureEntryPoint
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.room.JoinedRoom

fun interface ChangeRoomMemberRolesEntryPoint : FeatureEntryPoint {
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        room: JoinedRoom,
        listType: ChangeRoomMemberRolesListType,
    ): Node

    interface NodeProxy {
        val roomId: RoomId
        suspend fun waitForCompletion(): Boolean
    }
}

enum class ChangeRoomMemberRolesListType {
    SelectNewOwnersWhenLeaving,
    Admins,
    Moderators
}
