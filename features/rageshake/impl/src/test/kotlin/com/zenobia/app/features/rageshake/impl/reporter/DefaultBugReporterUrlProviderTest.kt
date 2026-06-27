/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.rageshake.impl.reporter

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.zenobia.app.appconfig.RageshakeConfig
import com.zenobia.app.features.enterprise.api.BugReportUrl
import com.zenobia.app.features.enterprise.api.EnterpriseService
import com.zenobia.app.features.enterprise.test.FakeEnterpriseService
import com.zenobia.app.libraries.sessionstorage.api.SessionStore
import com.zenobia.app.libraries.sessionstorage.test.InMemorySessionStore
import kotlinx.coroutines.test.runTest
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Test

class DefaultBugReporterUrlProviderTest {
    @Test
    fun `provide returns values when there is an rageshake app name`() = runTest {
        val enterpriseService = FakeEnterpriseService()
        val sut = createDefaultBugReporterUrlProvider(
            bugReportAppNameProvider = { "rageshakeAppName" },
            enterpriseService = enterpriseService,
        )
        sut.provide().test {
            assertThat(awaitItem()).isEqualTo(
                RageshakeConfig.BUG_REPORT_URL.takeIf { it.isNotEmpty() }?.toHttpUrl()
            )
            enterpriseService.bugReportUrlMutableFlow.emit(BugReportUrl.Disabled)
            assertThat(awaitItem()).isNull()
            enterpriseService.bugReportUrlMutableFlow.emit(BugReportUrl.Custom("https://aURL.org"))
            assertThat(awaitItem()).isEqualTo("https://aURL.org".toHttpUrl())
        }
    }

    @Test
    fun `provide returns null when there is no rageshake app name`() = runTest {
        val sut = createDefaultBugReporterUrlProvider()
        sut.provide().test {
            assertThat(awaitItem()).isNull()
            awaitComplete()
        }
    }
}

private fun createDefaultBugReporterUrlProvider(
    bugReportAppNameProvider: BugReportAppNameProvider = BugReportAppNameProvider { "" },
    enterpriseService: EnterpriseService = FakeEnterpriseService(),
    sessionStore: SessionStore = InMemorySessionStore(),
) = DefaultBugReporterUrlProvider(
    bugReportAppNameProvider = bugReportAppNameProvider,
    enterpriseService = enterpriseService,
    sessionStore = sessionStore,
)
