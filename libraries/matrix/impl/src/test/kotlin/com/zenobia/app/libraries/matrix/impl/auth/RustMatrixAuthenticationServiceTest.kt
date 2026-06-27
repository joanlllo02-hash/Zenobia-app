/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.impl.auth

import com.google.common.truth.Truth.assertThat
import com.zenobia.app.features.enterprise.api.EnterpriseService
import com.zenobia.app.features.enterprise.test.FakeEnterpriseService
import com.zenobia.app.libraries.matrix.impl.ClientBuilderProvider
import com.zenobia.app.libraries.matrix.impl.FakeClientBuilderProvider
import com.zenobia.app.libraries.matrix.impl.createRustMatrixClientFactory
import com.zenobia.app.libraries.matrix.impl.fixtures.fakes.FakeFfiClient
import com.zenobia.app.libraries.matrix.impl.fixtures.fakes.FakeFfiClientBuilder
import com.zenobia.app.libraries.matrix.impl.fixtures.fakes.FakeFfiHomeserverLoginDetails
import com.zenobia.app.libraries.matrix.impl.paths.SessionPathsFactory
import com.zenobia.app.libraries.matrix.test.auth.FakeOAuthRedirectUrlProvider
import com.zenobia.app.libraries.matrix.test.core.aBuildMeta
import com.zenobia.app.libraries.sessionstorage.api.SessionStore
import com.zenobia.app.libraries.sessionstorage.test.InMemorySessionStore
import com.zenobia.app.tests.testutils.testCoroutineDispatchers
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.File

class RustMatrixAuthenticationServiceTest {
    @Test
    fun `setHomeserver is successful`() = runTest {
        val sut = createRustMatrixAuthenticationService(
            clientBuilderProvider = FakeClientBuilderProvider(
                provideResult = {
                    FakeFfiClientBuilder(
                        buildResult = {
                            FakeFfiClient(
                                homeserverLoginDetailsResult = {
                                    FakeFfiHomeserverLoginDetails()
                                }
                            )
                        }
                    )
                }
            ),
        )
        assertThat(sut.setHomeserver("matrix.org").isSuccess).isTrue()
    }

    private fun TestScope.createRustMatrixAuthenticationService(
        sessionStore: SessionStore = InMemorySessionStore(),
        clientBuilderProvider: ClientBuilderProvider = FakeClientBuilderProvider(),
        enterpriseService: EnterpriseService = FakeEnterpriseService(),
    ): RustMatrixAuthenticationService {
        val baseDirectory = File("/base")
        val cacheDirectory = File("/cache")
        val rustMatrixClientFactory = createRustMatrixClientFactory(
            cacheDirectory = cacheDirectory,
            sessionStore = sessionStore,
            clientBuilderProvider = clientBuilderProvider,
        )
        return RustMatrixAuthenticationService(
            sessionPathsFactory = SessionPathsFactory(baseDirectory, cacheDirectory),
            coroutineDispatchers = testCoroutineDispatchers(),
            sessionStore = sessionStore,
            rustMatrixClientFactory = rustMatrixClientFactory,
            secretGenerator = FakeSecretGenerator(),
            oAuthConfigurationProvider = OAuthConfigurationProvider(
                buildMeta = aBuildMeta(),
                oAuthRedirectUrlProvider = FakeOAuthRedirectUrlProvider(),
            ),
            enterpriseService = enterpriseService,
        )
    }
}
