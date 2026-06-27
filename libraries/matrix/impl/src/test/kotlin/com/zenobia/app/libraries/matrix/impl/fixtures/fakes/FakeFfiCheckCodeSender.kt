/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.impl.fixtures.fakes

import com.zenobia.app.tests.testutils.lambda.lambdaError
import org.matrix.rustcomponents.sdk.CheckCodeSender
import org.matrix.rustcomponents.sdk.NoHandle

class FakeFfiCheckCodeSender(
    private val sendResult: (UByte) -> Unit = { _ -> lambdaError() }
) : CheckCodeSender(NoHandle) {
    override suspend fun send(code: UByte) {
        sendResult(code)
    }
}
