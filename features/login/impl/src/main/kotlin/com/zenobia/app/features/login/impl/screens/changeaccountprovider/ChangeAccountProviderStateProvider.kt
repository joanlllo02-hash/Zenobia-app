/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.login.impl.screens.changeaccountprovider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.zenobia.app.features.login.impl.accountprovider.AccountProvider
import com.zenobia.app.features.login.impl.accountprovider.anAccountProvider
import com.zenobia.app.features.login.impl.changeserver.ChangeServerState
import com.zenobia.app.features.login.impl.changeserver.aChangeServerState
import kotlinx.collections.immutable.toImmutableList

open class ChangeAccountProviderStateProvider : PreviewParameterProvider<ChangeAccountProviderState> {
    override val values: Sequence<ChangeAccountProviderState>
        get() = sequenceOf(
            aChangeAccountProviderState(),
            aChangeAccountProviderState(canSearchForAccountProviders = false),
            // Add other state here
        )
}

fun aChangeAccountProviderState(
    accountProviders: List<AccountProvider> = listOf(
        anAccountProvider()
    ),
    canSearchForAccountProviders: Boolean = true,
    changeServerState: ChangeServerState = aChangeServerState(),
) = ChangeAccountProviderState(
    accountProviders = accountProviders.toImmutableList(),
    canSearchForAccountProviders = canSearchForAccountProviders,
    changeServerState = changeServerState,
)
