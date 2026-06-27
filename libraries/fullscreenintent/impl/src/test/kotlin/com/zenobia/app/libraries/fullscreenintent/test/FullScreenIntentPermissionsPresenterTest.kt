/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.fullscreenintent.test

import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.zenobia.app.libraries.core.meta.BuildMeta
import com.zenobia.app.libraries.fullscreenintent.api.FullScreenIntentPermissionsEvents
import com.zenobia.app.libraries.fullscreenintent.impl.FullScreenIntentPermissionsPresenter
import com.zenobia.app.libraries.matrix.test.core.aBuildMeta
import com.zenobia.app.libraries.preferences.test.FakePreferenceDataStoreFactory
import com.zenobia.app.services.toolbox.api.intent.ExternalIntentLauncher
import com.zenobia.app.services.toolbox.test.intent.FakeExternalIntentLauncher
import com.zenobia.app.services.toolbox.test.sdk.FakeBuildVersionSdkIntProvider
import com.zenobia.app.tests.testutils.WarmUpRule
import com.zenobia.app.tests.testutils.lambda.lambdaRecorder
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FullScreenIntentPermissionsPresenterTest {
    @get:Rule
    val warmUpRule = WarmUpRule()

    @Test
    fun `shouldDisplay - is true when permission is not granted and banner is not dismissed`() = runTest {
        val presenter = createPresenter(
            notificationManagerCompat = mockk {
                every { canUseFullScreenIntent() } returns false
            }
        )
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            skipItems(1)
            val initialItem = awaitItem()
            assertThat(initialItem.shouldDisplayBanner).isTrue()
        }
    }

    @Test
    fun `shouldDisplay - is false if permission is granted`() = runTest {
        val presenter = createPresenter(
            notificationManagerCompat = mockk {
                every { canUseFullScreenIntent() } returns true
            }
        )
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            skipItems(1)
            val initialItem = awaitItem()
            assertThat(initialItem.shouldDisplayBanner).isFalse()
        }
    }

    @Test
    fun `dismissFullScreenIntentBanner - makes shouldDisplay false`() = runTest {
        val presenter = createPresenter()
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            skipItems(1)
            val loadedItem = awaitItem()
            loadedItem.eventSink(FullScreenIntentPermissionsEvents.Dismiss)
            runCurrent()
            assertThat(awaitItem().shouldDisplayBanner).isFalse()
        }
    }

    @Test
    fun `openFullScreenIntentSettings - opens external screen using intent`() = runTest {
        val launchLambda = lambdaRecorder<Intent, Unit> { _ -> }
        val externalIntentLauncher = FakeExternalIntentLauncher(launchLambda)
        val presenter = createPresenter(externalIntentLauncher = externalIntentLauncher)
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            skipItems(1)
            val loadedItem = awaitItem()
            loadedItem.eventSink(FullScreenIntentPermissionsEvents.OpenSettings)
            launchLambda.assertions().isCalledOnce()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `openFullScreenIntentSettings - does nothing in old APIs`() = runTest {
        val launchLambda = lambdaRecorder<Intent, Unit> { _ -> }
        val externalIntentLauncher = FakeExternalIntentLauncher(launchLambda)
        val presenter = createPresenter(
            buildVersionSdkIntProvider = FakeBuildVersionSdkIntProvider(Build.VERSION_CODES.Q),
            externalIntentLauncher = externalIntentLauncher,
        )
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            skipItems(1)
            val loadedItem = awaitItem()
            loadedItem.eventSink(FullScreenIntentPermissionsEvents.OpenSettings)
            launchLambda.assertions().isNeverCalled()
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createPresenter(
        buildVersionSdkIntProvider: FakeBuildVersionSdkIntProvider = FakeBuildVersionSdkIntProvider(Build.VERSION_CODES.UPSIDE_DOWN_CAKE),
        dataStoreFactory: FakePreferenceDataStoreFactory = FakePreferenceDataStoreFactory(),
        externalIntentLauncher: ExternalIntentLauncher = FakeExternalIntentLauncher(),
        buildMeta: BuildMeta = aBuildMeta(),
        notificationManagerCompat: NotificationManagerCompat = mockk(relaxed = true)
    ) = FullScreenIntentPermissionsPresenter(
        buildVersionSdkIntProvider = buildVersionSdkIntProvider,
        externalIntentLauncher = externalIntentLauncher,
        buildMeta = buildMeta,
        preferencesDataStoreFactory = dataStoreFactory,
        notificationManagerCompat = notificationManagerCompat,
    )
}
