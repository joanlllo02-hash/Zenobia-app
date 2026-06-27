/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.services.appnavstate.impl.di

import android.content.Context
import androidx.startup.AppInitializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import com.zenobia.app.libraries.di.annotations.ApplicationContext
import com.zenobia.app.services.appnavstate.api.AppForegroundStateService
import com.zenobia.app.services.appnavstate.impl.initializer.AppForegroundStateServiceInitializer

@BindingContainer
@ContributesTo(AppScope::class)
object AppNavStateModule {
    @Provides
    fun provideAppForegroundStateService(
        @ApplicationContext context: Context
    ): AppForegroundStateService =
        AppInitializer.getInstance(context).initializeComponent(AppForegroundStateServiceInitializer::class.java)
}
