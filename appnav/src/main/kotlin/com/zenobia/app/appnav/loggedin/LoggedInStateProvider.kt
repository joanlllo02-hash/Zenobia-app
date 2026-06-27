/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.appnav.loggedin

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.push.api.PusherRegistrationFailure

open class LoggedInStateProvider : PreviewParameterProvider<LoggedInState> {
    override val values: Sequence<LoggedInState>
        get() = sequenceOf(
            aLoggedInState(),
            aLoggedInState(showSyncSpinner = true),
            aLoggedInState(pusherRegistrationState = AsyncData.Failure(PusherRegistrationFailure.NoDistributorsAvailable())),
            aLoggedInState(forceNativeSlidingSyncMigration = true),
        )
}

fun aLoggedInState(
    showSyncSpinner: Boolean = false,
    pusherRegistrationState: AsyncData<Unit> = AsyncData.Uninitialized,
    forceNativeSlidingSyncMigration: Boolean = false,
    appName: String = "Zenobia",
) = LoggedInState(
    showSyncSpinner = showSyncSpinner,
    pusherRegistrationState = pusherRegistrationState,
    ignoreRegistrationError = false,
    forceNativeSlidingSyncMigration = forceNativeSlidingSyncMigration,
    appName = appName,
    eventSink = {},
)
