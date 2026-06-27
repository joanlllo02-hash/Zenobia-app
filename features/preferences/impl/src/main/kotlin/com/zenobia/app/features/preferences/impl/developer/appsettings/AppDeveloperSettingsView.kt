/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.preferences.impl.developer.appsettings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.features.preferences.impl.R
import com.zenobia.app.features.preferences.impl.developer.tracing.LogLevelItem
import com.zenobia.app.features.rageshake.api.preferences.RageshakePreferencesView
import com.zenobia.app.libraries.designsystem.components.preferences.PreferenceCategory
import com.zenobia.app.libraries.designsystem.components.preferences.PreferenceDropdown
import com.zenobia.app.libraries.designsystem.components.preferences.PreferenceSwitch
import com.zenobia.app.libraries.designsystem.components.preferences.PreferenceTextField
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.ListItem
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.featureflag.ui.FeatureListView
import com.zenobia.app.libraries.featureflag.ui.model.FeatureUiModel
import com.zenobia.app.libraries.matrix.api.tracing.TraceLogPack
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppDeveloperSettingsView(
    state: AppDeveloperSettingsState,
    onOpenShowkase: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        // Note: this is OK to hardcode strings in this debug screen.
        PreferenceCategory(
            title = "Feature flags",
            showTopDivider = false,
        ) {
            FeatureListContent(state)
        }
        ElementCallCategory(state = state)
        PreferenceCategory(title = "Rust SDK") {
            PreferenceDropdown(
                title = "Tracing log level",
                supportingText = "Requires app reboot",
                selectedOption = state.tracingLogLevel.dataOrNull(),
                options = LogLevelItem.entries.toImmutableList(),
                onSelectOption = { logLevel ->
                    state.eventSink(AppDeveloperSettingsEvent.SetTracingLogLevel(logLevel))
                }
            )
        }
        PreferenceCategory(title = "Enable trace logs per SDK feature") {
            Text(
                text = "Requires app reboot",
                style = ZenobiaTheme.typography.fontBodyMdRegular,
                color = ZenobiaTheme.colors.textSecondary,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            )
            for (logPack in TraceLogPack.entries) {
                PreferenceSwitch(
                    title = logPack.title,
                    isChecked = state.tracingLogPacks.contains(logPack),
                    onCheckedChange = { isChecked -> state.eventSink(AppDeveloperSettingsEvent.ToggleTracingLogPack(logPack, isChecked)) }
                )
            }
        }
        PreferenceCategory(title = "Showkase") {
            ListItem(
                headlineContent = {
                    Text("Open Showkase browser")
                },
                onClick = onOpenShowkase
            )
        }
        RageshakePreferencesView(
            state = state.rageshakeState,
        )
        PreferenceCategory(title = "Crash") {
            ListItem(
                headlineContent = {
                    Text("Crash the app 💥")
                },
                onClick = { error("This crash is a test.") }
            )
        }
    }
}

@Composable
private fun ElementCallCategory(
    state: AppDeveloperSettingsState,
) {
    PreferenceCategory(title = "Element Call") {
        val callUrlState = state.customElementCallBaseUrlState

        val supportingText = if (callUrlState.baseUrl.isNullOrEmpty()) {
            stringResource(R.string.screen_advanced_settings_element_call_base_url_description)
        } else {
            callUrlState.baseUrl
        }
        PreferenceTextField(
            headline = stringResource(R.string.screen_advanced_settings_element_call_base_url),
            value = callUrlState.baseUrl,
            placeholder = "https://.../room",
            supportingText = supportingText,
            validation = callUrlState.validator,
            onValidationErrorMessage = stringResource(R.string.screen_advanced_settings_element_call_base_url_validation_error),
            displayValue = { value -> !value.isNullOrEmpty() },
            keyboardOptions = KeyboardOptions.Default.copy(autoCorrectEnabled = false, keyboardType = KeyboardType.Uri),
            onChange = { state.eventSink(AppDeveloperSettingsEvent.SetCustomElementCallBaseUrl(it)) }
        )
    }
}

@Composable
private fun FeatureListContent(
    state: AppDeveloperSettingsState,
) {
    fun onFeatureEnabled(feature: FeatureUiModel, isEnabled: Boolean) {
        state.eventSink(AppDeveloperSettingsEvent.UpdateEnabledFeature(feature, isEnabled))
    }

    FeatureListView(
        features = state.features,
        onCheckedChange = ::onFeatureEnabled,
    )
}

@PreviewsDayNight
@Composable
internal fun AppDeveloperSettingsViewPreview(
    @PreviewParameter(AppDeveloperSettingsStateProvider::class) state: AppDeveloperSettingsState
) = ZenobiaPreview {
    AppDeveloperSettingsView(
        state = state,
        onOpenShowkase = {},
    )
}
