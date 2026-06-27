/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.roommembermoderation.impl

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.features.roommembermoderation.api.ModerationAction
import com.zenobia.app.features.roommembermoderation.api.ModerationActionState
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.designsystem.components.async.AsyncIndicator
import com.zenobia.app.libraries.designsystem.components.async.AsyncIndicatorHost
import com.zenobia.app.libraries.designsystem.components.async.rememberAsyncIndicatorState
import com.zenobia.app.libraries.designsystem.components.avatar.Avatar
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarSize
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarType
import com.zenobia.app.libraries.designsystem.components.dialogs.TextFieldDialog
import com.zenobia.app.libraries.designsystem.components.list.ListItemContent
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.IconSource
import com.zenobia.app.libraries.designsystem.theme.components.ListItem
import com.zenobia.app.libraries.designsystem.theme.components.ListItemStyle
import com.zenobia.app.libraries.designsystem.theme.components.ModalBottomSheet
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.matrix.api.user.MatrixUser
import com.zenobia.app.libraries.matrix.ui.model.getAvatarData
import com.zenobia.app.libraries.matrix.ui.model.getBestName
import com.zenobia.app.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun RoomMemberModerationView(
    state: InternalRoomMemberModerationState,
    onSelectAction: (ModerationAction, MatrixUser) -> Unit,
    onAvatarClick: ((MatrixUser) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        val selectedUser = state.selectedUser
        if (selectedUser != null && state.canDisplayActions) {
            RoomMemberActionsBottomSheet(
                user = selectedUser,
                actions = state.actions,
                onSelectAction = onSelectAction,
                onAvatarClick = onAvatarClick,
                onDismiss = { state.eventSink(InternalRoomMemberModerationEvents.Reset) },
            )
        }
        RoomMemberAsyncActions(state = state)
    }
}

@Composable
private fun RoomMemberAsyncActions(
    state: InternalRoomMemberModerationState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        val selectedUser = state.selectedUser
        val asyncIndicatorState = rememberAsyncIndicatorState()
        AsyncIndicatorHost(modifier = Modifier.statusBarsPadding(), state = asyncIndicatorState)

        when (val action = state.kickUserAsyncAction) {
            is AsyncAction.Confirming -> {
                TextFieldDialog(
                    title = stringResource(R.string.screen_bottom_sheet_manage_room_member_kick_member_confirmation_title),
                    submitText = stringResource(R.string.screen_bottom_sheet_manage_room_member_kick_member_confirmation_action),
                    destructiveSubmit = true,
                    minLines = 2,
                    onSubmit = { reason ->
                        state.eventSink(InternalRoomMemberModerationEvents.DoKickUser(reason = reason))
                    },
                    onDismissRequest = { state.eventSink(InternalRoomMemberModerationEvents.Reset) },
                    placeholder = stringResource(id = CommonStrings.common_reason),
                    content = stringResource(R.string.screen_bottom_sheet_manage_room_member_kick_member_confirmation_description),
                    value = "",
                )
            }
            is AsyncAction.Loading -> {
                LaunchedEffect(action) {
                    val userDisplayName = selectedUser?.getBestName().orEmpty()
                    asyncIndicatorState.enqueue {
                        AsyncIndicator.Loading(text = stringResource(R.string.screen_bottom_sheet_manage_room_member_removing_user, userDisplayName))
                    }
                }
            }
            is AsyncAction.Failure -> {
                Timber.e(action.error, "Failed to kick user.")
                LaunchedEffect(action) {
                    asyncIndicatorState.enqueue(AsyncIndicator.DURATION_SHORT) {
                        AsyncIndicator.Failure(
                            text = stringResource(CommonStrings.common_failed),
                        )
                    }
                }
            }
            is AsyncAction.Success -> {
                LaunchedEffect(action) { asyncIndicatorState.clear() }
            }
            else -> Unit
        }

        when (val action = state.banUserAsyncAction) {
            is AsyncAction.Confirming -> {
                TextFieldDialog(
                    title = stringResource(R.string.screen_bottom_sheet_manage_room_member_ban_member_confirmation_title),
                    submitText = stringResource(R.string.screen_bottom_sheet_manage_room_member_ban_member_confirmation_action),
                    destructiveSubmit = true,
                    minLines = 2,
                    onSubmit = { reason ->
                        state.eventSink(InternalRoomMemberModerationEvents.DoBanUser(reason = reason))
                    },
                    onDismissRequest = { state.eventSink(InternalRoomMemberModerationEvents.Reset) },
                    placeholder = stringResource(id = CommonStrings.common_reason),
                    content = stringResource(R.string.screen_bottom_sheet_manage_room_member_ban_member_confirmation_description),
                    value = "",
                )
            }
            is AsyncAction.Loading -> {
                LaunchedEffect(action) {
                    val userDisplayName = selectedUser?.getBestName().orEmpty()
                    asyncIndicatorState.enqueue {
                        AsyncIndicator.Loading(text = stringResource(R.string.screen_bottom_sheet_manage_room_member_banning_user, userDisplayName))
                    }
                }
            }
            is AsyncAction.Failure -> {
                Timber.e(action.error, "Failed to ban user.")
                LaunchedEffect(action) {
                    asyncIndicatorState.enqueue(AsyncIndicator.DURATION_SHORT) {
                        AsyncIndicator.Failure(
                            text = stringResource(CommonStrings.common_failed),
                        )
                    }
                }
            }
            is AsyncAction.Success -> {
                LaunchedEffect(action) { asyncIndicatorState.clear() }
            }
            else -> Unit
        }
        when (val action = state.unbanUserAsyncAction) {
            is AsyncAction.Confirming -> {
                TextFieldDialog(
                    title = stringResource(R.string.screen_bottom_sheet_manage_room_member_unban_member_confirmation_title),
                    submitText = stringResource(R.string.screen_bottom_sheet_manage_room_member_unban_member_confirmation_action),
                    destructiveSubmit = true,
                    minLines = 2,
                    onSubmit = { reason ->
                        val userDisplayName = selectedUser?.getBestName().orEmpty()
                        asyncIndicatorState.enqueue {
                            AsyncIndicator.Loading(text = stringResource(R.string.screen_bottom_sheet_manage_room_member_unbanning_user, userDisplayName))
                        }
                        state.eventSink(InternalRoomMemberModerationEvents.DoUnbanUser(reason = reason))
                    },
                    onDismissRequest = { state.eventSink(InternalRoomMemberModerationEvents.Reset) },
                    placeholder = stringResource(id = CommonStrings.common_reason),
                    content = stringResource(R.string.screen_bottom_sheet_manage_room_member_unban_member_confirmation_description),
                    value = "",
                )
            }
            is AsyncAction.Failure -> {
                Timber.e(action.error, "Failed to unban user.")
                LaunchedEffect(action) {
                    asyncIndicatorState.enqueue(AsyncIndicator.DURATION_SHORT) {
                        AsyncIndicator.Failure(
                            text = stringResource(CommonStrings.common_failed),
                        )
                    }
                }
            }
            is AsyncAction.Success -> {
                LaunchedEffect(action) { asyncIndicatorState.clear() }
            }
            is AsyncAction.Loading,
            AsyncAction.Uninitialized -> Unit
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoomMemberActionsBottomSheet(
    user: MatrixUser,
    actions: ImmutableList<ModerationActionState>,
    onSelectAction: (ModerationAction, MatrixUser) -> Unit,
    onAvatarClick: ((MatrixUser) -> Unit)? = null,
    onDismiss: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        modifier = Modifier.systemBarsPadding(),
        sheetState = bottomSheetState,
        onDismissRequest = {
            coroutineScope.launch {
                bottomSheetState.hide()
                onDismiss()
            }
        },
        scrollable = false,
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Avatar(
                avatarData = user.getAvatarData(size = AvatarSize.RoomListManageUser),
                avatarType = AvatarType.User,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable(enabled = user.avatarUrl != null && onAvatarClick != null) {
                        coroutineScope.launch {
                            bottomSheetState.hide()
                            onAvatarClick?.invoke(user)
                            onDismiss()
                        }
                    }
            )
            val bestName = user.getBestName()
            Text(
                text = bestName,
                style = ZenobiaTheme.typography.fontHeadingLgBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                    .fillMaxWidth()
            )
            // Show user ID only if it's different from the display name
            if (bestName != user.userId.value) {
                Text(
                    text = user.userId.value,
                    style = ZenobiaTheme.typography.fontBodyMdRegular,
                    color = ZenobiaTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(32.dp))

            for (actionState in actions) {
                when (val action = actionState.action) {
                    is ModerationAction.DisplayProfile -> {
                        ListItem(
                            headlineContent = { Text(stringResource(R.string.screen_bottom_sheet_manage_room_member_member_user_info)) },
                            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.UserProfile())),
                            onClick = {
                                coroutineScope.launch {
                                    bottomSheetState.hide()
                                    onSelectAction(action, user)
                                }
                            },
                            enabled = actionState.isEnabled
                        )
                    }
                    is ModerationAction.KickUser -> {
                        ListItem(
                            headlineContent = { Text(stringResource(R.string.screen_bottom_sheet_manage_room_member_remove)) },
                            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Close())),
                            style = ListItemStyle.Destructive,
                            onClick = {
                                coroutineScope.launch {
                                    bottomSheetState.hide()
                                    onSelectAction(action, user)
                                }
                            },
                            enabled = actionState.isEnabled
                        )
                    }
                    is ModerationAction.BanUser -> {
                        ListItem(
                            headlineContent = { Text(stringResource(R.string.screen_bottom_sheet_manage_room_member_ban)) },
                            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Block())),
                            style = ListItemStyle.Destructive,
                            onClick = {
                                coroutineScope.launch {
                                    bottomSheetState.hide()
                                    onSelectAction(action, user)
                                }
                            },
                            enabled = actionState.isEnabled
                        )
                    }
                    is ModerationAction.UnbanUser -> {
                        ListItem(
                            headlineContent = { Text(stringResource(R.string.screen_bottom_sheet_manage_room_member_unban)) },
                            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Restart())),
                            style = ListItemStyle.Destructive,
                            onClick = {
                                coroutineScope.launch {
                                    bottomSheetState.hide()
                                    onSelectAction(action, user)
                                }
                            },
                            enabled = actionState.isEnabled
                        )
                    }
                }
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun RoomMemberModerationViewPreview(@PreviewParameter(InternalRoomMemberModerationStateProvider::class) state: InternalRoomMemberModerationState) {
    val isDoingAction = listOf(state.kickUserAsyncAction, state.banUserAsyncAction, state.unbanUserAsyncAction).any { it is AsyncAction.Loading }
    val modifier = if (isDoingAction) {
        Modifier.fillMaxWidth().heightIn(min = 64.dp)
    } else {
        Modifier.fillMaxSize()
    }
    ZenobiaPreview {
        Box(modifier) {
            RoomMemberModerationView(
                state = state,
                onSelectAction = { _, _ -> },
                onAvatarClick = {},
            )
        }
    }
}
