/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.startchat.impl.root

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.zenobia.app.features.startchat.api.ConfirmingStartDmWithMatrixUser
import com.zenobia.app.features.startchat.impl.userlist.UserListState
import com.zenobia.app.features.startchat.impl.userlist.aRecentDirectRoomList
import com.zenobia.app.features.startchat.impl.userlist.aUserListState
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.designsystem.theme.components.SearchBarResultState
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.user.MatrixUser
import com.zenobia.app.libraries.matrix.ui.components.aMatrixUser
import com.zenobia.app.libraries.usersearch.api.UserSearchResult
import kotlinx.collections.immutable.persistentListOf

open class StartChatStateProvider : PreviewParameterProvider<StartChatState> {
    override val values: Sequence<StartChatState>
        get() = sequenceOf(
            aCreateRoomRootState(),
            aCreateRoomRootState(
                startDmAction = AsyncAction.Loading,
                userListState = aMatrixUser().let {
                    aUserListState(
                        searchQuery = it.userId.value,
                        searchResults = SearchBarResultState.Results(persistentListOf(UserSearchResult(it, false))),
                        selectedUsers = listOf(it),
                        isSearchActive = true,
                    )
                }
            ),
            aCreateRoomRootState(
                startDmAction = AsyncAction.Failure(RuntimeException("error")),
                userListState = aMatrixUser().let {
                    aUserListState(
                        searchQuery = it.userId.value,
                        searchResults = SearchBarResultState.Results(persistentListOf(UserSearchResult(it, false))),
                        selectedUsers = listOf(it),
                        isSearchActive = true,
                    )
                }
            ),
            aCreateRoomRootState(
                userListState = aUserListState(
                    recentDirectRooms = aRecentDirectRoomList()
                )
            ),
            aCreateRoomRootState(
                startDmAction = aConfirmingStartDmWithMatrixUser()
            ),
        )
}

fun aConfirmingStartDmWithMatrixUser(
    matrixUser: MatrixUser = aMatrixUser(),
    isUserIdentityUnknown: Boolean = false
): ConfirmingStartDmWithMatrixUser {
    return ConfirmingStartDmWithMatrixUser(
        matrixUser,
        isUserIdentityUnknown
    )
}

fun aCreateRoomRootState(
    applicationName: String = "Element X Preview",
    userListState: UserListState = aUserListState(),
    startDmAction: AsyncAction<RoomId> = AsyncAction.Uninitialized,
    eventSink: (StartChatEvents) -> Unit = {},
) = StartChatState(
    applicationName = applicationName,
    userListState = userListState,
    startDmAction = startDmAction,
    eventSink = eventSink,
)
