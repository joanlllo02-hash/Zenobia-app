/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.preferences.impl.store

import com.google.common.truth.Truth.assertThat
import com.zenobia.app.libraries.core.meta.BuildMeta
import com.zenobia.app.libraries.core.meta.BuildType
import com.zenobia.app.libraries.preferences.test.FakePreferenceDataStoreFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultAppPreferencesStoreTest {
    private val buildMeta = BuildMeta(
        buildType = BuildType.DEBUG,
        isDebuggable = true,
        applicationName = "Zenobia",
        productionApplicationName = "Zenobia",
        desktopApplicationName = "Element Desktop",
        applicationId = "com.zenobia.app",
        isEnterpriseBuild = false,
        lowPrivacyLoggingEnabled = false,
        versionName = "1.0.0",
        versionCode = 1,
        gitRevision = "test",
        gitBranchName = "test",
        flavorDescription = "test",
        flavorShortDescription = "test",
    )

    @Test
    fun `live location minimum distance defaults to 10`() = runTest {
        val store = DefaultAppPreferencesStore(
            buildMeta = buildMeta,
            preferenceDataStoreFactory = FakePreferenceDataStoreFactory(),
        )

        assertThat(store.getLiveLocationMinimumDistanceInMetersUpdateFlow().first()).isEqualTo(10)
    }

    @Test
    fun `live location minimum distance persists updates`() = runTest {
        val store = DefaultAppPreferencesStore(
            buildMeta = buildMeta,
            preferenceDataStoreFactory = FakePreferenceDataStoreFactory(),
        )

        store.setLiveLocationMinimumDistanceInMetersUpdate(25)

        assertThat(store.getLiveLocationMinimumDistanceInMetersUpdateFlow().first()).isEqualTo(25)
    }
}
