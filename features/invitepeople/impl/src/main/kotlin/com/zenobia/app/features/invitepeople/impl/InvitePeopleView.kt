/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.invitepeople.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.features.invitepeople.api.InvitePeopleEvents
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.designsystem.atomic.molecules.ButtonRowMolecule
import com.zenobia.app.libraries.designsystem.atomic.molecules.IconTitleSubtitleMolecule
import com.zenobia.app.libraries.designsystem.components.BigIcon
import com.zenobia.app.libraries.designsystem.components.async.AsyncFailure
import com.zenobia.app.libraries.designsystem.components.async.AsyncLoading
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarSize
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.Button
import com.zenobia.app.libraries.designsystem.theme.components.HorizontalDivider
import com.zenobia.app.libraries.designsystem.theme.components.IconSource
import com.zenobia.app.libraries.designsystem.theme.components.ListSectionHeader
import com.zenobia.app.libraries.designsystem.theme.components.ModalBottomSheet
import com.zenobia.app.libraries.designsystem.theme.components.OutlinedButton
import com.zenobia.app.libraries.designsystem.theme.components.SearchBar
import com.zenobia.app.libraries.designsystem.theme.components.SearchBarResultState
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.matrix.api.user.MatrixUser
import com.zenobia.app.libraries.matrix.ui.components.CheckableUserRow
import com.zenobia.app.libraries.matrix.ui.components.CheckableUserRowData
import com.zenobia.app.libraries.matrix.ui.components.MatrixUserRow
import com.zenobia.app.libraries.matrix.ui.components.SelectedUsersRowList
import com.zenobia.app.libraries.matrix.ui.model.getAvatarData
import com.zenobia.app.libraries.matrix.ui.model.getBestName
import com.zenobia.app.libraries.testtags.TestTags
import com.zenobia.app.libraries.testtags.testTag
import com.zenobia.app.libraries.ui.strings.CommonStrings
import com.zenobia.app.libraries.ui.utils.strings.simplePluralStringResource
import kotlinx.collections.immutable.ImmutableList

@Composable
fun InvitePeopleView(
    state: DefaultInvitePeopleState,
    modifier: Modifier = Modifier,
) {
    when (state.room) {
        is AsyncData.Failure -> InvitePeopleViewError(state.room.error, modifier)
        AsyncData.Uninitialized,
        is AsyncData.Loading,
        is AsyncData.Success -> InvitePeopleContentView(state, modifier)
    }
}

@Composable
private fun InvitePeopleViewError(
    error: Throwable,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        AsyncFailure(
            throwable = error,
            onRetry = null,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
}

@Composable
private fun InvitePeopleContentView(
    state: DefaultInvitePeopleState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        fun toggleUser(user: MatrixUser) {
            state.eventSink(DefaultInvitePeopleEvents.ToggleUser(user))
        }

        InvitePeopleSearchBar(
            modifier = Modifier.imePadding().fillMaxWidth(),
            queryState = state.searchQuery,
            showLoader = state.showSearchLoader,
            selectedUsers = state.selectedUsers,
            state = state.searchResults,
            active = state.isSearchActive,
            onActiveChange = {
                state.eventSink(
                    DefaultInvitePeopleEvents.OnSearchActiveChanged(
                        it
                    )
                )
            },
            onToggleUser = ::toggleUser,
        )

        if (!state.isSearchActive) {
            if (state.selectedUsers.isNotEmpty()) {
                SelectedUsersRowList(
                    modifier = Modifier.fillMaxWidth(),
                    selectedUsers = state.selectedUsers,
                    autoScroll = true,
                    onUserRemove = ::toggleUser,
                    contentPadding = PaddingValues(all = 16.dp),
                )
            }
            if (state.suggestions.isNotEmpty()) {
                LazyColumn {
                    item {
                        ListSectionHeader(
                            title = stringResource(id = CommonStrings.common_suggestions),
                            hasDivider = false,
                        )
                    }
                    itemsIndexed(state.suggestions) { index, invitableUser ->
                        CheckableUserRow(
                            checked = invitableUser.isSelected,
                            onCheckedChange = {
                                state.eventSink(DefaultInvitePeopleEvents.ToggleUser(invitableUser.matrixUser))
                            },
                            data = CheckableUserRowData.Resolved(
                                avatarData = invitableUser.matrixUser.getAvatarData(AvatarSize.UserListItem),
                                name = invitableUser.matrixUser.getBestName(),
                                subtext = invitableUser.matrixUser.userId.value,
                            ),
                        )
                        if (index < state.suggestions.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }

        if (state.sendInvitesAction is ConfirmingUnknownUserInvitation) {
            InvitePeopleConfirmModal(
                users = state.sendInvitesAction.users,
                onDismiss = { state.eventSink.invoke(DefaultInvitePeopleEvents.DismissUnknownUsersModal) },
                onInvite = { state.eventSink.invoke(InvitePeopleEvents.SendInvites) },
                onRemove = { state.eventSink.invoke(DefaultInvitePeopleEvents.RemoveUnknownUsers) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InvitePeopleSearchBar(
    queryState: TextFieldState,
    state: SearchBarResultState<ImmutableList<InvitableUser>>,
    showLoader: Boolean,
    selectedUsers: ImmutableList<MatrixUser>,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    onToggleUser: (MatrixUser) -> Unit,
    modifier: Modifier = Modifier,
    placeHolderTitle: String = stringResource(CommonStrings.common_search_for_someone),
) {
    SearchBar(
        queryState = queryState,
        active = active,
        onActiveChange = onActiveChange,
        modifier = modifier,
        placeHolderTitle = placeHolderTitle,
        contentPrefix = {
            if (selectedUsers.isNotEmpty()) {
                SelectedUsersRowList(
                    modifier = Modifier.fillMaxWidth(),
                    selectedUsers = selectedUsers,
                    autoScroll = true,
                    onUserRemove = onToggleUser,
                    contentPadding = PaddingValues(all = 16.dp),
                )
            }
        },
        showBackButton = false,
        resultState = state,
        contentSuffix = {
            if (showLoader) {
                AsyncLoading()
            }
        },
        resultHandler = { results ->
            Text(
                text = stringResource(id = CommonStrings.common_search_results),
                style = ZenobiaTheme.typography.fontBodyLgMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 8.dp)
            )

            LazyColumn {
                itemsIndexed(results) { index, invitableUser ->
                    val invitedOrJoined = invitableUser.isAlreadyInvited || invitableUser.isAlreadyJoined
                    val isUnresolved = invitableUser.isUnresolved && !invitedOrJoined
                    val enabled = isUnresolved || !invitedOrJoined
                    val data = if (isUnresolved) {
                        CheckableUserRowData.Unresolved(
                            avatarData = invitableUser.matrixUser.getAvatarData(AvatarSize.UserListItem),
                            id = invitableUser.matrixUser.userId.value,
                        )
                    } else {
                        CheckableUserRowData.Resolved(
                            avatarData = invitableUser.matrixUser.getAvatarData(AvatarSize.UserListItem),
                            name = invitableUser.matrixUser.getBestName(),
                            subtext = when {
                                // If they're already invited or joined we show that information
                                invitableUser.isAlreadyJoined -> stringResource(R.string.screen_invite_users_already_a_member)
                                invitableUser.isAlreadyInvited -> stringResource(R.string.screen_invite_users_already_invited)
                                // Otherwise show the ID, unless that's already used for their name
                                invitableUser.matrixUser.displayName.isNullOrEmpty()
                                    .not() -> invitableUser.matrixUser.userId.value
                                else -> null
                            }
                        )
                    }
                    CheckableUserRow(
                        checked = invitableUser.isSelected || invitedOrJoined,
                        enabled = enabled,
                        data = data,
                        onCheckedChange = { onToggleUser(invitableUser.matrixUser) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InvitePeopleConfirmModal(
    users: ImmutableList<MatrixUser>,
    onDismiss: () -> Unit,
    onInvite: () -> Unit,
    onRemove: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = null,
        scrollable = false,
    ) {
        IconTitleSubtitleMolecule(
            title = simplePluralStringResource(
                resIdForOne = R.string.screen_invite_users_confirm_dialog_title_one_user,
                resIdForOthers = R.string.screen_invite_users_confirm_dialog_title_mutiple_users,
                count = users.size,
            ),
            subTitle = simplePluralStringResource(
                resIdForOne = R.string.screen_invite_users_confirm_dialog_subtitle_one_user,
                resIdForOthers = R.string.screen_invite_users_confirm_dialog_subtitle_multiple_users,
                count = users.size,
            ),
            iconStyle = BigIcon.Style.Default(CompoundIcons.UserAddSolid()),
            modifier = Modifier.padding(
                top = 32.dp,
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp,
            )
        )

        LazyColumn {
            items(users) { user ->
                MatrixUserRow(user)
            }
        }

        ButtonRowMolecule(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            OutlinedButton(
                text = stringResource(CommonStrings.action_remove),
                onClick = onRemove,
                leadingIcon = IconSource.Vector(CompoundIcons.Close()),
                modifier = Modifier.weight(1f).testTag(TestTags.confirmInviteUnknown),
            )
            Button(
                text = stringResource(CommonStrings.action_invite),
                onClick = onInvite,
                leadingIcon = IconSource.Vector(CompoundIcons.Check()),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun InvitePeopleViewPreview(@PreviewParameter(DefaultInvitePeopleStateProvider::class) state: DefaultInvitePeopleState) =
    ZenobiaPreview {
        InvitePeopleView(state = state)
    }
