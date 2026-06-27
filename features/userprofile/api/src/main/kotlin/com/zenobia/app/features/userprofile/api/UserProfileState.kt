/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.userprofile.api

import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.designsystem.utils.snackbar.SnackbarMessage
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.core.UserId

data class UserProfileState(
    val userId: UserId,
    val userName: String?,
    val avatarUrl: String?,
    val verificationState: UserProfileVerificationState,
    val isBlocked: AsyncData<Boolean>,
    val startDmActionState: AsyncAction<RoomId>,
    val displayConfirmationDialog: ConfirmationDialog?,
    val isCurrentUser: Boolean,
    val dmRoomId: RoomId?,
    val canCall: Boolean,
    val snackbarMessage: SnackbarMessage?,
    val eventSink: (UserProfileEvents) -> Unit
) {
    enum class ConfirmationDialog {
        Block,
        Unblock
    }
}

enum class UserProfileVerificationState {
    UNKNOWN,
    VERIFIED,
    UNVERIFIED,
    VERIFICATION_VIOLATION,
}
