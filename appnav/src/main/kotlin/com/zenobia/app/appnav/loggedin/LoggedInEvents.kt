/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.appnav.loggedin

sealed interface LoggedInEvents {
    data class CloseErrorDialog(val doNotShowAgain: Boolean) : LoggedInEvents
    data object CheckSlidingSyncProxyAvailability : LoggedInEvents
    data object LogoutAndMigrateToNativeSlidingSync : LoggedInEvents
}
