/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.push.impl.di

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import com.zenobia.app.libraries.architecture.Presenter
import com.zenobia.app.libraries.di.annotations.ApplicationContext
import com.zenobia.app.libraries.push.api.battery.BatteryOptimizationState
import com.zenobia.app.libraries.push.impl.battery.BatteryOptimizationPresenter

@BindingContainer
@ContributesTo(AppScope::class)
interface PushModule {
    companion object {
        @Provides
        fun provideNotificationCompatManager(@ApplicationContext context: Context): NotificationManagerCompat {
            return NotificationManagerCompat.from(context)
        }
    }

    @Binds
    fun bindBatteryOptimizationPresenter(presenter: BatteryOptimizationPresenter): Presenter<BatteryOptimizationState>
}
