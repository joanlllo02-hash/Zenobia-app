/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.analytics.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Inject
import com.zenobia.app.appconfig.AnalyticsConfig
import com.zenobia.app.features.analytics.api.AnalyticsOptInEvents
import com.zenobia.app.libraries.architecture.Presenter
import com.zenobia.app.libraries.core.meta.BuildMeta
import com.zenobia.app.services.analytics.api.AnalyticsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Inject
class AnalyticsOptInPresenter(
    private val buildMeta: BuildMeta,
    private val analyticsService: AnalyticsService,
) : Presenter<AnalyticsOptInState> {
    @Composable
    override fun present(): AnalyticsOptInState {
        val localCoroutineScope = rememberCoroutineScope()

        fun handleEvent(event: AnalyticsOptInEvents) {
            when (event) {
                is AnalyticsOptInEvents.EnableAnalytics -> localCoroutineScope.setIsEnabled(event.isEnabled)
            }
            localCoroutineScope.launch {
                analyticsService.setDidAskUserConsent()
            }
        }

        return AnalyticsOptInState(
            applicationName = buildMeta.applicationName,
            hasPolicyLink = AnalyticsConfig.POLICY_LINK.isNotEmpty(),
            eventSink = ::handleEvent,
        )
    }

    private fun CoroutineScope.setIsEnabled(enabled: Boolean) = launch {
        analyticsService.setUserConsent(enabled)
    }
}
