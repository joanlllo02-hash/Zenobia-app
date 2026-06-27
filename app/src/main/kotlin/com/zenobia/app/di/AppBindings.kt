/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import com.zenobia.app.features.api.MigrationEntryPoint
import com.zenobia.app.features.enterprise.api.EnterpriseService
import com.zenobia.app.features.lockscreen.api.LockScreenEntryPoint
import com.zenobia.app.features.lockscreen.api.LockScreenService
import com.zenobia.app.features.rageshake.api.reporter.BugReporter
import com.zenobia.app.libraries.core.meta.BuildMeta
import com.zenobia.app.libraries.designsystem.utils.snackbar.SnackbarDispatcher
import com.zenobia.app.libraries.di.identifiers.SentrySdkDsn
import com.zenobia.app.libraries.featureflag.api.FeatureFlagService
import com.zenobia.app.libraries.matrix.api.platform.InitPlatformService
import com.zenobia.app.libraries.matrix.api.tracing.TracingService
import com.zenobia.app.libraries.preferences.api.store.AppPreferencesStore
import com.zenobia.app.services.analytics.api.AnalyticsService

@ContributesTo(AppScope::class)
interface AppBindings {
    fun snackbarDispatcher(): SnackbarDispatcher

    fun tracingService(): TracingService

    fun platformService(): InitPlatformService

    fun bugReporter(): BugReporter

    fun lockScreenService(): LockScreenService

    fun preferencesStore(): AppPreferencesStore

    fun migrationEntryPoint(): MigrationEntryPoint

    fun lockScreenEntryPoint(): LockScreenEntryPoint

    fun analyticsService(): AnalyticsService

    fun enterpriseService(): EnterpriseService

    fun featureFlagService(): FeatureFlagService

    fun buildMeta(): BuildMeta

    fun sentrySdkDsn(): SentrySdkDsn?
}
