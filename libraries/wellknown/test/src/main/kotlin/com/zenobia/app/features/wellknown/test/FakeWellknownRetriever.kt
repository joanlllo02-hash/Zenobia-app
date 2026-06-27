/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.wellknown.test

import com.zenobia.app.libraries.wellknown.api.ElementWellKnown
import com.zenobia.app.libraries.wellknown.api.WellknownRetriever
import com.zenobia.app.libraries.wellknown.api.WellknownRetrieverResult
import com.zenobia.app.tests.testutils.simulateLongTask

class FakeWellknownRetriever(
    private val getElementWellKnownResult: (String) -> WellknownRetrieverResult<ElementWellKnown> = { WellknownRetrieverResult.NotFound },
) : WellknownRetriever {
    override suspend fun getElementWellKnown(baseUrl: String): WellknownRetrieverResult<ElementWellKnown> = simulateLongTask {
        getElementWellKnownResult(baseUrl)
    }
}
