/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.logout.impl

import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.matrix.api.encryption.BackupState
import com.zenobia.app.libraries.matrix.api.encryption.BackupUploadState
import com.zenobia.app.libraries.matrix.api.encryption.RecoveryState

data class LogoutState(
    val isLastDevice: Boolean,
    val backupState: BackupState,
    val doesBackupExistOnServer: Boolean,
    val recoveryState: RecoveryState,
    val backupUploadState: BackupUploadState,
    val waitingForALongTime: Boolean,
    val logoutAction: AsyncAction<Unit>,
    val eventSink: (LogoutEvents) -> Unit,
)
