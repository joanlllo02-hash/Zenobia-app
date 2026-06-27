/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.linknewdevice.impl.screens.desktop

import android.Manifest
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.zenobia.app.libraries.permissions.api.PermissionsState
import com.zenobia.app.libraries.permissions.api.aPermissionsState

open class DesktopNoticeStateProvider : PreviewParameterProvider<DesktopNoticeState> {
    override val values: Sequence<DesktopNoticeState>
        get() = sequenceOf(
            aDesktopNoticeState(),
            aDesktopNoticeState(cameraPermissionState = aPermissionsState(showDialog = true, permission = Manifest.permission.CAMERA)),
        )
}

fun aDesktopNoticeState(
    cameraPermissionState: PermissionsState = aPermissionsState(
        showDialog = false,
        permission = Manifest.permission.CAMERA,
    ),
    canContinue: Boolean = false,
    eventSink: (DesktopNoticeEvent) -> Unit = {},
) = DesktopNoticeState(
    cameraPermissionState = cameraPermissionState,
    canContinue = canContinue,
    eventSink = eventSink
)
