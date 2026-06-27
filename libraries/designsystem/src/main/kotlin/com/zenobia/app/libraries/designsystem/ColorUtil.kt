/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.zenobia.app.compound.theme.ZenobiaTheme

@Composable
fun Boolean.toEnabledColor(): Color {
    return if (this) {
        ZenobiaTheme.colors.textPrimary
    } else {
        ZenobiaTheme.colors.textDisabled
    }
}

@Composable
fun Boolean.toSecondaryEnabledColor(): Color {
    return if (this) {
        ZenobiaTheme.colors.textSecondary
    } else {
        ZenobiaTheme.colors.textDisabled
    }
}

@Composable
fun Boolean.toIconEnabledColor(): Color {
    return if (this) {
        ZenobiaTheme.colors.iconPrimary
    } else {
        ZenobiaTheme.colors.iconDisabled
    }
}

@Composable
fun Boolean.toIconSecondaryEnabledColor(): Color {
    return if (this) {
        ZenobiaTheme.colors.iconSecondary
    } else {
        ZenobiaTheme.colors.iconDisabled
    }
}
