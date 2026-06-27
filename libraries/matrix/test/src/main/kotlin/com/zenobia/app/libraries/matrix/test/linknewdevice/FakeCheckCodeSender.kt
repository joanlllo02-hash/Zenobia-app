/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.test.linknewdevice

import com.zenobia.app.libraries.matrix.api.linknewdevice.CheckCodeSender
import com.zenobia.app.tests.testutils.lambda.lambdaError
import com.zenobia.app.tests.testutils.simulateLongTask

class FakeCheckCodeSender(
    private val validateResult: (UByte) -> Boolean = { lambdaError() },
    private val sendResult: (UByte) -> Result<Unit> = { lambdaError() },
) : CheckCodeSender {
    override suspend fun validate(code: UByte): Boolean = simulateLongTask {
        validateResult(code)
    }

    override suspend fun send(code: UByte): Result<Unit> = simulateLongTask {
        sendResult(code)
    }
}
