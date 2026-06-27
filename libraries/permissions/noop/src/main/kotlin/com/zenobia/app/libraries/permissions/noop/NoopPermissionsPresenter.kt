/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.permissions.noop

import androidx.compose.runtime.Composable
import com.zenobia.app.libraries.permissions.api.PermissionsPresenter
import com.zenobia.app.libraries.permissions.api.PermissionsState

class NoopPermissionsPresenter(
    private val isGranted: Boolean = false,
) : PermissionsPresenter {
    @Composable
    override fun present(): PermissionsState {
        return PermissionsState(
            permission = "",
            permissionGranted = isGranted,
            shouldShowRationale = false,
            showDialog = false,
            permissionAlreadyAsked = false,
            permissionAlreadyDenied = false,
            eventSink = {},
        )
    }
}
