/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.preferences.impl.tasks

import android.content.Context
import dev.zacsweers.metro.ContributesBinding
import com.zenobia.app.libraries.androidutils.file.getSizeOfFiles
import com.zenobia.app.libraries.androidutils.filesize.FileSizeFormatter
import com.zenobia.app.libraries.core.coroutine.CoroutineDispatchers
import com.zenobia.app.libraries.di.SessionScope
import com.zenobia.app.libraries.di.annotations.ApplicationContext
import com.zenobia.app.libraries.matrix.api.MatrixClient
import kotlinx.coroutines.withContext

interface ComputeCacheSizeUseCase {
    suspend operator fun invoke(): String
}

@ContributesBinding(SessionScope::class)
class DefaultComputeCacheSizeUseCase(
    @ApplicationContext private val context: Context,
    private val matrixClient: MatrixClient,
    private val coroutineDispatchers: CoroutineDispatchers,
    private val fileSizeFormatter: FileSizeFormatter,
) : ComputeCacheSizeUseCase {
    override suspend fun invoke(): String = withContext(coroutineDispatchers.io) {
        var cumulativeSize = 0L
        cumulativeSize += matrixClient.getCacheSize()
        // - 4096 to not include the size fo the folder
        cumulativeSize += (context.cacheDir.getSizeOfFiles() - 4096).coerceAtLeast(0)
        fileSizeFormatter.format(cumulativeSize)
    }
}
