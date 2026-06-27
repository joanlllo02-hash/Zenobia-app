/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.ui.media

import coil3.ImageLoader
import com.zenobia.app.libraries.matrix.api.MatrixClient
import com.zenobia.app.libraries.matrix.api.core.SessionId

interface ImageLoaderHolder {
    fun get(): ImageLoader
    fun get(client: MatrixClient): ImageLoader
    fun remove(sessionId: SessionId)
}
