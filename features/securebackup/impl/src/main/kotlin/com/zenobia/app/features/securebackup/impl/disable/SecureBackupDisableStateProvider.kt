/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.securebackup.impl.disable

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.matrix.api.encryption.BackupState

open class SecureBackupDisableStateProvider : PreviewParameterProvider<SecureBackupDisableState> {
    override val values: Sequence<SecureBackupDisableState>
        get() = sequenceOf(
            aSecureBackupDisableState(),
            aSecureBackupDisableState(disableAction = AsyncAction.ConfirmingNoParams),
            aSecureBackupDisableState(disableAction = AsyncAction.Loading),
            aSecureBackupDisableState(disableAction = AsyncAction.Failure(Exception("Failed to disable"))),
            // Add other states here
        )
}

fun aSecureBackupDisableState(
    backupState: BackupState = BackupState.UNKNOWN,
    disableAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
) = SecureBackupDisableState(
    backupState = backupState,
    disableAction = disableAction,
    appName = "Zenobia",
    eventSink = {}
)
