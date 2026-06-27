/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.startchat.impl.root

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.features.startchat.api.ConfirmingStartDmWithMatrixUser
import com.zenobia.app.features.startchat.impl.R
import com.zenobia.app.features.startchat.impl.components.UserListView
import com.zenobia.app.libraries.androidutils.ui.hideKeyboardAndAwaitAnimation
import com.zenobia.app.libraries.designsystem.components.async.AsyncActionView
import com.zenobia.app.libraries.designsystem.components.async.AsyncActionViewDefaults
import com.zenobia.app.libraries.designsystem.components.button.BackButton
import com.zenobia.app.libraries.designsystem.icons.CompoundDrawables
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.Icon
import com.zenobia.app.libraries.designsystem.theme.components.ListSectionHeader
import com.zenobia.app.libraries.designsystem.theme.components.Scaffold
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.designsystem.theme.components.TopAppBar
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.ui.components.CreateDmConfirmationBottomSheet
import com.zenobia.app.libraries.matrix.ui.components.MatrixUserRow
import com.zenobia.app.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@Composable
fun StartChatView(
    state: StartChatState,
    onCloseClick: () -> Unit,
    onNewRoomClick: () -> Unit,
    onOpenDM: (RoomId) -> Unit,
    onInviteFriendsClick: () -> Unit,
    onJoinByAddressClick: () -> Unit,
    onRoomDirectorySearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxWidth(),
        topBar = {
            if (!state.userListState.isSearchActive) {
                CreateRoomRootViewTopBar(onCloseClick = onCloseClick)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val view = LocalView.current

            UserListView(
                modifier = Modifier.fillMaxWidth(),
                // Do not render suggestions in this case, the suggestion will be rendered
                // by CreateRoomActionButtonsList
                state = state.userListState.copy(
                    recentDirectRooms = persistentListOf(),
                ),
                onSelectUser = {
                    coroutineScope.launch {
                        view.hideKeyboardAndAwaitAnimation()
                        state.eventSink(StartChatEvents.StartDM(it))
                    }
                },
                onDeselectUser = { },
            )

            if (!state.userListState.isSearchActive) {
                CreateRoomActionButtonsList(
                    state = state,
                    onNewRoomClick = onNewRoomClick,
                    onInvitePeopleClick = onInviteFriendsClick,
                    onJoinByAddressClick = onJoinByAddressClick,
                    onRoomDirectorySearchClick = onRoomDirectorySearchClick,
                    onDmClick = onOpenDM,
                )
            }
        }
    }

    AsyncActionView(
        async = state.startDmAction,
        progressDialog = {
            AsyncActionViewDefaults.ProgressDialog(
                progressText = stringResource(CommonStrings.common_starting_chat),
            )
        },
        onSuccess = { onOpenDM(it) },
        errorMessage = { stringResource(R.string.screen_start_chat_error_starting_chat) },
        onRetry = {
            state.userListState.selectedUsers.firstOrNull()
                ?.let { state.eventSink(StartChatEvents.StartDM(it)) }
            // Cancel start DM if there is no more selected user (should not happen)
                ?: state.eventSink(StartChatEvents.CancelStartDM)
        },
        onErrorDismiss = { state.eventSink(StartChatEvents.CancelStartDM) },
        confirmationDialog = { data ->
            if (data is ConfirmingStartDmWithMatrixUser) {
                CreateDmConfirmationBottomSheet(
                    matrixUser = data.matrixUser,
                    isUserIdentityUnknown = data.isUserIdentityUnknown,
                    onSendInvite = {
                        state.eventSink(StartChatEvents.StartDM(data.matrixUser))
                    },
                    onDismiss = {
                        state.eventSink(StartChatEvents.CancelStartDM)
                    },
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateRoomRootViewTopBar(
    onCloseClick: () -> Unit,
) {
    TopAppBar(
        titleStr = stringResource(id = CommonStrings.action_start_chat),
        navigationIcon = {
            BackButton(
                imageVector = CompoundIcons.Close(),
                onClick = onCloseClick,
            )
        }
    )
}

@Composable
private fun CreateRoomActionButtonsList(
    state: StartChatState,
    onNewRoomClick: () -> Unit,
    onInvitePeopleClick: () -> Unit,
    onJoinByAddressClick: () -> Unit,
    onRoomDirectorySearchClick: () -> Unit,
    onDmClick: (RoomId) -> Unit,
) {
    LazyColumn {
        item {
            CreateRoomActionButton(
                iconRes = CompoundDrawables.ic_compound_plus,
                text = stringResource(id = R.string.screen_create_room_action_create_room),
                onClick = onNewRoomClick,
            )
        }
        item {
            CreateRoomActionButton(
                iconRes = CompoundDrawables.ic_compound_list_bulleted,
                text = stringResource(id = R.string.screen_room_directory_search_title),
                onClick = onRoomDirectorySearchClick,
            )
        }
        item {
            CreateRoomActionButton(
                iconRes = CompoundDrawables.ic_compound_share_android,
                text = stringResource(id = CommonStrings.action_invite_friends_to_app, state.applicationName),
                onClick = onInvitePeopleClick,
            )
        }
        item {
            CreateRoomActionButton(
                iconRes = CompoundDrawables.ic_compound_room,
                text = stringResource(R.string.screen_start_chat_join_room_by_address_action),
                onClick = onJoinByAddressClick,
            )
        }
        if (state.userListState.recentDirectRooms.isNotEmpty()) {
            item {
                ListSectionHeader(
                    title = stringResource(id = CommonStrings.common_suggestions),
                    hasDivider = false,
                )
            }
            state.userListState.recentDirectRooms.forEach { recentDirectRoom ->
                item {
                    MatrixUserRow(
                        modifier = Modifier.clickable(
                            onClick = {
                                onDmClick(recentDirectRoom.roomId)
                            }
                        ),
                        matrixUser = recentDirectRoom.matrixUser,
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateRoomActionButton(
    @DrawableRes iconRes: Int,
    text: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            tint = ZenobiaTheme.colors.iconSecondary,
            resourceId = iconRes,
            contentDescription = null,
        )
        Text(
            text = text,
            style = ZenobiaTheme.typography.fontBodyLgRegular,
        )
    }
}

@PreviewsDayNight
@Composable
internal fun StartChatViewPreview(@PreviewParameter(StartChatStateProvider::class) state: StartChatState) =
    ZenobiaPreview {
        StartChatView(
            state = state,
            onCloseClick = {},
            onNewRoomClick = {},
            onOpenDM = {},
            onJoinByAddressClick = {},
            onInviteFriendsClick = {},
            onRoomDirectorySearchClick = {},
        )
    }
