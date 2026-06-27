/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.enterprise.test

import com.zenobia.app.features.enterprise.api.SessionEnterpriseService
import com.zenobia.app.tests.testutils.lambda.lambdaError
import com.zenobia.app.tests.testutils.simulateLongTask

class FakeSessionEnterpriseService(
    private val isElementCallAvailableResult: () -> Boolean = { lambdaError() },
    private val tweakMasUrlResult: (String) -> String = { lambdaError() },
) : SessionEnterpriseService {
    override suspend fun init() {
    }

    override suspend fun tweakMasUrl(url: String): String = simulateLongTask {
        tweakMasUrlResult(url)
    }

    override suspend fun isElementCallAvailable(): Boolean = simulateLongTask {
        isElementCallAvailableResult()
    }
}
