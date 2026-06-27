/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.di

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.preference.PreferenceManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import com.zenobia.app.appconfig.ApplicationConfig
import com.zenobia.app.features.enterprise.api.EnterpriseService
import com.zenobia.app.libraries.androidutils.system.getVersionCodeFromManifest
import com.zenobia.app.libraries.core.coroutine.CoroutineDispatchers
import com.zenobia.app.libraries.core.meta.BuildMeta
import com.zenobia.app.libraries.core.meta.BuildType
import com.zenobia.app.libraries.designsystem.utils.snackbar.SnackbarDispatcher
import com.zenobia.app.libraries.di.BaseDirectory
import com.zenobia.app.libraries.di.CacheDirectory
import com.zenobia.app.libraries.di.annotations.AppCoroutineScope
import com.zenobia.app.libraries.di.annotations.ApplicationContext
import com.zenobia.app.libraries.recentemojis.api.EmojibaseProvider
import com.zenobia.app.libraries.recentemojis.impl.DefaultEmojibaseProvider
import com.zenobia.app.BuildConfig
import com.zenobia.app.R
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.plus
import java.io.File

@BindingContainer
@ContributesTo(AppScope::class)
object AppModule {
    @Provides
    @BaseDirectory
    fun providesBaseDirectory(@ApplicationContext context: Context): File {
        return File(context.filesDir, "sessions")
    }

    @Provides
    @CacheDirectory
    fun providesCacheDirectory(@ApplicationContext context: Context): File {
        return context.cacheDir
    }

    @Provides
    fun providesResources(@ApplicationContext context: Context): Resources {
        return context.resources
    }

    @Provides
    @AppCoroutineScope
    @SingleIn(AppScope::class)
    fun providesAppCoroutineScope(): CoroutineScope {
        return MainScope() + CoroutineName("Zenobia Scope")
    }

    @Provides
    @SingleIn(AppScope::class)
    fun providesBuildType(): BuildType {
        return BuildType.valueOf(BuildConfig.BUILD_TYPE.uppercase())
    }

    @Provides
    @SingleIn(AppScope::class)
    fun providesBuildMeta(
        @ApplicationContext context: Context,
        buildType: BuildType,
        enterpriseService: EnterpriseService,
    ): BuildMeta {
        val applicationName = ApplicationConfig.APPLICATION_NAME.takeIf { it.isNotEmpty() } ?: context.getString(R.string.app_name)
        return BuildMeta(
            isDebuggable = BuildConfig.DEBUG,
            buildType = buildType,
            applicationName = applicationName,
            productionApplicationName = if (enterpriseService.isEnterpriseBuild) applicationName else ApplicationConfig.PRODUCTION_APPLICATION_NAME,
            desktopApplicationName = if (enterpriseService.isEnterpriseBuild) applicationName else ApplicationConfig.DESKTOP_APPLICATION_NAME,
            applicationId = BuildConfig.APPLICATION_ID,
            isEnterpriseBuild = enterpriseService.isEnterpriseBuild,
            // TODO EAx Config.LOW_PRIVACY_LOG_ENABLE,
            lowPrivacyLoggingEnabled = false,
            versionName = BuildConfig.VERSION_NAME,
            versionCode = context.getVersionCodeFromManifest(),
            gitRevision = BuildConfig.GIT_REVISION,
            gitBranchName = BuildConfig.GIT_BRANCH_NAME,
            flavorDescription = BuildConfig.FLAVOR_DESCRIPTION,
            flavorShortDescription = BuildConfig.SHORT_FLAVOR_DESCRIPTION,
        )
    }

    @Provides
    @SingleIn(AppScope::class)
    fun providesSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @SingleIn(AppScope::class)
    fun providesCoroutineDispatchers(): CoroutineDispatchers {
        return CoroutineDispatchers.Default
    }

    @Provides
    @SingleIn(AppScope::class)
    fun provideSnackbarDispatcher(): SnackbarDispatcher {
        return SnackbarDispatcher()
    }

    @Provides
    @SingleIn(AppScope::class)
    fun providesEmojibaseProvider(@ApplicationContext context: Context): EmojibaseProvider {
        return DefaultEmojibaseProvider(context)
    }
}
