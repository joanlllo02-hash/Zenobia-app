/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.analytics.impl

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.zenobia.app.features.analytics.api.AnalyticsOptInEvents
import com.zenobia.app.libraries.matrix.test.core.aBuildMeta
import com.zenobia.app.services.analytics.test.FakeAnalyticsService
import com.zenobia.app.tests.testutils.WarmUpRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class AnalyticsOptInPresenterTest {
    @get:Rule
    val warmUpRule = WarmUpRule()

    @Test
    fun `present - enable`() = runTest {
        val analyticsService = FakeAnalyticsService(isEnabled = false)
        val presenter = AnalyticsOptInPresenter(
            aBuildMeta(),
            analyticsService
        )
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitItem()
            assertThat(analyticsService.didAskUserConsentFlow.first()).isFalse()
            initialState.eventSink.invoke(AnalyticsOptInEvents.EnableAnalytics(true))
            assertThat(analyticsService.didAskUserConsentFlow.first()).isTrue()
            assertThat(analyticsService.userConsentFlow.first()).isTrue()
        }
    }

    @Test
    fun `present - not now`() = runTest {
        val analyticsService = FakeAnalyticsService(isEnabled = false)
        val presenter = AnalyticsOptInPresenter(
            aBuildMeta(),
            analyticsService
        )
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitItem()
            assertThat(analyticsService.didAskUserConsentFlow.first()).isFalse()
            initialState.eventSink.invoke(AnalyticsOptInEvents.EnableAnalytics(false))
            assertThat(analyticsService.didAskUserConsentFlow.first()).isTrue()
            assertThat(analyticsService.userConsentFlow.first()).isFalse()
        }
    }
}
