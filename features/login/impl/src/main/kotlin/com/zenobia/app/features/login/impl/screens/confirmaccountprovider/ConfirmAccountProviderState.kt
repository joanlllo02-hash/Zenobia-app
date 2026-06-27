/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.login.impl.screens.confirmaccountprovider

import com.zenobia.app.features.login.impl.accountprovider.AccountProvider
import com.zenobia.app.features.login.impl.login.LoginMode
import com.zenobia.app.libraries.architecture.AsyncData

data class ConfirmAccountProviderState(
    val accountProvider: AccountProvider,
    val isAccountCreation: Boolean,
    val loginMode: AsyncData<LoginMode>,
    val eventSink: (ConfirmAccountProviderEvents) -> Unit
) {
    val submitEnabled: Boolean get() = accountProvider.url.isNotEmpty() && (loginMode is AsyncData.Uninitialized || loginMode is AsyncData.Loading)
}
