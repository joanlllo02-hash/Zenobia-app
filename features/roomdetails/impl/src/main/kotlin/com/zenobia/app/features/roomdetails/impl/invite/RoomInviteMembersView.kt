/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.roomdetails.impl.invite

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.features.invitepeople.api.InvitePeopleEvents
import com.zenobia.app.features.invitepeople.api.InvitePeopleState
import com.zenobia.app.features.invitepeople.api.InvitePeopleStateProvider
import com.zenobia.app.features.roomdetails.impl.R
import com.zenobia.app.libraries.designsystem.components.ProgressDialog
import com.zenobia.app.libraries.designsystem.components.button.BackButton
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.Scaffold
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.designsystem.theme.components.TextButton
import com.zenobia.app.libraries.designsystem.theme.components.TopAppBar
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun RoomInviteMembersView(
    state: InvitePeopleState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    invitePeopleView: @Composable () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            RoomInviteMembersTopBar(
                onBackClick = {
                    if (state.isSearchActive) {
                        state.eventSink(InvitePeopleEvents.CloseSearch)
                    } else {
                        onBackClick()
                    }
                },
                onSubmitClick = {
                    state.eventSink(InvitePeopleEvents.SendInvites)
                },
                canSend = state.canInvite,
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .consumeWindowInsets(padding),
        ) {
            invitePeopleView()
        }
    }

    if (state.sendInvitesAction.isLoading()) {
        InviteProgressDialog()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoomInviteMembersTopBar(
    canSend: Boolean,
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit,
) {
    TopAppBar(
        titleStr = stringResource(R.string.screen_room_details_invite_people_title),
        navigationIcon = { BackButton(onClick = onBackClick) },
        actions = {
            TextButton(
                text = stringResource(CommonStrings.action_invite),
                onClick = onSubmitClick,
                enabled = canSend,
            )
        }
    )
}

@Composable
private fun InviteProgressDialog() {
    ProgressDialog {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.screen_room_details_invite_people_preparing),
            color = ZenobiaTheme.colors.textPrimary,
            style = ZenobiaTheme.typography.fontHeadingSmMedium,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.screen_room_details_invite_people_dont_close),
            color = ZenobiaTheme.colors.textSecondary,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@PreviewsDayNight
@Composable
internal fun RoomInviteMembersViewPreview(@PreviewParameter(InvitePeopleStateProvider::class) state: InvitePeopleState) = ZenobiaPreview {
    RoomInviteMembersView(
        state = state,
        invitePeopleView = {},
        onBackClick = {},
    )
}
