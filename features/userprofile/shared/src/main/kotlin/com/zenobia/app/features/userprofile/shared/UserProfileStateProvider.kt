/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.userprofile.shared

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.zenobia.app.features.startchat.api.ConfirmingStartDmWithMatrixUser
import com.zenobia.app.features.userprofile.api.UserProfileEvents
import com.zenobia.app.features.userprofile.api.UserProfileState
import com.zenobia.app.features.userprofile.api.UserProfileVerificationState
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.designsystem.utils.snackbar.SnackbarMessage
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.ui.components.aMatrixUser

open class UserProfileStateProvider : PreviewParameterProvider<UserProfileState> {
    override val values: Sequence<UserProfileState>
        get() = sequenceOf(
            aUserProfileState(),
            aUserProfileState(userName = null),
            aUserProfileState(isBlocked = AsyncData.Success(true), verificationState = UserProfileVerificationState.VERIFIED),
            aUserProfileState(displayConfirmationDialog = UserProfileState.ConfirmationDialog.Block),
            aUserProfileState(displayConfirmationDialog = UserProfileState.ConfirmationDialog.Unblock),
            aUserProfileState(isBlocked = AsyncData.Loading(true), verificationState = UserProfileVerificationState.UNKNOWN),
            aUserProfileState(startDmActionState = AsyncAction.Loading),
            aUserProfileState(canCall = true),
            aUserProfileState(startDmActionState = ConfirmingStartDmWithMatrixUser(aMatrixUser(), isUserIdentityUnknown = false)),
            aUserProfileState(verificationState = UserProfileVerificationState.VERIFICATION_VIOLATION),
        )
}

fun aUserProfileState(
    userId: UserId = UserId("@daniel:domain.com"),
    userName: String? = "Daniel",
    avatarUrl: String? = null,
    isBlocked: AsyncData<Boolean> = AsyncData.Success(false),
    verificationState: UserProfileVerificationState = UserProfileVerificationState.UNVERIFIED,
    startDmActionState: AsyncAction<RoomId> = AsyncAction.Uninitialized,
    displayConfirmationDialog: UserProfileState.ConfirmationDialog? = null,
    isCurrentUser: Boolean = false,
    dmRoomId: RoomId? = null,
    canCall: Boolean = false,
    snackbarMessage: SnackbarMessage? = null,
    eventSink: (UserProfileEvents) -> Unit = {},
) = UserProfileState(
    userId = userId,
    userName = userName,
    avatarUrl = avatarUrl,
    isBlocked = isBlocked,
    verificationState = verificationState,
    startDmActionState = startDmActionState,
    displayConfirmationDialog = displayConfirmationDialog,
    isCurrentUser = isCurrentUser,
    dmRoomId = dmRoomId,
    canCall = canCall,
    snackbarMessage = snackbarMessage,
    eventSink = eventSink,
)
