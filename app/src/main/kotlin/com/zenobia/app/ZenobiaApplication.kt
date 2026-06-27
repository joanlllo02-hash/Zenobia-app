/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app

import android.app.Application
import androidx.compose.material3.ComposeMaterial3Flags.isAnchoredDraggableComponentsStrictOffsetCheckEnabled
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.startup.AppInitializer
import androidx.work.Configuration
import dev.zacsweers.metro.createGraphFactory
import com.zenobia.app.libraries.di.DependencyInjectionGraphOwner
import com.zenobia.app.libraries.workmanager.api.di.MetroWorkerFactory
import com.zenobia.app.di.AppGraph
import com.zenobia.app.info.logApplicationInfo
import com.zenobia.app.initializer.CacheCleanerInitializer
import com.zenobia.app.initializer.CrashInitializer
import com.zenobia.app.initializer.PlatformInitializer

class ZenobiaApplication : Application(), DependencyInjectionGraphOwner, Configuration.Provider {
    override val graph: AppGraph = createGraphFactory<AppGraph.Factory>().create(this)

    override val workManagerConfiguration: Configuration = Configuration.Builder()
        .setWorkerFactory(MetroWorkerFactory(graph.workerProviders))
        .build()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate() {
        super.onCreate()
        AppInitializer.getInstance(this).apply {
            initializeComponent(CrashInitializer::class.java)
            initializeComponent(PlatformInitializer::class.java)
            initializeComponent(CacheCleanerInitializer::class.java)
        }

        logApplicationInfo(this)

        // Disable the strict offset check for anchored draggable components, as it can cause issues with bottom sheets.
        // Remove once https://issuetracker.google.com/issues/477038695 is fixed.
        isAnchoredDraggableComponentsStrictOffsetCheckEnabled = false
    }
}
