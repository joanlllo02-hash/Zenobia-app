/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.designsystem.preview

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import com.zenobia.app.compound.theme.Theme
import com.zenobia.app.libraries.designsystem.utils.CommonDrawables

@Composable
fun ZenobiaPreviewDark(
    showBackground: Boolean = true,
    @DrawableRes
    drawableFallbackForImages: Int = CommonDrawables.sample_background,
    content: @Composable () -> Unit,
) {
    ZenobiaPreview(
        theme = Theme.Dark,
        showBackground = showBackground,
        drawableFallbackForImages = drawableFallbackForImages,
        content = content,
    )
}
