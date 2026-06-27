/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.preferences.impl.blockedusers

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.matrix.api.user.MatrixUser
import com.zenobia.app.libraries.matrix.ui.components.aMatrixUserList
import kotlinx.collections.immutable.toImmutableList

class BlockedUsersStateProvider : PreviewParameterProvider<BlockedUsersState> {
    override val values: Sequence<BlockedUsersState>
        get() = sequenceOf(
            aBlockedUsersState(),
            aBlockedUsersState(blockedUsers = aMatrixUserList().map { it.copy(displayName = null, avatarUrl = null) }),
            aBlockedUsersState(blockedUsers = emptyList()),
            aBlockedUsersState(unblockUserAction = AsyncAction.ConfirmingNoParams),
            aBlockedUsersState(unblockUserAction = AsyncAction.Loading),
            aBlockedUsersState(unblockUserAction = AsyncAction.Failure(RuntimeException("Failed to unblock user"))),
            aBlockedUsersState(unblockUserAction = AsyncAction.Success(Unit)),
        )
}

internal fun aBlockedUsersState(
    blockedUsers: List<MatrixUser> = aMatrixUserList(),
    unblockUserAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    eventSink: (BlockedUsersEvents) -> Unit = {},
): BlockedUsersState {
    return BlockedUsersState(
        blockedUsers = blockedUsers.toImmutableList(),
        unblockUserAction = unblockUserAction,
        eventSink = eventSink,
    )
}
