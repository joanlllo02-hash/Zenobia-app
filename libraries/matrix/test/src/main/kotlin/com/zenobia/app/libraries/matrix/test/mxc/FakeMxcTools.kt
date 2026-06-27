/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.test.mxc

import com.zenobia.app.libraries.matrix.api.mxc.MxcTools
import com.zenobia.app.tests.testutils.lambda.lambdaError

class FakeMxcTools(
    private val mxcUri2FilePathResult: (String) -> String? = { lambdaError() }
) : MxcTools {
    override fun mxcUri2FilePath(mxcUri: String): String? {
        return mxcUri2FilePathResult(mxcUri)
    }
}
