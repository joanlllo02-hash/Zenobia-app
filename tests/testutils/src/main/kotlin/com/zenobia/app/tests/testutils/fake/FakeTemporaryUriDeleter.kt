/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.tests.testutils.fake

import android.net.Uri
import com.zenobia.app.libraries.androidutils.file.TemporaryUriDeleter
import com.zenobia.app.tests.testutils.lambda.lambdaError

class FakeTemporaryUriDeleter(
    val deleteLambda: (uri: Uri?) -> Unit = { lambdaError() }
) : TemporaryUriDeleter {
    override fun delete(uri: Uri?) {
        deleteLambda(uri)
    }
}
