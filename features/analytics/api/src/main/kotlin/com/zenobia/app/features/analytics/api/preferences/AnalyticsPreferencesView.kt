/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.analytics.api.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.zenobia.app.features.analytics.api.AnalyticsOptInEvents
import com.zenobia.app.features.analytics.api.R
import com.zenobia.app.libraries.designsystem.components.LINK_TAG
import com.zenobia.app.libraries.designsystem.components.list.ListItemContent
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.text.buildAnnotatedStringWithStyledPart
import com.zenobia.app.libraries.designsystem.theme.components.ListItem
import com.zenobia.app.libraries.designsystem.theme.components.ListSupportingText
import com.zenobia.app.libraries.designsystem.theme.components.Text

@Composable
fun AnalyticsPreferencesView(
    state: AnalyticsPreferencesState,
    modifier: Modifier = Modifier,
) {
    fun onEnabledChanged(isEnabled: Boolean) {
        state.eventSink(AnalyticsOptInEvents.EnableAnalytics(isEnabled = isEnabled))
    }

    val supportingText = stringResource(
        id = R.string.screen_analytics_settings_help_us_improve,
        state.applicationName
    )
    Column(modifier) {
        ListItem(
            headlineContent = {
                Text(stringResource(id = R.string.screen_analytics_settings_share_data))
            },
            supportingContent = {
                Text(supportingText)
            },
            leadingContent = null,
            trailingContent = ListItemContent.Switch(
                checked = state.isEnabled,
            ),
            onClick = {
                onEnabledChanged(!state.isEnabled)
            }
        )
        if (state.policyUrl.isNotEmpty()) {
            val linkText = buildAnnotatedStringWithStyledPart(
                R.string.screen_analytics_settings_read_terms,
                R.string.screen_analytics_settings_read_terms_content_link,
                tagAndLink = LINK_TAG to state.policyUrl,
            )
            ListSupportingText(annotatedString = linkText)
        }
    }
}

@PreviewsDayNight
@Composable
internal fun AnalyticsPreferencesViewPreview(@PreviewParameter(AnalyticsPreferencesStateProvider::class) state: AnalyticsPreferencesState) =
    ZenobiaPreview {
        AnalyticsPreferencesView(
            state = state,
        )
    }
