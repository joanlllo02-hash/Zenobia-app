/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.userprofile.shared.blockuser

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.zenobia.app.features.userprofile.api.UserProfileEvents
import com.zenobia.app.features.userprofile.api.UserProfileState
import com.zenobia.app.features.userprofile.shared.R
import com.zenobia.app.libraries.designsystem.components.dialogs.ConfirmationDialog

@Composable
fun BlockUserDialogs(state: UserProfileState) {
    when (state.displayConfirmationDialog) {
        null -> Unit
        UserProfileState.ConfirmationDialog.Block -> {
            BlockConfirmationDialog(
                onBlockAction = {
                    state.eventSink(
                        UserProfileEvents.BlockUser(
                            needsConfirmation = false
                        )
                    )
                },
                onDismiss = { state.eventSink(UserProfileEvents.ClearConfirmationDialog) }
            )
        }
        UserProfileState.ConfirmationDialog.Unblock -> {
            UnblockConfirmationDialog(
                onUnblockAction = {
                    state.eventSink(
                        UserProfileEvents.UnblockUser(
                            needsConfirmation = false
                        )
                    )
                },
                onDismiss = { state.eventSink(UserProfileEvents.ClearConfirmationDialog) }
            )
        }
    }
}

@Composable
private fun BlockConfirmationDialog(
    onBlockAction: () -> Unit,
    onDismiss: () -> Unit,
) {
    ConfirmationDialog(
        title = stringResource(R.string.screen_dm_details_block_user),
        content = stringResource(R.string.screen_dm_details_block_alert_description),
        submitText = stringResource(R.string.screen_dm_details_block_alert_action),
        onSubmitClick = onBlockAction,
        onDismiss = onDismiss
    )
}

@Composable
private fun UnblockConfirmationDialog(
    onUnblockAction: () -> Unit,
    onDismiss: () -> Unit,
) {
    ConfirmationDialog(
        title = stringResource(R.string.screen_dm_details_unblock_user),
        content = stringResource(R.string.screen_dm_details_unblock_alert_description),
        submitText = stringResource(R.string.screen_dm_details_unblock_alert_action),
        onSubmitClick = onUnblockAction,
        onDismiss = onDismiss
    )
}
