/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.startchat.impl.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.zenobia.app.features.startchat.impl.userlist.UserListEvents
import com.zenobia.app.features.startchat.impl.userlist.UserListState
import com.zenobia.app.features.startchat.impl.userlist.UserListStateProvider
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarSize
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.HorizontalDivider
import com.zenobia.app.libraries.designsystem.theme.components.ListSectionHeader
import com.zenobia.app.libraries.matrix.api.user.MatrixUser
import com.zenobia.app.libraries.matrix.ui.components.CheckableUserRow
import com.zenobia.app.libraries.matrix.ui.components.CheckableUserRowData
import com.zenobia.app.libraries.matrix.ui.components.SelectedUsersRowList
import com.zenobia.app.libraries.matrix.ui.model.getAvatarData
import com.zenobia.app.libraries.matrix.ui.model.getBestName
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun UserListView(
    state: UserListState,
    onSelectUser: (MatrixUser) -> Unit,
    onDeselectUser: (MatrixUser) -> Unit,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
) {
    Column(
        modifier = modifier,
    ) {
        SearchUserBar(
            modifier = Modifier.fillMaxWidth(),
            queryState = state.searchQuery,
            resultState = state.searchResults,
            selectedUsers = state.selectedUsers,
            active = state.isSearchActive,
            showLoader = state.showSearchLoader,
            isMultiSelectionEnable = state.isMultiSelectionEnabled,
            showBackButton = showBackButton,
            onActiveChange = { state.eventSink(UserListEvents.OnSearchActiveChanged(it)) },
            onUserSelect = {
                state.eventSink(UserListEvents.AddToSelection(it))
                onSelectUser(it)
            },
            onUserDeselect = {
                state.eventSink(UserListEvents.RemoveFromSelection(it))
                onDeselectUser(it)
            },
        )

        if (state.isMultiSelectionEnabled && !state.isSearchActive && state.selectedUsers.isNotEmpty()) {
            SelectedUsersRowList(
                contentPadding = PaddingValues(16.dp),
                selectedUsers = state.selectedUsers,
                autoScroll = true,
                onUserRemove = {
                    state.eventSink(UserListEvents.RemoveFromSelection(it))
                    onDeselectUser(it)
                },
            )
        }
        if (!state.isSearchActive && state.recentDirectRooms.isNotEmpty()) {
            LazyColumn {
                item {
                    ListSectionHeader(
                        title = stringResource(id = CommonStrings.common_suggestions),
                        hasDivider = false,
                    )
                }
                state.recentDirectRooms.forEachIndexed { index, recentDirectRoom ->
                    item {
                        val isSelected = state.selectedUsers.any {
                            recentDirectRoom.matrixUser.userId == it.userId
                        }
                        CheckableUserRow(
                            checked = isSelected,
                            onCheckedChange = {
                                if (isSelected) {
                                    state.eventSink(UserListEvents.RemoveFromSelection(recentDirectRoom.matrixUser))
                                    onDeselectUser(recentDirectRoom.matrixUser)
                                } else {
                                    state.eventSink(UserListEvents.AddToSelection(recentDirectRoom.matrixUser))
                                    onSelectUser(recentDirectRoom.matrixUser)
                                }
                            },
                            data = CheckableUserRowData.Resolved(
                                avatarData = recentDirectRoom.matrixUser.getAvatarData(AvatarSize.UserListItem),
                                name = recentDirectRoom.matrixUser.getBestName(),
                                subtext = recentDirectRoom.matrixUser.userId.value,
                            ),
                        )
                        if (index < state.recentDirectRooms.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun UserListViewPreview(@PreviewParameter(UserListStateProvider::class) state: UserListState) = ZenobiaPreview {
    UserListView(
        state = state,
        onSelectUser = {},
        onDeselectUser = {},
    )
}
