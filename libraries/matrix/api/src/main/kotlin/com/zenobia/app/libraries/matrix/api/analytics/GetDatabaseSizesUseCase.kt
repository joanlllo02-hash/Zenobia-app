/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.api.analytics

import com.zenobia.app.libraries.matrix.api.core.SessionId

fun interface GetDatabaseSizesUseCase {
    operator fun invoke(sessionId: SessionId): Result<SdkStoreSizes>
}
