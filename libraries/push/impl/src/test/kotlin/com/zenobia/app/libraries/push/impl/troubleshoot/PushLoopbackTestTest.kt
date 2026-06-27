/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.push.impl.troubleshoot

import com.google.common.truth.Truth.assertThat
import com.zenobia.app.libraries.matrix.api.core.SessionId
import com.zenobia.app.libraries.matrix.test.AN_EXCEPTION
import com.zenobia.app.libraries.matrix.test.A_FAILURE_REASON
import com.zenobia.app.libraries.matrix.test.A_SESSION_ID
import com.zenobia.app.libraries.push.api.PushService
import com.zenobia.app.libraries.push.api.gateway.PushGatewayFailure
import com.zenobia.app.libraries.push.test.FakePushService
import com.zenobia.app.libraries.pushproviders.test.FakePushProvider
import com.zenobia.app.libraries.troubleshoot.api.test.NotificationTroubleshootTestState
import com.zenobia.app.libraries.troubleshoot.test.FakeNotificationTroubleshootNavigator
import com.zenobia.app.libraries.troubleshoot.test.runAndTestState
import com.zenobia.app.services.toolbox.api.strings.StringProvider
import com.zenobia.app.services.toolbox.api.systemclock.SystemClock
import com.zenobia.app.services.toolbox.test.strings.FakeStringProvider
import com.zenobia.app.services.toolbox.test.systemclock.FakeSystemClock
import com.zenobia.app.tests.testutils.lambda.lambdaRecorder
import kotlinx.coroutines.test.runTest
import org.junit.Test

class PushLoopbackTestTest {
    @Test
    fun `test PushLoopbackTest timeout - push is not received`() = runTest {
        val sut = createPushLoopbackTest()
        sut.runAndTestState {
            assertThat(awaitItem().status).isEqualTo(NotificationTroubleshootTestState.Status.Idle(true))
            assertThat(awaitItem().status).isEqualTo(NotificationTroubleshootTestState.Status.InProgress)
            val lastItem = awaitItem()
            assertThat(lastItem.status).isEqualTo(NotificationTroubleshootTestState.Status.Failure())
        }
    }

    @Test
    fun `test PushLoopbackTest PusherRejected error`() = runTest {
        val sut = createPushLoopbackTest(
            pushService = FakePushService(
                testPushBlock = {
                    throw PushGatewayFailure.PusherRejected()
                }
            ),
        )
        sut.runAndTestState {
            assertThat(awaitItem().status).isEqualTo(NotificationTroubleshootTestState.Status.Idle(true))
            assertThat(awaitItem().status).isEqualTo(NotificationTroubleshootTestState.Status.InProgress)
            val lastItem = awaitItem()
            assertThat(lastItem.status).isEqualTo(NotificationTroubleshootTestState.Status.Failure())
            sut.reset()
            assertThat(awaitItem().status).isEqualTo(NotificationTroubleshootTestState.Status.Idle(true))
        }
    }

    @Test
    fun `test PushLoopbackTest PusherRejected error with quick fix`() = runTest {
        val rotateTokenLambda = lambdaRecorder<Result<Unit>> { Result.success(Unit) }
        val sut = createPushLoopbackTest(
            pushService = FakePushService(
                testPushBlock = {
                    throw PushGatewayFailure.PusherRejected()
                },
                currentPushProvider = {
                    FakePushProvider(
                        canRotateTokenResult = { true },
                        rotateTokenLambda = rotateTokenLambda,
                    )
                }
            ),
        )
        sut.runAndTestState {
            assertThat(awaitItem().status).isEqualTo(NotificationTroubleshootTestState.Status.Idle(true))
            assertThat(awaitItem().status).isEqualTo(NotificationTroubleshootTestState.Status.InProgress)
            val lastItem = awaitItem()
            assertThat(lastItem.status).isEqualTo(NotificationTroubleshootTestState.Status.Failure(hasQuickFix = true))
            sut.quickFix(this, FakeNotificationTroubleshootNavigator())
            assertThat(awaitItem().status).isEqualTo(NotificationTroubleshootTestState.Status.InProgress)
            assertThat(awaitItem().status).isEqualTo(NotificationTroubleshootTestState.Status.Failure(hasQuickFix = true))
            rotateTokenLambda.assertions().isCalledOnce()
        }
    }

    @Test
    fun `test PushLoopbackTest setup error`() = runTest {
        val sut = createPushLoopbackTest(
            pushService = FakePushService(
                testPushBlock = { false }
            ),
        )
        sut.runAndTestState {
            assertThat(awaitItem().status).isEqualTo(NotificationTroubleshootTestState.Status.Idle(true))
            assertThat(awaitItem().status).isEqualTo(NotificationTroubleshootTestState.Status.InProgress)
            val lastItem = awaitItem()
            assertThat(lastItem.status).isEqualTo(NotificationTroubleshootTestState.Status.Failure())
        }
    }

    @Test
    fun `test PushLoopbackTest other error`() = runTest {
        val sut = createPushLoopbackTest(
            pushService = FakePushService(
                testPushBlock = {
                    throw AN_EXCEPTION
                }
            ),
        )
        sut.runAndTestState {
            assertThat(awaitItem().status).isEqualTo(NotificationTroubleshootTestState.Status.Idle(true))
            assertThat(awaitItem().status).isEqualTo(NotificationTroubleshootTestState.Status.InProgress)
            val lastItem = awaitItem()
            assertThat(lastItem.status).isEqualTo(NotificationTroubleshootTestState.Status.Failure())
            assertThat(lastItem.description).contains(A_FAILURE_REASON)
        }
    }

    @Test
    fun `test PushLoopbackTest push is received`() = runTest {
        val diagnosticPushHandler = DiagnosticPushHandler()
        val sut = createPushLoopbackTest(
            pushService = FakePushService(testPushBlock = {
                diagnosticPushHandler.handlePush()
                true
            }),
            diagnosticPushHandler = diagnosticPushHandler,
        )
        sut.runAndTestState {
            assertThat(awaitItem().status).isEqualTo(NotificationTroubleshootTestState.Status.Idle(true))
            assertThat(awaitItem().status).isEqualTo(NotificationTroubleshootTestState.Status.InProgress)
            val lastItem = awaitItem()
            assertThat(lastItem.status).isEqualTo(NotificationTroubleshootTestState.Status.Success)
        }
    }
}

private fun createPushLoopbackTest(
    sessionId: SessionId = A_SESSION_ID,
    pushService: PushService = FakePushService(),
    diagnosticPushHandler: DiagnosticPushHandler = DiagnosticPushHandler(),
    clock: SystemClock = FakeSystemClock(),
    stringProvider: StringProvider = FakeStringProvider(),
) = PushLoopbackTest(
    sessionId = sessionId,
    pushService = pushService,
    diagnosticPushHandler = diagnosticPushHandler,
    clock = clock,
    stringProvider = stringProvider
)
