/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.preferences.impl.blockedusers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.zenobia.app.features.preferences.impl.R
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.designsystem.components.async.AsyncIndicator
import com.zenobia.app.libraries.designsystem.components.async.AsyncIndicatorHost
import com.zenobia.app.libraries.designsystem.components.async.rememberAsyncIndicatorState
import com.zenobia.app.libraries.designsystem.components.button.BackButton
import com.zenobia.app.libraries.designsystem.components.dialogs.ConfirmationDialog
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.Scaffold
import com.zenobia.app.libraries.designsystem.theme.components.TopAppBar
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.api.user.MatrixUser
import com.zenobia.app.libraries.matrix.ui.components.MatrixUserRow
import com.zenobia.app.libraries.ui.strings.CommonStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockedUsersView(
    state: BlockedUsersState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Scaffold(
            topBar = {
                TopAppBar(
                    titleStr = stringResource(CommonStrings.common_blocked_users),
                    navigationIcon = {
                        BackButton(onClick = onBackClick)
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding)
            ) {
                items(state.blockedUsers) { matrixUser ->
                    BlockedUserItem(
                        matrixUser = matrixUser,
                        onClick = { state.eventSink(BlockedUsersEvents.Unblock(it)) }
                    )
                }
            }
        }

        val asyncIndicatorState = rememberAsyncIndicatorState()
        AsyncIndicatorHost(modifier = Modifier.statusBarsPadding(), state = asyncIndicatorState)

        when (state.unblockUserAction) {
            is AsyncAction.Loading -> {
                LaunchedEffect(state.unblockUserAction) {
                    asyncIndicatorState.enqueue {
                        AsyncIndicator.Loading(text = stringResource(R.string.screen_blocked_users_unblocking))
                    }
                }
            }
            is AsyncAction.Failure -> {
                LaunchedEffect(state.unblockUserAction) {
                    asyncIndicatorState.enqueue(durationMs = AsyncIndicator.DURATION_SHORT) {
                        AsyncIndicator.Failure(text = stringResource(CommonStrings.common_failed))
                    }
                }
            }
            is AsyncAction.Success -> {
                LaunchedEffect(state.unblockUserAction) {
                    asyncIndicatorState.clear()
                }
            }
            is AsyncAction.Confirming -> {
                ConfirmationDialog(
                    title = stringResource(R.string.screen_blocked_users_unblock_alert_title),
                    content = stringResource(R.string.screen_blocked_users_unblock_alert_description),
                    submitText = stringResource(R.string.screen_blocked_users_unblock_alert_action),
                    onSubmitClick = { state.eventSink(BlockedUsersEvents.ConfirmUnblock) },
                    onDismiss = { state.eventSink(BlockedUsersEvents.Cancel) }
                )
            }
            else -> Unit
        }
    }
}

@Composable
private fun BlockedUserItem(
    matrixUser: MatrixUser,
    onClick: (UserId) -> Unit,
) {
    MatrixUserRow(
        modifier = Modifier.clickable { onClick(matrixUser.userId) },
        matrixUser = matrixUser,
    )
}

@PreviewsDayNight
@Composable
internal fun BlockedUsersViewPreview(@PreviewParameter(BlockedUsersStateProvider::class) state: BlockedUsersState) {
    ZenobiaPreview {
        BlockedUsersView(
            state = state,
            onBackClick = {}
        )
    }
}
