/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.preferences.impl.analytics

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.zenobia.app.features.analytics.api.preferences.AnalyticsPreferencesView
import com.zenobia.app.libraries.designsystem.components.preferences.PreferencePage
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun AnalyticsSettingsView(
    state: AnalyticsSettingsState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PreferencePage(
        modifier = modifier,
        onBackClick = onBackClick,
        title = stringResource(id = CommonStrings.common_analytics)
    ) {
        AnalyticsPreferencesView(
            state = state.analyticsPreferencesState,
        )
    }
}

@PreviewsDayNight
@Composable
internal fun AnalyticsSettingsViewPreview(@PreviewParameter(AnalyticsSettingsStateProvider::class) state: AnalyticsSettingsState) = ZenobiaPreview {
    AnalyticsSettingsView(
        state = state,
        onBackClick = {},
    )
}
