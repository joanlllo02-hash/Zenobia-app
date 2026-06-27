/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.licenses.impl.list

import com.zenobia.app.features.licenses.impl.LicensesProvider
import com.zenobia.app.features.licenses.impl.model.DependencyLicenseItem
import com.zenobia.app.tests.testutils.lambda.lambdaError

class FakeLicensesProvider(
    private val provideResult: () -> List<DependencyLicenseItem> = { lambdaError() }
) : LicensesProvider {
    override suspend fun provides(): List<DependencyLicenseItem> {
        return provideResult()
    }
}
