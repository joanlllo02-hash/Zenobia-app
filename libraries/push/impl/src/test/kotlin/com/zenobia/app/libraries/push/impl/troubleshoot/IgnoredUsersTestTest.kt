/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.push.impl.troubleshoot

import com.google.common.truth.Truth.assertThat
import com.zenobia.app.libraries.matrix.test.A_USER_ID
import com.zenobia.app.libraries.matrix.test.A_USER_ID_2
import com.zenobia.app.libraries.matrix.test.FakeMatrixClient
import com.zenobia.app.libraries.troubleshoot.api.test.NotificationTroubleshootNavigator
import com.zenobia.app.libraries.troubleshoot.api.test.NotificationTroubleshootTestState
import com.zenobia.app.libraries.troubleshoot.test.runAndTestState
import com.zenobia.app.services.toolbox.test.strings.FakeStringProvider
import com.zenobia.app.tests.testutils.lambda.lambdaRecorder
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test

class IgnoredUsersTestTest {
    @Test
    fun `test IgnoredUsersTest order`() = runTest {
        val sut = IgnoredUsersTest(
            matrixClient = FakeMatrixClient(),
            stringProvider = FakeStringProvider(),
        )
        assertThat(sut.order).isEqualTo(80)
    }

    @Test
    fun `test IgnoredUsersTest quick fix`() = runTest {
        val sut = IgnoredUsersTest(
            matrixClient = FakeMatrixClient(),
            stringProvider = FakeStringProvider(),
        )
        val openIgnoredUsersResult = lambdaRecorder<Unit> {}
        val navigator = object : NotificationTroubleshootNavigator {
            override fun navigateToBlockedUsers() = openIgnoredUsersResult()
        }
        sut.quickFix(
            coroutineScope = backgroundScope,
            navigator = navigator,
        )
        openIgnoredUsersResult.assertions().isCalledOnce()
    }

    @Test
    fun `test IgnoredUsersTest with no blocked users`() = runTest {
        val sut = IgnoredUsersTest(
            matrixClient = FakeMatrixClient(
                ignoredUsersFlow = MutableStateFlow(persistentListOf())
            ),
            stringProvider = FakeStringProvider(),
        )
        sut.runAndTestState {
            assertThat(awaitItem().status).isEqualTo(NotificationTroubleshootTestState.Status.Idle(true))
            assertThat(awaitItem().status).isEqualTo(NotificationTroubleshootTestState.Status.InProgress)
            val lastItem = awaitItem()
            assertThat(lastItem.status).isEqualTo(NotificationTroubleshootTestState.Status.Success)
        }
    }

    @Test
    fun `test IgnoredUsersTest with blocked users`() = runTest {
        val sut = IgnoredUsersTest(
            matrixClient = FakeMatrixClient(
                ignoredUsersFlow = MutableStateFlow(persistentListOf(A_USER_ID, A_USER_ID_2))
            ),
            stringProvider = FakeStringProvider(),
        )
        sut.runAndTestState {
            assertThat(awaitItem().status).isEqualTo(NotificationTroubleshootTestState.Status.Idle(true))
            assertThat(awaitItem().status).isEqualTo(NotificationTroubleshootTestState.Status.InProgress)
            val lastItem = awaitItem()
            val lastStatus = lastItem.status as NotificationTroubleshootTestState.Status.Failure
            assertThat(lastStatus.hasQuickFix).isTrue()
            assertThat(lastStatus.isCritical).isFalse()
            assertThat(lastStatus.quickFixButtonString).isNotNull()
            assertThat(lastItem.description).contains("2")
        }
    }
}
