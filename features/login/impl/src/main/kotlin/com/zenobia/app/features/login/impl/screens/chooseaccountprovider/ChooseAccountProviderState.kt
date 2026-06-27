/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.login.impl.screens.chooseaccountprovider

import com.zenobia.app.features.login.impl.accountprovider.AccountProvider
import com.zenobia.app.features.login.impl.login.LoginMode
import com.zenobia.app.libraries.architecture.AsyncData
import kotlinx.collections.immutable.ImmutableList

data class ChooseAccountProviderState(
    val accountProviders: ImmutableList<AccountProvider>,
    val selectedAccountProvider: AccountProvider?,
    val loginMode: AsyncData<LoginMode>,
    val eventSink: (ChooseAccountProviderEvents) -> Unit,
) {
    val submitEnabled: Boolean
        get() = selectedAccountProvider != null && (loginMode is AsyncData.Uninitialized || loginMode is AsyncData.Loading)
}
