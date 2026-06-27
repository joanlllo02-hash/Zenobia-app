/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.login.impl.login

import com.zenobia.app.libraries.matrix.api.auth.OAuthDetails

sealed interface LoginMode {
    data object PasswordLogin : LoginMode
    data class OAuth(val oAuthDetails: OAuthDetails) : LoginMode
    data class AccountCreation(val url: String) : LoginMode
}
