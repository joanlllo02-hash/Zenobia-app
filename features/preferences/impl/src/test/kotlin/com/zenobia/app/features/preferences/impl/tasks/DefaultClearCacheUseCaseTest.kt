/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.preferences.impl.tasks

import androidx.test.platform.app.InstrumentationRegistry
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.zenobia.app.features.invite.test.InMemorySeenInvitesStore
import com.zenobia.app.features.preferences.impl.DefaultCacheService
import com.zenobia.app.libraries.matrix.api.core.SessionId
import com.zenobia.app.libraries.matrix.test.A_ROOM_ID
import com.zenobia.app.libraries.matrix.test.A_SESSION_ID
import com.zenobia.app.libraries.matrix.test.FakeMatrixClient
import com.zenobia.app.libraries.matrix.test.room.FakeJoinedRoom
import com.zenobia.app.libraries.push.test.FakePushService
import com.zenobia.app.libraries.sessionstorage.test.InMemoryCacheStore
import com.zenobia.app.libraries.sessionstorage.test.aCacheData
import com.zenobia.app.services.appnavstate.impl.DefaultActiveRoomsHolder
import com.zenobia.app.tests.testutils.lambda.lambdaRecorder
import com.zenobia.app.tests.testutils.lambda.value
import com.zenobia.app.tests.testutils.robolectric.RobolectricTest
import com.zenobia.app.tests.testutils.testCoroutineDispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.Test

class DefaultClearCacheUseCaseTest : RobolectricTest() {
    @Test
    fun `execute clear cache should do all the expected tasks`() = runTest {
        val activeRoomsHolder = DefaultActiveRoomsHolder().apply { addRoom(FakeJoinedRoom()) }
        val clearCacheLambda = lambdaRecorder<Unit> { }
        val matrixClient = FakeMatrixClient(
            sessionId = A_SESSION_ID,
            clearCacheLambda = clearCacheLambda,
        )
        val defaultCacheService = DefaultCacheService()
        val setIgnoreRegistrationErrorLambda = lambdaRecorder<SessionId, Boolean, Unit> { _, _ -> }
        val resetBatteryOptimizationStateResult = lambdaRecorder<Unit> { }
        val pushService = FakePushService(
            setIgnoreRegistrationErrorLambda = setIgnoreRegistrationErrorLambda,
            resetBatteryOptimizationStateResult = resetBatteryOptimizationStateResult,
        )
        val seenInvitesStore = InMemorySeenInvitesStore(setOf(A_ROOM_ID))
        assertThat(seenInvitesStore.seenRoomIds().first()).isNotEmpty()
        val cacheStore = InMemoryCacheStore(
            initialData = mapOf("key1" to aCacheData())
        )
        val sut = DefaultClearCacheUseCase(
            context = InstrumentationRegistry.getInstrumentation().context,
            matrixClient = matrixClient,
            coroutineDispatchers = testCoroutineDispatchers(),
            defaultCacheService = defaultCacheService,
            okHttpClient = { OkHttpClient.Builder().build() },
            pushService = pushService,
            seenInvitesStore = seenInvitesStore,
            activeRoomsHolder = activeRoomsHolder,
            cacheStore = cacheStore,
        )
        defaultCacheService.clearedCacheEventFlow.test {
            sut.invoke()
            assertThat(cacheStore.dataMap).isEmpty()
            clearCacheLambda.assertions().isCalledOnce()
            setIgnoreRegistrationErrorLambda.assertions().isCalledOnce()
                .with(value(matrixClient.sessionId), value(false))
            resetBatteryOptimizationStateResult.assertions().isCalledOnce()
            assertThat(awaitItem()).isEqualTo(matrixClient.sessionId)
            assertThat(seenInvitesStore.seenRoomIds().first()).isEmpty()
            assertThat(activeRoomsHolder.getActiveRoom(A_SESSION_ID)).isNull()
        }
    }
}
