/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.login.impl.screens.createaccount

import com.google.common.truth.Truth.assertThat
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.core.meta.BuildMeta
import com.zenobia.app.libraries.matrix.api.auth.MatrixAuthenticationService
import com.zenobia.app.libraries.matrix.api.auth.external.ExternalSession
import com.zenobia.app.libraries.matrix.api.verification.SessionVerifiedStatus
import com.zenobia.app.libraries.matrix.test.AN_EXCEPTION
import com.zenobia.app.libraries.matrix.test.A_SESSION_ID
import com.zenobia.app.libraries.matrix.test.auth.FakeMatrixAuthenticationService
import com.zenobia.app.libraries.matrix.test.core.aBuildMeta
import com.zenobia.app.libraries.matrix.test.verification.FakeSessionVerificationService
import com.zenobia.app.tests.testutils.WarmUpRule
import com.zenobia.app.tests.testutils.lambda.lambdaRecorder
import com.zenobia.app.tests.testutils.lambda.value
import com.zenobia.app.tests.testutils.test
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class CreateAccountPresenterTest {
    @get:Rule
    val warmUpRule = WarmUpRule()

    @Test
    fun `present - initial state`() = runTest {
        val presenter = createPresenter()
        presenter.test {
            val initialState = awaitItem()
            assertThat(initialState.url).isEqualTo("aUrl")
            assertThat(initialState.pageProgress).isEqualTo(0)
            assertThat(initialState.createAction).isEqualTo(AsyncAction.Uninitialized)
            assertThat(initialState.isDebugBuild).isTrue()
        }
    }

    @Test
    fun `present - set up progress update the state`() = runTest {
        val presenter = createPresenter()
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink(CreateAccountEvents.SetPageProgress(33))
            assertThat(awaitItem().pageProgress).isEqualTo(33)
        }
    }

    @Test
    fun `present - receiving a message not able to be parsed change the state to error`() = runTest {
        val presenter = createPresenter(
            messageParser = FakeMessageParser { error("An error") }
        )
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink(CreateAccountEvents.OnMessageReceived(""))
            assertThat(awaitItem().createAction).isInstanceOf(AsyncAction.Failure::class.java)
        }
    }

    @Test
    fun `present - receiving a message containing isTrusted is ignored`() = runTest {
        val presenter = createPresenter()
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink(CreateAccountEvents.OnMessageReceived("isTrusted"))
        }
    }

    @Test
    fun `present - receiving a message able to be parsed change the state to success`() = runTest {
        val lambda = lambdaRecorder<String, ExternalSession> { _ -> anExternalSession() }
        val sessionVerificationService = FakeSessionVerificationService()
        val presenter = createPresenter(
            authenticationService = FakeMatrixAuthenticationService(
                importCreatedSessionLambda = { Result.success(A_SESSION_ID) }
            ),
            messageParser = FakeMessageParser(lambda),
        )
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink(CreateAccountEvents.OnMessageReceived("aMessage"))
            assertThat(awaitItem().createAction.isLoading()).isTrue()
            sessionVerificationService.emitVerifiedStatus(SessionVerifiedStatus.Verified)
            assertThat(awaitItem().createAction.dataOrNull()).isEqualTo(A_SESSION_ID)
        }
        lambda.assertions().isCalledOnce().with(value("aMessage"))
    }

    @Test
    fun `present - receiving a message able to be parsed but error in importing change the state to error`() = runTest {
        val presenter = createPresenter(
            authenticationService = FakeMatrixAuthenticationService(
                importCreatedSessionLambda = { Result.failure(AN_EXCEPTION) }
            ),
            messageParser = FakeMessageParser { anExternalSession() }
        )
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink(CreateAccountEvents.OnMessageReceived(""))
            assertThat(awaitItem().createAction.isLoading()).isTrue()
            assertThat(awaitItem().createAction.errorOrNull()).isNotNull()
        }
    }

    private fun createPresenter(
        url: String = "aUrl",
        authenticationService: MatrixAuthenticationService = FakeMatrixAuthenticationService(),
        messageParser: MessageParser = FakeMessageParser(),
        buildMeta: BuildMeta = aBuildMeta(),
    ) = CreateAccountPresenter(
        url = url,
        authenticationService = authenticationService,
        messageParser = messageParser,
        buildMeta = buildMeta,
    )
}
