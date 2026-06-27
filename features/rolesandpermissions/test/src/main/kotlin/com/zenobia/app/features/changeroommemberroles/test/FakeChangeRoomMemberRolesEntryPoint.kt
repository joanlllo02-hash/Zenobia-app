/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.changeroommemberroles.test

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.zenobia.app.features.rolesandpermissions.api.ChangeRoomMemberRolesEntryPoint
import com.zenobia.app.features.rolesandpermissions.api.ChangeRoomMemberRolesListType
import com.zenobia.app.libraries.matrix.api.room.JoinedRoom
import com.zenobia.app.tests.testutils.lambda.lambdaError

class FakeChangeRoomMemberRolesEntryPoint : ChangeRoomMemberRolesEntryPoint {
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        room: JoinedRoom,
        listType: ChangeRoomMemberRolesListType,
    ): Node {
        lambdaError()
    }
}
