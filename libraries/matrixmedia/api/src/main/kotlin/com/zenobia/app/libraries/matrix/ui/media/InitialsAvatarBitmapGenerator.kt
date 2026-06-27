/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.ui.media

import android.graphics.Bitmap
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarData

/**
 * Generates a bitmap for an initials avatar based on the provided [com.zenobia.app.libraries.designsystem.components.avatar.AvatarData].
 */
interface InitialsAvatarBitmapGenerator {
    fun generateBitmap(
        size: Int,
        avatarData: AvatarData,
        useDarkTheme: Boolean,
        fontSizePercentage: Float = 0.5f,
    ): Bitmap?
}
