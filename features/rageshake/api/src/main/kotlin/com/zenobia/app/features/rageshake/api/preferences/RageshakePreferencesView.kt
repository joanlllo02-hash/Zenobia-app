/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.rageshake.api.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.zenobia.app.features.rageshake.api.R
import com.zenobia.app.libraries.designsystem.components.preferences.PreferenceCategory
import com.zenobia.app.libraries.designsystem.components.preferences.PreferenceSlide
import com.zenobia.app.libraries.designsystem.components.preferences.PreferenceSwitch
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.ListItem
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun RageshakePreferencesView(
    state: RageshakePreferencesState,
    modifier: Modifier = Modifier,
) {
    fun onSensitivityChanged(sensitivity: Float) {
        state.eventSink(RageshakePreferencesEvent.SetSensitivity(sensitivity = sensitivity))
    }

    fun onEnabledChanged(isEnabled: Boolean) {
        state.eventSink(RageshakePreferencesEvent.SetIsEnabled(isEnabled = isEnabled))
    }

    Column(modifier = modifier) {
        if (state.isFeatureEnabled) {
            PreferenceCategory(title = stringResource(id = R.string.settings_rageshake)) {
                if (state.isSupported) {
                    PreferenceSwitch(
                        title = stringResource(id = CommonStrings.preference_rageshake),
                        isChecked = state.isEnabled,
                        onCheckedChange = ::onEnabledChanged
                    )
                    PreferenceSlide(
                        title = stringResource(id = R.string.settings_rageshake_detection_threshold),
                        // summary = stringResource(id = CommonStrings.settings_rageshake_detection_threshold_summary),
                        value = state.sensitivity,
                        enabled = state.isEnabled,
                        // 5 possible values - steps are in ]0, 1[
                        steps = 3,
                        onValueChange = ::onSensitivityChanged
                    )
                } else {
                    ListItem(
                        headlineContent = {
                            Text("Rageshaking is not supported by your device")
                        },
                    )
                }
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun RageshakePreferencesViewPreview(@PreviewParameter(RageshakePreferencesStateProvider::class) state: RageshakePreferencesState) = ZenobiaPreview {
    RageshakePreferencesView(state)
}
