/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.test

import com.zenobia.app.libraries.matrix.api.MatrixClient
import com.zenobia.app.libraries.matrix.api.MatrixClientProvider
import com.zenobia.app.libraries.matrix.api.core.SessionId

class FakeMatrixClientProvider(
    var getClient: (SessionId) -> Result<MatrixClient> = { Result.success(FakeMatrixClient()) }
) : MatrixClientProvider {
    override suspend fun getOrRestore(sessionId: SessionId): Result<MatrixClient> = getClient(sessionId)

    override fun getOrNull(sessionId: SessionId): MatrixClient? = getClient(sessionId).getOrNull()
}
