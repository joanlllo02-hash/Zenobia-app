/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.test.analytics

import com.zenobia.app.services.analytics.api.AnalyticsSdkManager
import com.zenobia.app.services.analytics.api.AnalyticsSdkSpan
import com.zenobia.app.services.analytics.api.NoopAnalyticsSdkSpan
import com.zenobia.app.tests.testutils.lambda.lambdaError

class FakeAnalyticsSdkManager(
    private val enableSdkAnalyticsLambda: ((Boolean) -> Unit) = { lambdaError() },
) : AnalyticsSdkManager {
    override fun enableSdkAnalytics(enabled: Boolean) {
        enableSdkAnalyticsLambda(enabled)
    }

    override fun startSpan(name: String, parentTraceId: String?): AnalyticsSdkSpan = NoopAnalyticsSdkSpan
    override fun bridge(parentTraceId: String?): AnalyticsSdkSpan = NoopAnalyticsSdkSpan
}
