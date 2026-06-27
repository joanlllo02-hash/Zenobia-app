/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.ui.media

import coil3.ImageLoader
import com.zenobia.app.libraries.matrix.api.media.MatrixMediaLoader
import com.zenobia.app.tests.testutils.lambda.lambdaError

class FakeImageLoaderFactory(
    private val newImageLoaderLambda: () -> ImageLoader = { lambdaError() },
    private val newMatrixImageLoaderLambda: (MatrixMediaLoader) -> ImageLoader = { lambdaError() },
) : ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader {
        return newImageLoaderLambda()
    }

    override fun newImageLoader(matrixMediaLoader: MatrixMediaLoader): ImageLoader {
        return newMatrixImageLoaderLambda(matrixMediaLoader)
    }
}
