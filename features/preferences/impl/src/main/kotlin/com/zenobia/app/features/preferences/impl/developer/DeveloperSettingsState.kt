/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.preferences.impl.developer

import com.zenobia.app.features.preferences.impl.developer.appsettings.AppDeveloperSettingsState
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.architecture.AsyncData
import kotlinx.collections.immutable.ImmutableMap

data class DeveloperSettingsState(
    val appDeveloperSettingsState: AppDeveloperSettingsState,
    val cacheSize: AsyncData<String>,
    val databaseSizes: AsyncData<ImmutableMap<String, String>>,
    val clearCacheAction: AsyncAction<Unit>,
    val isEnterpriseBuild: Boolean,
    val showColorPicker: Boolean,
    val eventSink: (DeveloperSettingsEvents) -> Unit
) {
    val showLoader = clearCacheAction is AsyncAction.Loading
}
