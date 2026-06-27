/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.preferences.impl.root

import com.zenobia.app.features.logout.api.direct.DirectLogoutState
import com.zenobia.app.libraries.designsystem.utils.snackbar.SnackbarMessage
import com.zenobia.app.libraries.matrix.api.core.DeviceId
import com.zenobia.app.libraries.matrix.api.user.MatrixUser
import kotlinx.collections.immutable.ImmutableList

data class PreferencesRootState(
    val myUser: MatrixUser,
    val version: String,
    val deviceId: DeviceId?,
    val isMultiAccountEnabled: Boolean,
    val otherSessions: ImmutableList<MatrixUser>,
    val showSecureBackup: Boolean,
    val showSecureBackupBadge: Boolean,
    val accountManagementUrl: String?,
    val canReportBug: Boolean,
    val showLinkNewDevice: Boolean,
    val showAnalyticsSettings: Boolean,
    val showDeveloperSettings: Boolean,
    val canDeactivateAccount: Boolean,
    val nbOfBlockedUsers: Int,
    val showLabsItem: Boolean,
    val directLogoutState: DirectLogoutState,
    val snackbarMessage: SnackbarMessage?,
    val eventSink: (PreferencesRootEvent) -> Unit,
) {
    val showBlockedUsersItem = nbOfBlockedUsers > 0
}
