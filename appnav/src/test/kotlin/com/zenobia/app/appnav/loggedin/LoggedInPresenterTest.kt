/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.zenobia.app.appnav.loggedin

import app.cash.turbine.ReceiveTurbine
import com.google.common.truth.Truth.assertThat
import im.vector.app.features.analytics.plan.CryptoSessionStateChange
import im.vector.app.features.analytics.plan.UserProperties
import com.zenobia.app.features.networkmonitor.api.NetworkStatus
import com.zenobia.app.features.networkmonitor.test.FakeNetworkMonitor
import com.zenobia.app.libraries.core.meta.BuildMeta
import com.zenobia.app.libraries.matrix.api.MatrixClient
import com.zenobia.app.libraries.matrix.api.core.SessionId
import com.zenobia.app.libraries.matrix.api.encryption.EncryptionService
import com.zenobia.app.libraries.matrix.api.encryption.RecoveryState
import com.zenobia.app.libraries.matrix.api.oauth.AccountManagementAction
import com.zenobia.app.libraries.matrix.api.roomlist.RoomListService
import com.zenobia.app.libraries.matrix.api.sync.SlidingSyncVersion
import com.zenobia.app.libraries.matrix.api.sync.SyncState
import com.zenobia.app.libraries.matrix.api.verification.SessionVerificationService
import com.zenobia.app.libraries.matrix.api.verification.SessionVerifiedStatus
import com.zenobia.app.libraries.matrix.test.AN_EXCEPTION
import com.zenobia.app.libraries.matrix.test.A_SESSION_ID
import com.zenobia.app.libraries.matrix.test.FakeHomeserverCapabilitiesProvider
import com.zenobia.app.libraries.matrix.test.FakeMatrixClient
import com.zenobia.app.libraries.matrix.test.core.aBuildMeta
import com.zenobia.app.libraries.matrix.test.encryption.FakeEncryptionService
import com.zenobia.app.libraries.matrix.test.roomlist.FakeRoomListService
import com.zenobia.app.libraries.matrix.test.sync.FakeSyncService
import com.zenobia.app.libraries.matrix.test.verification.FakeSessionVerificationService
import com.zenobia.app.libraries.push.api.PushService
import com.zenobia.app.libraries.push.api.PusherRegistrationFailure
import com.zenobia.app.libraries.push.test.FakePushService
import com.zenobia.app.libraries.pushproviders.api.Distributor
import com.zenobia.app.libraries.pushproviders.api.PushProvider
import com.zenobia.app.libraries.pushproviders.test.FakePushProvider
import com.zenobia.app.services.analytics.api.AnalyticsService
import com.zenobia.app.services.analytics.test.FakeAnalyticsService
import com.zenobia.app.tests.testutils.WarmUpRule
import com.zenobia.app.tests.testutils.consumeItemsUntilPredicate
import com.zenobia.app.tests.testutils.lambda.lambdaError
import com.zenobia.app.tests.testutils.lambda.lambdaRecorder
import com.zenobia.app.tests.testutils.lambda.value
import com.zenobia.app.tests.testutils.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class LoggedInPresenterTest {
    @get:Rule
    val warmUpRule = WarmUpRule()

    @Test
    fun `present - initial state`() = runTest {
        createLoggedInPresenter().test {
            val initialState = awaitItem()
            assertThat(initialState.showSyncSpinner).isFalse()
            assertThat(initialState.pusherRegistrationState.isUninitialized()).isTrue()
            assertThat(initialState.ignoreRegistrationError).isFalse()
        }
    }

    @Test
    fun `present - ensure that account url is preloaded`() = runTest {
        val accountManagementUrlResult = lambdaRecorder<AccountManagementAction?, Result<String?>> { Result.success("aUrl") }
        val matrixClient = FakeMatrixClient(
            accountManagementUrlResult = accountManagementUrlResult,
        )
        createLoggedInPresenter(
            matrixClient = matrixClient,
        ).test {
            awaitItem()
            advanceUntilIdle()
            accountManagementUrlResult.assertions().isCalledOnce()
                .with(value(null))
        }
    }

    @Test
    fun `present - show sync spinner`() = runTest {
        val roomListService = FakeRoomListService()
        createLoggedInPresenter(
            syncState = SyncState.Running,
            matrixClient = FakeMatrixClient(roomListService = roomListService),
        ).test {
            val initialState = awaitItem()
            assertThat(initialState.showSyncSpinner).isFalse()
            roomListService.postSyncIndicator(RoomListService.SyncIndicator.Show)
            consumeItemsUntilPredicate { it.showSyncSpinner }
            roomListService.postSyncIndicator(RoomListService.SyncIndicator.Hide)
            consumeItemsUntilPredicate { !it.showSyncSpinner }
        }
    }

    @Test
    fun `present - report crypto status analytics`() = runTest {
        val analyticsService = FakeAnalyticsService()
        val roomListService = FakeRoomListService()
        val verificationService = FakeSessionVerificationService()
        val encryptionService = FakeEncryptionService()
        val buildMeta = aBuildMeta()
        val networkMonitor = FakeNetworkMonitor()
        LoggedInPresenter(
            matrixClient = FakeMatrixClient(
                roomListService = roomListService,
                encryptionService = encryptionService,
            ),
            syncService = FakeSyncService(initialSyncState = SyncState.Running),
            pushService = FakePushService(
                ensurePusherIsRegisteredResult = { Result.success(Unit) },
            ),
            sessionVerificationService = verificationService,
            analyticsService = analyticsService,
            encryptionService = encryptionService,
            buildMeta = buildMeta,
            networkMonitor = networkMonitor,
        ).test {
            encryptionService.emitRecoveryState(RecoveryState.UNKNOWN)
            encryptionService.emitRecoveryState(RecoveryState.INCOMPLETE)
            verificationService.emitVerifiedStatus(SessionVerifiedStatus.Verified)
            skipItems(2)
            assertThat(analyticsService.capturedEvents.size).isEqualTo(1)
            assertThat(analyticsService.capturedEvents[0]).isInstanceOf(CryptoSessionStateChange::class.java)
            assertThat(analyticsService.capturedUserProperties.size).isEqualTo(1)
            assertThat(analyticsService.capturedUserProperties[0].recoveryState).isEqualTo(UserProperties.RecoveryState.Incomplete)
            assertThat(analyticsService.capturedUserProperties[0].verificationState).isEqualTo(UserProperties.VerificationState.Verified)
            // ensure a sync status change does not trigger a new capture
            roomListService.postSyncIndicator(RoomListService.SyncIndicator.Show)
            skipItems(1)
            assertThat(analyticsService.capturedEvents.size).isEqualTo(1)
        }
    }

    @Test
    fun `present - ensure default pusher is not registered if session is not verified`() = runTest {
        val lambda = lambdaRecorder<Result<Unit>> {
            Result.success(Unit)
        }
        val pushService = createFakePushService(ensurePusherIsRegisteredResult = lambda)
        val verificationService = FakeSessionVerificationService(
            initialSessionVerifiedStatus = SessionVerifiedStatus.NotVerified
        )
        createLoggedInPresenter(
            pushService = pushService,
            sessionVerificationService = verificationService,
        ).test {
            val finalState = awaitFirstItem()
            assertThat(finalState.pusherRegistrationState.errorOrNull())
                .isInstanceOf(PusherRegistrationFailure.AccountNotVerified::class.java)
            lambda.assertions().isNeverCalled()
        }
    }

    @Test
    fun `present - ensure default pusher is registered with default provider`() = runTest {
        val lambda = lambdaRecorder<Result<Unit>> { Result.success(Unit) }
        val sessionVerificationService = FakeSessionVerificationService(
            initialSessionVerifiedStatus = SessionVerifiedStatus.Verified
        )
        val pushService = createFakePushService(
            ensurePusherIsRegisteredResult = lambda,
        )
        createLoggedInPresenter(
            pushService = pushService,
            sessionVerificationService = sessionVerificationService,
            matrixClient = FakeMatrixClient(
                accountManagementUrlResult = { Result.success(null) },
            ),
        ).test {
            val finalState = awaitFirstItem()
            assertThat(finalState.pusherRegistrationState.isSuccess()).isTrue()
            lambda.assertions()
                .isCalledOnce()
        }
    }

    @Test
    fun `present - ensure default pusher is registered with default provider - fail to register`() = runTest {
        val lambda = lambdaRecorder<Result<Unit>> { Result.failure(AN_EXCEPTION) }
        val sessionVerificationService = FakeSessionVerificationService(
            initialSessionVerifiedStatus = SessionVerifiedStatus.Verified
        )
        val pushService = createFakePushService(
            ensurePusherIsRegisteredResult = lambda,
        )
        createLoggedInPresenter(
            pushService = pushService,
            sessionVerificationService = sessionVerificationService,
            matrixClient = FakeMatrixClient(
                accountManagementUrlResult = { Result.success(null) },
            ),
        ).test {
            val finalState = awaitFirstItem()
            assertThat(finalState.pusherRegistrationState.isFailure()).isTrue()
            lambda.assertions()
                .isCalledOnce()
            // Reset the error and do not show again
            finalState.eventSink(LoggedInEvents.CloseErrorDialog(doNotShowAgain = false))
            val lastState = awaitItem()
            assertThat(lastState.pusherRegistrationState.isUninitialized()).isTrue()
            assertThat(lastState.ignoreRegistrationError).isFalse()
        }
    }

    @Test
    fun `present - ensure default pusher is registered with default provider - fail to register - do not show again`() = runTest {
        val lambda = lambdaRecorder<Result<Unit>> { Result.failure(AN_EXCEPTION) }
        val setIgnoreRegistrationErrorLambda = lambdaRecorder<SessionId, Boolean, Unit> { _, _ -> }
        val sessionVerificationService = FakeSessionVerificationService(
            initialSessionVerifiedStatus = SessionVerifiedStatus.Verified
        )
        val pushService = createFakePushService(
            ensurePusherIsRegisteredResult = lambda,
            setIgnoreRegistrationErrorLambda = setIgnoreRegistrationErrorLambda,
        )
        createLoggedInPresenter(
            pushService = pushService,
            sessionVerificationService = sessionVerificationService,
            matrixClient = FakeMatrixClient(
                accountManagementUrlResult = { Result.success(null) },
            ),
        ).test {
            val finalState = awaitFirstItem()
            assertThat(finalState.pusherRegistrationState.isFailure()).isTrue()
            lambda.assertions()
                .isCalledOnce()
            // Reset the error and do not show again
            finalState.eventSink(LoggedInEvents.CloseErrorDialog(doNotShowAgain = true))
            skipItems(1)
            setIgnoreRegistrationErrorLambda.assertions()
                .isCalledOnce()
                .with(
                    // SessionId
                    value(A_SESSION_ID),
                    // Ignore
                    value(true),
                )
            val lastState = awaitItem()
            assertThat(lastState.pusherRegistrationState.isUninitialized()).isTrue()
            assertThat(lastState.ignoreRegistrationError).isTrue()
        }
    }

    private fun createFakePushService(
        pushProvider0: PushProvider? = FakePushProvider(
            index = 0,
            name = "aFakePushProvider0",
            distributors = listOf(Distributor("aDistributorValue0", "aDistributorName0")),
            currentDistributor = { null },
        ),
        pushProvider1: PushProvider? = FakePushProvider(
            index = 1,
            name = "aFakePushProvider1",
            distributors = listOf(Distributor("aDistributorValue1", "aDistributorName1")),
            currentDistributor = { null },
        ),
        ensurePusherIsRegisteredResult: () -> Result<Unit> = {
            Result.success(Unit)
        },
        selectPushProviderLambda: (SessionId, PushProvider) -> Unit = { _, _ -> lambdaError() },
        currentPushProvider: (SessionId) -> PushProvider? = { null },
        setIgnoreRegistrationErrorLambda: (SessionId, Boolean) -> Unit = { _, _ -> lambdaError() },
    ): PushService {
        return FakePushService(
            availablePushProviders = listOfNotNull(pushProvider0, pushProvider1),
            ensurePusherIsRegisteredResult = ensurePusherIsRegisteredResult,
            currentPushProvider = currentPushProvider,
            selectPushProviderLambda = selectPushProviderLambda,
            setIgnoreRegistrationErrorLambda = setIgnoreRegistrationErrorLambda,
        )
    }

    @Test
    fun `present - CheckSlidingSyncProxyAvailability forces the sliding sync migration under the right circumstances`() = runTest {
        // The migration will be forced if the user is not using the native sliding sync
        val matrixClient = FakeMatrixClient(
            currentSlidingSyncVersionLambda = { Result.success(SlidingSyncVersion.Proxy) },
        )
        createLoggedInPresenter(
            matrixClient = matrixClient,
        ).test {
            val initialState = awaitItem()
            assertThat(initialState.forceNativeSlidingSyncMigration).isFalse()
            initialState.eventSink(LoggedInEvents.CheckSlidingSyncProxyAvailability)
            assertThat(awaitItem().forceNativeSlidingSyncMigration).isTrue()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `present - LogoutAndMigrateToNativeSlidingSync logs out the user`() = runTest {
        val logoutLambda = lambdaRecorder<Boolean, Boolean, Unit> { userInitiated, ignoreSdkError ->
            assertThat(userInitiated).isTrue()
            assertThat(ignoreSdkError).isTrue()
        }
        val matrixClient = FakeMatrixClient(
            accountManagementUrlResult = { Result.success(null) },
        ).apply {
            this.logoutLambda = logoutLambda
        }
        createLoggedInPresenter(
            matrixClient = matrixClient,
        ).test {
            val initialState = awaitItem()

            initialState.eventSink(LoggedInEvents.LogoutAndMigrateToNativeSlidingSync)

            advanceUntilIdle()

            assertThat(logoutLambda.assertions().isCalledOnce())
        }
    }

    @Test
    fun `present - refreshes homeserver capabilities when network is back`() = runTest {
        val refreshLambda = lambdaRecorder<Result<Unit>> { Result.success(Unit) }
        val matrixClient = FakeMatrixClient(
            homeserverCapabilitiesProvider = FakeHomeserverCapabilitiesProvider(refresh = refreshLambda),
            accountManagementUrlResult = { Result.success(null) },
        )
        val networkMonitor = FakeNetworkMonitor()
        createLoggedInPresenter(
            matrixClient = matrixClient,
            networkMonitor = networkMonitor,
        ).test {
            awaitItem()
            networkMonitor.connectivity.value = NetworkStatus.Connected

            advanceUntilIdle()

            refreshLambda.assertions().isCalledOnce()
        }
    }

    private suspend fun <T> ReceiveTurbine<T>.awaitFirstItem(): T {
        skipItems(1)
        return awaitItem()
    }

    private fun createLoggedInPresenter(
        syncState: SyncState = SyncState.Running,
        analyticsService: AnalyticsService = FakeAnalyticsService(),
        sessionVerificationService: SessionVerificationService = FakeSessionVerificationService(),
        encryptionService: EncryptionService = FakeEncryptionService(),
        pushService: PushService = FakePushService(),
        matrixClient: MatrixClient = FakeMatrixClient(
            accountManagementUrlResult = { Result.success(null) },
        ),
        buildMeta: BuildMeta = aBuildMeta(),
        networkMonitor: FakeNetworkMonitor = FakeNetworkMonitor(),
    ): LoggedInPresenter {
        return LoggedInPresenter(
            matrixClient = matrixClient,
            syncService = FakeSyncService(initialSyncState = syncState),
            pushService = pushService,
            sessionVerificationService = sessionVerificationService,
            analyticsService = analyticsService,
            encryptionService = encryptionService,
            buildMeta = buildMeta,
            networkMonitor = networkMonitor,
        )
    }
}
