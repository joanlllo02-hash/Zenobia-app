/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.impl

import com.google.common.truth.Truth.assertThat
import com.zenobia.app.libraries.featureflag.test.FakeFeatureFlagService
import com.zenobia.app.libraries.matrix.api.core.SessionId
import com.zenobia.app.libraries.matrix.impl.auth.FakeProxyProvider
import com.zenobia.app.libraries.matrix.impl.room.FakeTimelineEventFilterFactory
import com.zenobia.app.libraries.matrix.impl.storage.FakeSqliteStoreBuilderProvider
import com.zenobia.app.libraries.network.useragent.SimpleUserAgentProvider
import com.zenobia.app.libraries.sessionstorage.api.SessionStore
import com.zenobia.app.libraries.sessionstorage.test.InMemorySessionStore
import com.zenobia.app.libraries.sessionstorage.test.aSessionData
import com.zenobia.app.libraries.workmanager.api.WorkManagerRequestBuilder
import com.zenobia.app.libraries.workmanager.test.FakeWorkManagerScheduler
import com.zenobia.app.services.analytics.test.FakeAnalyticsService
import com.zenobia.app.services.toolbox.test.systemclock.FakeSystemClock
import com.zenobia.app.tests.testutils.lambda.lambdaRecorder
import com.zenobia.app.tests.testutils.testCoroutineDispatchers
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.File

class RustMatrixClientFactoryTest {
    @Test
    fun test() = runTest {
        val scheduleVacuumLambda = lambdaRecorder<WorkManagerRequestBuilder, Unit> {}
        val workManagerScheduler = FakeWorkManagerScheduler(submitLambda = scheduleVacuumLambda)
        val sut = createRustMatrixClientFactory(workManagerScheduler = workManagerScheduler)

        val result = sut.create(aSessionData())

        assertThat(result.sessionId).isEqualTo(SessionId("@alice:server.org"))
        scheduleVacuumLambda.assertions().isCalledOnce()
        result.destroy()
    }
}

fun TestScope.createRustMatrixClientFactory(
    cacheDirectory: File = File("/cache"),
    sessionStore: SessionStore = InMemorySessionStore(
        updateUserProfileResult = { _, _, _ -> },
    ),
    clientBuilderProvider: ClientBuilderProvider = FakeClientBuilderProvider(),
    workManagerScheduler: FakeWorkManagerScheduler = FakeWorkManagerScheduler(),
) = RustMatrixClientFactory(
    cacheDirectory = cacheDirectory,
    appCoroutineScope = backgroundScope,
    coroutineDispatchers = testCoroutineDispatchers(),
    sessionStore = sessionStore,
    userAgentProvider = SimpleUserAgentProvider(),
    proxyProvider = FakeProxyProvider(),
    clock = FakeSystemClock(),
    analyticsService = FakeAnalyticsService(),
    featureFlagService = FakeFeatureFlagService(),
    timelineEventFilterFactory = FakeTimelineEventFilterFactory(),
    clientBuilderProvider = clientBuilderProvider,
    sqliteStoreBuilderProvider = FakeSqliteStoreBuilderProvider(),
    workManagerScheduler = workManagerScheduler,
)
