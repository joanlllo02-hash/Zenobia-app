/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.invite.impl.acceptdecline

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.zenobia.app.features.invite.api.InviteData
import com.zenobia.app.features.invite.api.acceptdecline.AcceptDeclineInviteEvents
import com.zenobia.app.features.invite.api.acceptdecline.AcceptDeclineInviteState
import com.zenobia.app.features.invite.api.acceptdecline.ConfirmingDeclineInvite
import com.zenobia.app.features.invite.impl.AcceptInvite
import com.zenobia.app.features.invite.impl.R
import com.zenobia.app.libraries.designsystem.components.async.AsyncActionView
import com.zenobia.app.libraries.designsystem.components.dialogs.ConfirmationDialog
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun AcceptDeclineInviteView(
    state: AcceptDeclineInviteState,
    onAcceptInviteSuccess: (RoomId) -> Unit,
    onDeclineInviteSuccess: (RoomId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        AsyncActionView(
            async = state.acceptAction,
            onSuccess = { roomId ->
                state.eventSink(InternalAcceptDeclineInviteEvents.ClearAcceptActionState)
                onAcceptInviteSuccess(roomId)
            },
            onErrorDismiss = {
                state.eventSink(InternalAcceptDeclineInviteEvents.ClearAcceptActionState)
            },
            errorTitle = {
                stringResource(CommonStrings.common_something_went_wrong)
            },
            errorMessage = { error ->
                if (error is AcceptInvite.Failures.InvalidInvite) {
                    stringResource(CommonStrings.error_invalid_invite)
                } else {
                    stringResource(CommonStrings.error_network_or_server_issue)
                }
            }
        )
        AsyncActionView(
            async = state.declineAction,
            onSuccess = { roomId ->
                state.eventSink(InternalAcceptDeclineInviteEvents.ClearDeclineActionState)
                onDeclineInviteSuccess(roomId)
            },
            onErrorDismiss = {
                state.eventSink(InternalAcceptDeclineInviteEvents.ClearDeclineActionState)
            },
            errorTitle = {
                stringResource(CommonStrings.common_something_went_wrong)
            },
            errorMessage = {
                stringResource(CommonStrings.error_network_or_server_issue)
            },
            confirmationDialog = { confirming ->
                // Note: confirming will always be of type ConfirmingDeclineInvite.
                if (confirming is ConfirmingDeclineInvite) {
                    DeclineConfirmationDialog(
                        invite = confirming.inviteData,
                        blockUser = confirming.blockUser,
                        onConfirmClick = {
                            state.eventSink(
                                AcceptDeclineInviteEvents.DeclineInvite(
                                    confirming.inviteData,
                                    blockUser = confirming.blockUser,
                                    shouldConfirm = false
                                )
                            )
                        },
                        onDismissClick = {
                            state.eventSink(InternalAcceptDeclineInviteEvents.ClearDeclineActionState)
                        }
                    )
                }
            }
        )
    }
}

@Composable
private fun DeclineConfirmationDialog(
    invite: InviteData,
    blockUser: Boolean,
    onConfirmClick: () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ConfirmationDialog(
        modifier = modifier,
        content = stringResource(R.string.screen_invites_decline_chat_message, invite.roomName),
        title = if (blockUser) {
            stringResource(R.string.screen_join_room_decline_and_block_alert_title)
        } else {
            stringResource(R.string.screen_invites_decline_chat_title)
        },
        submitText = if (blockUser) {
            stringResource(R.string.screen_join_room_decline_and_block_alert_confirmation)
        } else {
            stringResource(CommonStrings.action_decline)
        },
        cancelText = stringResource(CommonStrings.action_cancel),
        onSubmitClick = onConfirmClick,
        onDismiss = onDismissClick,
    )
}

@PreviewsDayNight
@Composable
internal fun AcceptDeclineInviteViewPreview(@PreviewParameter(AcceptDeclineInviteStateProvider::class) state: AcceptDeclineInviteState) =
    ZenobiaPreview {
        AcceptDeclineInviteView(
            state = state,
            onAcceptInviteSuccess = {},
            onDeclineInviteSuccess = {},
        )
    }
