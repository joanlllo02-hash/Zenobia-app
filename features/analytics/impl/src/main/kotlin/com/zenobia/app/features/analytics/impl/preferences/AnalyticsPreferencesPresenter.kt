/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.analytics.impl.preferences

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Inject
import com.zenobia.app.appconfig.AnalyticsConfig
import com.zenobia.app.features.analytics.api.AnalyticsOptInEvents
import com.zenobia.app.features.analytics.api.preferences.AnalyticsPreferencesState
import com.zenobia.app.libraries.architecture.Presenter
import com.zenobia.app.libraries.core.meta.BuildMeta
import com.zenobia.app.services.analytics.api.AnalyticsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Inject
class AnalyticsPreferencesPresenter(
    private val analyticsService: AnalyticsService,
    private val buildMeta: BuildMeta,
) : Presenter<AnalyticsPreferencesState> {
    @Composable
    override fun present(): AnalyticsPreferencesState {
        val localCoroutineScope = rememberCoroutineScope()
        val isEnabled = analyticsService.userConsentFlow.collectAsState(initial = false)

        fun handleEvent(event: AnalyticsOptInEvents) {
            when (event) {
                is AnalyticsOptInEvents.EnableAnalytics -> localCoroutineScope.setIsEnabled(event.isEnabled)
            }
        }

        return AnalyticsPreferencesState(
            applicationName = buildMeta.applicationName,
            isEnabled = isEnabled.value,
            policyUrl = AnalyticsConfig.POLICY_LINK,
            eventSink = ::handleEvent,
        )
    }

    private fun CoroutineScope.setIsEnabled(enabled: Boolean) = launch {
        analyticsService.setUserConsent(enabled)
    }
}
