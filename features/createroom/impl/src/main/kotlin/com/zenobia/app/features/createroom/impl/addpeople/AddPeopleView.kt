/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.createroom.impl.addpeople

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.zenobia.app.features.createroom.impl.R
import com.zenobia.app.features.invitepeople.api.InvitePeopleEvents
import com.zenobia.app.features.invitepeople.api.InvitePeopleState
import com.zenobia.app.features.invitepeople.api.InvitePeopleStateProvider
import com.zenobia.app.libraries.designsystem.atomic.pages.HeaderFooterPage
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.Button
import com.zenobia.app.libraries.designsystem.theme.components.TextButton
import com.zenobia.app.libraries.designsystem.theme.components.TopAppBar
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun AddPeopleView(
    state: InvitePeopleState,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    invitePeopleView: @Composable () -> Unit,
) {
    val currentOnFinish by rememberUpdatedState(onFinish)
    LaunchedEffect(state.sendInvitesAction, state.createRoomFromDmAction) {
        if (state.sendInvitesAction.isSuccess() || state.createRoomFromDmAction.isSuccess()) {
            currentOnFinish()
        }
    }

    HeaderFooterPage(
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
        topBar = {
            AddPeopleTopBar(onSkipClick = onFinish)
        },
        footer = {
            Button(
                text = stringResource(CommonStrings.action_finish),
                onClick = {
                    state.eventSink(InvitePeopleEvents.SendInvites)
                },
                enabled = state.canInvite,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        },
        content = invitePeopleView
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddPeopleTopBar(
    onSkipClick: () -> Unit,
) {
    TopAppBar(
        titleStr = stringResource(R.string.screen_create_room_add_people_title),
        actions = {
            TextButton(
                text = stringResource(CommonStrings.action_skip),
                onClick = onSkipClick,
            )
        }
    )
}

@PreviewsDayNight
@Composable
internal fun AddPeopleViewPreview(@PreviewParameter(InvitePeopleStateProvider::class) state: InvitePeopleState) = ZenobiaPreview {
    AddPeopleView(
        state = state,
        invitePeopleView = {},
        onFinish = {},
    )
}
