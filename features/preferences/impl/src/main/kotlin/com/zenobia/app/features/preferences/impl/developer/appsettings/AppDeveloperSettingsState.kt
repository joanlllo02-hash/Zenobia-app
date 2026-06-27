/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.preferences.impl.developer.appsettings

import com.zenobia.app.features.preferences.impl.developer.tracing.LogLevelItem
import com.zenobia.app.features.rageshake.api.preferences.RageshakePreferencesState
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.featureflag.ui.model.FeatureUiModel
import com.zenobia.app.libraries.matrix.api.tracing.TraceLogPack
import kotlinx.collections.immutable.ImmutableList

data class AppDeveloperSettingsState(
    val features: ImmutableList<FeatureUiModel>,
    val rageshakeState: RageshakePreferencesState,
    val customElementCallBaseUrlState: CustomElementCallBaseUrlState,
    val tracingLogLevel: AsyncData<LogLevelItem>,
    val tracingLogPacks: ImmutableList<TraceLogPack>,
    val eventSink: (AppDeveloperSettingsEvent) -> Unit
)

data class CustomElementCallBaseUrlState(
    val baseUrl: String?,
    val validator: (String?) -> Boolean,
)
