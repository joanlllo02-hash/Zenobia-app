/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.login.impl.changeserver

import com.google.common.truth.Truth.assertThat
import com.zenobia.app.features.enterprise.api.EnterpriseService
import com.zenobia.app.features.enterprise.test.FakeEnterpriseService
import com.zenobia.app.features.login.impl.accesscontrol.DefaultAccountProviderAccessControl
import com.zenobia.app.features.login.impl.accountprovider.AccountProvider
import com.zenobia.app.features.login.impl.accountprovider.AccountProviderDataSource
import com.zenobia.app.features.login.impl.error.ChangeServerError
import com.zenobia.app.features.wellknown.test.FakeWellknownRetriever
import com.zenobia.app.features.wellknown.test.anElementWellKnown
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.core.uri.ensureProtocol
import com.zenobia.app.libraries.matrix.test.AN_EXCEPTION
import com.zenobia.app.libraries.matrix.test.A_HOMESERVER_URL
import com.zenobia.app.libraries.matrix.test.auth.FakeMatrixAuthenticationService
import com.zenobia.app.libraries.matrix.test.auth.aMatrixHomeServerDetails
import com.zenobia.app.libraries.wellknown.api.ElementWellKnown
import com.zenobia.app.libraries.wellknown.api.WellknownRetriever
import com.zenobia.app.libraries.wellknown.api.WellknownRetrieverResult
import com.zenobia.app.tests.testutils.WarmUpRule
import com.zenobia.app.tests.testutils.lambda.lambdaRecorder
import com.zenobia.app.tests.testutils.lambda.value
import com.zenobia.app.tests.testutils.test
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ChangeServerPresenterTest {
    @get:Rule
    val warmUpRule = WarmUpRule()

    @Test
    fun `present - initial state`() = runTest {
        createPresenter().test {
            val initialState = awaitItem()
            assertThat(initialState.changeServerAction).isEqualTo(AsyncData.Uninitialized)
        }
    }

    @Test
    fun `present - change server ok`() = runTest {
        val authenticationService = FakeMatrixAuthenticationService(
            setHomeserverResult = {
                Result.success(aMatrixHomeServerDetails(supportsOAuthLogin = true))
            },
        )
        createPresenter(
            authenticationService = authenticationService,
            enterpriseService = FakeEnterpriseService(
                isAllowedToConnectToHomeserverResult = { true },
            ),
        ).test {
            val initialState = awaitItem()
            assertThat(initialState.changeServerAction).isEqualTo(AsyncData.Uninitialized)
            initialState.eventSink.invoke(ChangeServerEvents.ChangeServer(AccountProvider(url = A_HOMESERVER_URL)))
            val loadingState = awaitItem()
            assertThat(loadingState.changeServerAction).isInstanceOf(AsyncData.Loading::class.java)
            val successState = awaitItem()
            assertThat(successState.changeServerAction).isEqualTo(AsyncData.Success(Unit))
        }
    }

    @Test
    fun `present - change server error`() = runTest {
        val authenticationService = FakeMatrixAuthenticationService(
            setHomeserverResult = {
                Result.failure(AN_EXCEPTION)
            },
        )
        createPresenter(
            enterpriseService = FakeEnterpriseService(
                isAllowedToConnectToHomeserverResult = { true },
            ),
            authenticationService = authenticationService,
        ).test {
            val initialState = awaitItem()
            assertThat(initialState.changeServerAction).isEqualTo(AsyncData.Uninitialized)
            initialState.eventSink.invoke(ChangeServerEvents.ChangeServer(AccountProvider(url = A_HOMESERVER_URL)))
            val loadingState = awaitItem()
            assertThat(loadingState.changeServerAction).isInstanceOf(AsyncData.Loading::class.java)
            val failureState = awaitItem()
            assertThat(failureState.changeServerAction).isInstanceOf(AsyncData.Failure::class.java)
            // Clear error
            failureState.eventSink.invoke(ChangeServerEvents.ClearError)
            val finalState = awaitItem()
            assertThat(finalState.changeServerAction).isEqualTo(AsyncData.Uninitialized)
        }
    }

    @Test
    fun `present - change server unsupported server`() = runTest {
        val authenticationService = FakeMatrixAuthenticationService(
            setHomeserverResult = {
                Result.success(aMatrixHomeServerDetails())
            },
        )
        createPresenter(
            enterpriseService = FakeEnterpriseService(
                isAllowedToConnectToHomeserverResult = { true },
            ),
            authenticationService = authenticationService,
        ).test {
            val initialState = awaitItem()
            assertThat(initialState.changeServerAction).isEqualTo(AsyncData.Uninitialized)
            initialState.eventSink.invoke(ChangeServerEvents.ChangeServer(AccountProvider(url = A_HOMESERVER_URL)))
            val loadingState = awaitItem()
            assertThat(loadingState.changeServerAction).isInstanceOf(AsyncData.Loading::class.java)
            val failureState = awaitItem()
            assertThat(failureState.changeServerAction).isInstanceOf(AsyncData.Failure::class.java)
            assertThat(failureState.changeServerAction.errorOrNull()).isEqualTo(
                ChangeServerError.UnsupportedServer
            )
        }
    }

    @Test
    fun `present - change server not allowed error`() = runTest {
        val isAllowedToConnectToHomeserverResult = lambdaRecorder<String, Boolean> { false }
        createPresenter(
            enterpriseService = FakeEnterpriseService(
                isAllowedToConnectToHomeserverResult = isAllowedToConnectToHomeserverResult,
                defaultHomeserverListResult = { listOf("element.io") },
            ),
        ).test {
            val initialState = awaitItem()
            assertThat(initialState.changeServerAction).isEqualTo(AsyncData.Uninitialized)
            val anAccountProvider = AccountProvider(url = A_HOMESERVER_URL)
            initialState.eventSink.invoke(ChangeServerEvents.ChangeServer(anAccountProvider))
            val loadingState = awaitItem()
            assertThat(loadingState.changeServerAction).isInstanceOf(AsyncData.Loading::class.java)
            val failureState = awaitItem()
            assertThat(
                (failureState.changeServerAction.errorOrNull() as ChangeServerError.UnauthorizedAccountProvider).unauthorisedAccountProviderTitle
            ).isEqualTo(anAccountProvider.title)
            assertThat(
                (failureState.changeServerAction.errorOrNull() as ChangeServerError.UnauthorizedAccountProvider).authorisedAccountProviderTitles
            ).containsExactly("element.io")
            isAllowedToConnectToHomeserverResult.assertions()
                .isCalledOnce()
                .with(value(A_HOMESERVER_URL))
        }
    }

    @Test
    fun `present - change server element pro required error`() = runTest {
        val getElementWellKnownResult = lambdaRecorder<String, WellknownRetrieverResult<ElementWellKnown>> {
            WellknownRetrieverResult.Success(
                anElementWellKnown(
                    enforceElementPro = true,
                )
            )
        }
        createPresenter(
            wellknownRetriever = FakeWellknownRetriever(
                getElementWellKnownResult = getElementWellKnownResult,
            ),
        ).test {
            val initialState = awaitItem()
            assertThat(initialState.changeServerAction).isEqualTo(AsyncData.Uninitialized)
            val anAccountProvider = AccountProvider(url = A_HOMESERVER_URL)
            initialState.eventSink.invoke(ChangeServerEvents.ChangeServer(anAccountProvider))
            val loadingState = awaitItem()
            assertThat(loadingState.changeServerAction).isInstanceOf(AsyncData.Loading::class.java)
            val failureState = awaitItem()
            assertThat(
                (failureState.changeServerAction.errorOrNull() as ChangeServerError.NeedElementPro).unauthorisedAccountProviderTitle
            ).isEqualTo(anAccountProvider.title)
            assertThat(
                (failureState.changeServerAction.errorOrNull() as ChangeServerError.NeedElementPro).applicationId
            ).isEqualTo("io.element.enterprise")
            getElementWellKnownResult.assertions()
                .isCalledOnce()
                .with(value(A_HOMESERVER_URL.ensureProtocol()))
        }
    }

    private fun createPresenter(
        authenticationService: FakeMatrixAuthenticationService = FakeMatrixAuthenticationService(),
        accountProviderDataSource: AccountProviderDataSource = AccountProviderDataSource(FakeEnterpriseService()),
        enterpriseService: EnterpriseService = FakeEnterpriseService(),
        wellknownRetriever: WellknownRetriever = FakeWellknownRetriever(),
    ) = ChangeServerPresenter(
        authenticationService = authenticationService,
        accountProviderDataSource = accountProviderDataSource,
        defaultAccountProviderAccessControl = DefaultAccountProviderAccessControl(
            enterpriseService = enterpriseService,
            wellknownRetriever = wellknownRetriever,
        ),
    )
}
