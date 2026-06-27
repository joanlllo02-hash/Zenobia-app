/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.appnav.loggedin

import com.zenobia.app.libraries.architecture.AsyncData

data class LoggedInState(
    val showSyncSpinner: Boolean,
    val pusherRegistrationState: AsyncData<Unit>,
    val ignoreRegistrationError: Boolean,
    val forceNativeSlidingSyncMigration: Boolean,
    val appName: String,
    val eventSink: (LoggedInEvents) -> Unit,
)
