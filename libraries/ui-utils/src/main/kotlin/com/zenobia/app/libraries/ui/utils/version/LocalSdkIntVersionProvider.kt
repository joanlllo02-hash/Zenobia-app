/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.ui.utils.version

import androidx.compose.runtime.staticCompositionLocalOf
import com.zenobia.app.services.toolbox.api.sdk.BuildVersionSdkIntProvider
import com.zenobia.app.services.toolbox.impl.sdk.DefaultBuildVersionSdkIntProvider

val LocalSdkIntVersionProvider = staticCompositionLocalOf<BuildVersionSdkIntProvider> { DefaultBuildVersionSdkIntProvider() }
