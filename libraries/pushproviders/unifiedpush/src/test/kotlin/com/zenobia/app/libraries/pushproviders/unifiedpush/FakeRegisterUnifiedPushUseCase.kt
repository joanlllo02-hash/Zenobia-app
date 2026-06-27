/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.pushproviders.unifiedpush

import com.zenobia.app.libraries.pushproviders.api.Distributor
import com.zenobia.app.tests.testutils.lambda.lambdaError

class FakeRegisterUnifiedPushUseCase(
    private val result: (Distributor, String) -> Result<Unit> = { _, _ -> lambdaError() }
) : RegisterUnifiedPushUseCase {
    override suspend fun execute(distributor: Distributor, clientSecret: String): Result<Unit> {
        return result(distributor, clientSecret)
    }
}
