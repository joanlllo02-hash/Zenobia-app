/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.login.impl.screens.changeaccountprovider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.zacsweers.metro.Inject
import com.zenobia.app.appconfig.AuthenticationConfig
import com.zenobia.app.features.enterprise.api.EnterpriseService
import com.zenobia.app.features.enterprise.api.canConnectToAnyHomeserver
import com.zenobia.app.features.login.impl.accountprovider.AccountProvider
import com.zenobia.app.features.login.impl.changeserver.ChangeServerState
import com.zenobia.app.libraries.architecture.Presenter
import com.zenobia.app.libraries.core.uri.ensureProtocol
import kotlinx.collections.immutable.toImmutableList

@Inject
class ChangeAccountProviderPresenter(
    private val changeServerPresenter: Presenter<ChangeServerState>,
    private val enterpriseService: EnterpriseService,
) : Presenter<ChangeAccountProviderState> {
    @Composable
    override fun present(): ChangeAccountProviderState {
        val staticAccountProviderList = remember {
            enterpriseService.defaultHomeserverList()
                .filter { it != EnterpriseService.ANY_ACCOUNT_PROVIDER }
                .map { it.ensureProtocol() }
                .ifEmpty { listOf(AuthenticationConfig.MATRIX_ORG_URL) }
                .map { url ->
                    AccountProvider(
                        url = url,
                        subtitle = null,
                        isPublic = url == AuthenticationConfig.MATRIX_ORG_URL,
                        isMatrixOrg = url == AuthenticationConfig.MATRIX_ORG_URL,
                    )
                }
                .toImmutableList()
        }

        val canSearchForAccountProviders = remember {
            enterpriseService.canConnectToAnyHomeserver()
        }

        val changeServerState = changeServerPresenter.present()
        return ChangeAccountProviderState(
            accountProviders = staticAccountProviderList,
            canSearchForAccountProviders = canSearchForAccountProviders,
            changeServerState = changeServerState,
        )
    }
}
