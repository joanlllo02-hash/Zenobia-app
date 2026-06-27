/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.userprofile.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.features.startchat.api.ConfirmingStartDmWithMatrixUser
import com.zenobia.app.features.userprofile.api.UserProfileEvents
import com.zenobia.app.features.userprofile.api.UserProfileState
import com.zenobia.app.features.userprofile.api.UserProfileVerificationState
import com.zenobia.app.features.userprofile.shared.blockuser.BlockUserDialogs
import com.zenobia.app.features.userprofile.shared.blockuser.BlockUserSection
import com.zenobia.app.libraries.designsystem.components.async.AsyncActionView
import com.zenobia.app.libraries.designsystem.components.async.AsyncActionViewDefaults
import com.zenobia.app.libraries.designsystem.components.button.BackButton
import com.zenobia.app.libraries.designsystem.components.list.ListItemContent
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.IconSource
import com.zenobia.app.libraries.designsystem.theme.components.ListItem
import com.zenobia.app.libraries.designsystem.theme.components.Scaffold
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.designsystem.theme.components.TopAppBar
import com.zenobia.app.libraries.designsystem.utils.snackbar.SnackbarHost
import com.zenobia.app.libraries.designsystem.utils.snackbar.rememberSnackbarHostState
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.api.notification.CallIntent
import com.zenobia.app.libraries.matrix.ui.components.CreateDmConfirmationBottomSheet
import com.zenobia.app.libraries.ui.strings.CommonStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileView(
    state: UserProfileState,
    onShareUser: () -> Unit,
    onOpenDm: (RoomId) -> Unit,
    onStartCall: (RoomId, CallIntent) -> Unit,
    goBack: () -> Unit,
    openAvatarPreview: (username: String, url: String) -> Unit,
    onVerifyClick: (UserId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = rememberSnackbarHostState(snackbarMessage = state.snackbarMessage)
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { }, navigationIcon = { BackButton(onClick = goBack) })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .verticalScroll(rememberScrollState())
        ) {
            UserProfileHeaderSection(
                avatarUrl = state.avatarUrl,
                userId = state.userId,
                userName = state.userName,
                verificationState = state.verificationState,
                openAvatarPreview = { avatarUrl ->
                    openAvatarPreview(state.userName ?: state.userId.value, avatarUrl)
                },
                onUserIdClick = {
                    state.eventSink(UserProfileEvents.CopyToClipboard(state.userId.value))
                },
                withdrawVerificationClick = { state.eventSink(UserProfileEvents.WithdrawVerification) },
            )
            UserProfileMainActionsSection(
                isCurrentUser = state.isCurrentUser,
                canCall = state.canCall,
                onShareUser = onShareUser,
                onStartDM = { state.eventSink(UserProfileEvents.StartDM) },
                onCall = { intent -> state.dmRoomId?.let { onStartCall(it, intent) } }
            )
            Spacer(modifier = Modifier.height(26.dp))
            if (!state.isCurrentUser) {
                VerifyUserSection(state, onVerifyClick = { onVerifyClick(state.userId) })
                BlockUserSection(state)
                BlockUserDialogs(state)
            }
            AsyncActionView(
                async = state.startDmActionState,
                progressDialog = {
                    AsyncActionViewDefaults.ProgressDialog(
                        progressText = stringResource(CommonStrings.common_starting_chat),
                    )
                },
                onSuccess = onOpenDm,
                errorMessage = { stringResource(R.string.screen_start_chat_error_starting_chat) },
                onRetry = { state.eventSink(UserProfileEvents.StartDM) },
                onErrorDismiss = { state.eventSink(UserProfileEvents.ClearStartDMState) },
                confirmationDialog = { data ->
                    if (data is ConfirmingStartDmWithMatrixUser) {
                        CreateDmConfirmationBottomSheet(
                            matrixUser = data.matrixUser,
                            isUserIdentityUnknown = data.isUserIdentityUnknown,
                            onSendInvite = {
                                state.eventSink(UserProfileEvents.StartDM)
                            },
                            onDismiss = {
                                state.eventSink(UserProfileEvents.ClearStartDMState)
                            },
                        )
                    }
                },
            )
        }
    }
}

@Composable
private fun VerifyUserSection(
    state: UserProfileState,
    onVerifyClick: () -> Unit,
) {
    if (state.verificationState == UserProfileVerificationState.UNVERIFIED) {
        ListItem(
            headlineContent = { Text(stringResource(CommonStrings.common_verify_user)) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Lock())),
            onClick = onVerifyClick,
        )
    }
}

@PreviewsDayNight
@Composable
internal fun UserProfileViewPreview(
    @PreviewParameter(UserProfileStateProvider::class) state: UserProfileState
) = ZenobiaPreview {
    UserProfileView(
        state = state,
        onShareUser = {},
        goBack = {},
        onOpenDm = {},
        onStartCall = { _, _ -> },
        openAvatarPreview = { _, _ -> },
        onVerifyClick = {},
    )
}
