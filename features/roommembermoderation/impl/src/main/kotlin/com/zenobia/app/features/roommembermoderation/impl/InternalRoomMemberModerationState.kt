/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.roommembermoderation.impl

import com.zenobia.app.features.roommembermoderation.api.ModerationActionState
import com.zenobia.app.features.roommembermoderation.api.RoomMemberModerationEvents
import com.zenobia.app.features.roommembermoderation.api.RoomMemberModerationPermissions
import com.zenobia.app.features.roommembermoderation.api.RoomMemberModerationState
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.matrix.api.user.MatrixUser
import kotlinx.collections.immutable.ImmutableList

data class InternalRoomMemberModerationState(
    override val permissions: RoomMemberModerationPermissions,
    val selectedUser: MatrixUser?,
    val actions: ImmutableList<ModerationActionState>,
    val kickUserAsyncAction: AsyncAction<Unit>,
    val banUserAsyncAction: AsyncAction<Unit>,
    val unbanUserAsyncAction: AsyncAction<Unit>,
    override val eventSink: (RoomMemberModerationEvents) -> Unit,
) : RoomMemberModerationState {
    val canDisplayActions = actions.isNotEmpty()
}
