/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.test.permalink

import com.zenobia.app.libraries.matrix.api.permalink.PermalinkData
import com.zenobia.app.libraries.matrix.api.permalink.PermalinkParser
import com.zenobia.app.tests.testutils.lambda.lambdaError

class FakePermalinkParser(
    private var result: (String) -> PermalinkData = { lambdaError() }
) : PermalinkParser {
    fun givenResult(result: PermalinkData) {
        this.result = { result }
    }

    override fun parse(uriString: String): PermalinkData {
        return result(uriString)
    }
}
