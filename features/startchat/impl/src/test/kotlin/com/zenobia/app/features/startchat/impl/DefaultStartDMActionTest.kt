/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.startchat.impl

import androidx.compose.runtime.mutableStateOf
import com.google.common.truth.Truth.assertThat
import im.vector.app.features.analytics.plan.CreatedRoom
import com.zenobia.app.features.startchat.api.ConfirmingStartDmWithMatrixUser
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.matrix.api.MatrixClient
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.api.encryption.identity.IdentityState
import com.zenobia.app.libraries.matrix.test.AN_EXCEPTION
import com.zenobia.app.libraries.matrix.test.A_ROOM_ID
import com.zenobia.app.libraries.matrix.test.FakeMatrixClient
import com.zenobia.app.libraries.matrix.test.encryption.FakeEncryptionService
import com.zenobia.app.libraries.matrix.ui.components.aMatrixUser
import com.zenobia.app.services.analytics.api.AnalyticsService
import com.zenobia.app.services.analytics.test.FakeAnalyticsService
import com.zenobia.app.tests.testutils.lambda.lambdaRecorder
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultStartDMActionTest {
    @Test
    fun `when dm is found, assert state is updated with given room id`() = runTest {
        val matrixClient = FakeMatrixClient().apply {
            givenFindDmResult(Result.success(A_ROOM_ID))
        }
        val analyticsService = FakeAnalyticsService()
        val action = createStartDMAction(matrixClient, analyticsService)
        val state = mutableStateOf<AsyncAction<RoomId>>(AsyncAction.Uninitialized)
        action.execute(aMatrixUser(), true, state)
        assertThat(state.value).isEqualTo(AsyncAction.Success(A_ROOM_ID))
        assertThat(analyticsService.capturedEvents).isEmpty()
    }

    @Test
    fun `when finding the dm fails, assert state is updated with given error`() = runTest {
        val matrixClient = FakeMatrixClient().apply {
            givenFindDmResult(Result.failure(AN_EXCEPTION))
        }
        val analyticsService = FakeAnalyticsService()
        val action = createStartDMAction(matrixClient, analyticsService)
        val state = mutableStateOf<AsyncAction<RoomId>>(AsyncAction.Uninitialized)
        action.execute(aMatrixUser(), true, state)
        assertThat(state.value).isEqualTo(AsyncAction.Failure(AN_EXCEPTION))
        assertThat(analyticsService.capturedEvents).isEmpty()
    }

    @Test
    fun `when dm is not found, assert dm is created, state is updated with given room id and analytics get called`() = runTest {
        val matrixClient = FakeMatrixClient().apply {
            givenFindDmResult(Result.success(null))
            givenCreateDmResult(Result.success(A_ROOM_ID))
        }
        val analyticsService = FakeAnalyticsService()
        val action = createStartDMAction(matrixClient, analyticsService)
        val state = mutableStateOf<AsyncAction<RoomId>>(AsyncAction.Uninitialized)
        action.execute(aMatrixUser(), true, state)
        assertThat(state.value).isEqualTo(AsyncAction.Success(A_ROOM_ID))
        assertThat(analyticsService.capturedEvents).containsExactly(CreatedRoom(isDM = true))
    }

    @Test
    fun `when dm is not found, and createIfDmDoesNotExist is false, assert dm is not created and state is updated to confirmation state`() = runTest {
        val encryptionService = FakeEncryptionService(
            getUserIdentityResult = { Result.success(null) }
        )
        val matrixClient = FakeMatrixClient(
            encryptionService = encryptionService
        ).apply {
            givenFindDmResult(Result.success(null))
            givenCreateDmResult(Result.success(A_ROOM_ID))
        }
        val analyticsService = FakeAnalyticsService()
        val action = createStartDMAction(matrixClient, analyticsService)
        val state = mutableStateOf<AsyncAction<RoomId>>(AsyncAction.Uninitialized)
        val matrixUser = aMatrixUser()
        action.execute(matrixUser, false, state)
        assertThat(state.value).isEqualTo(ConfirmingStartDmWithMatrixUser(matrixUser, isUserIdentityUnknown = true))
        assertThat(analyticsService.capturedEvents).isEmpty()
    }

    @Test
    fun `when dm creation fails, assert state is updated with given error`() = runTest {
        val matrixClient = FakeMatrixClient().apply {
            givenFindDmResult(Result.success(null))
            givenCreateDmResult(Result.failure(AN_EXCEPTION))
        }
        val analyticsService = FakeAnalyticsService()
        val action = createStartDMAction(matrixClient, analyticsService)
        val state = mutableStateOf<AsyncAction<RoomId>>(AsyncAction.Uninitialized)
        action.execute(aMatrixUser(), true, state)
        assertThat(state.value).isEqualTo(AsyncAction.Failure(AN_EXCEPTION))
        assertThat(analyticsService.capturedEvents).isEmpty()
    }

    @Test
    fun `when user identity fetched and identity unknown`() = runTest {
        val getUserIdentityResult = lambdaRecorder<UserId, Result<IdentityState?>> { _ -> Result.success(null) }
        val encryptionService = FakeEncryptionService(getUserIdentityResult = getUserIdentityResult)
        val matrixClient = FakeMatrixClient(encryptionService = encryptionService).apply {
            givenFindDmResult(Result.success(null))
        }

        val action = createStartDMAction(
            matrixClient = matrixClient,
        )
        val state = mutableStateOf<AsyncAction<RoomId>>(AsyncAction.Uninitialized)

        action.execute(aMatrixUser(), false, state)

        getUserIdentityResult.assertions().isCalledOnce()
        assertThat(state.value).isEqualTo(ConfirmingStartDmWithMatrixUser(aMatrixUser(), isUserIdentityUnknown = true))
    }

    private fun createStartDMAction(
        matrixClient: MatrixClient = FakeMatrixClient(),
        analyticsService: AnalyticsService = FakeAnalyticsService(),
    ): DefaultStartDMAction {
        return DefaultStartDMAction(
            matrixClient = matrixClient,
            analyticsService = analyticsService,
        )
    }
}
