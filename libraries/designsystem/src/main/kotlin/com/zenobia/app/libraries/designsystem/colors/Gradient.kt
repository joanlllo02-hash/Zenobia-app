/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.designsystem.colors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.zenobia.app.compound.theme.ZenobiaTheme

@Composable
@ReadOnlyComposable
fun gradientActionColors(): List<Color> = listOf(
    ZenobiaTheme.colors.gradientActionStop1,
    ZenobiaTheme.colors.gradientActionStop2,
    ZenobiaTheme.colors.gradientActionStop3,
    ZenobiaTheme.colors.gradientActionStop4,
)

@Composable
@ReadOnlyComposable
fun gradientSubtleColors(): List<Color> = listOf(
    ZenobiaTheme.colors.gradientSubtleStop1,
    ZenobiaTheme.colors.gradientSubtleStop2,
    ZenobiaTheme.colors.gradientSubtleStop3,
    ZenobiaTheme.colors.gradientSubtleStop4,
    ZenobiaTheme.colors.gradientSubtleStop5,
    ZenobiaTheme.colors.gradientSubtleStop6,
)

@Composable
@ReadOnlyComposable
fun gradientInfoColors(): List<Color> = listOf(
    ZenobiaTheme.colors.gradientInfoStop1,
    ZenobiaTheme.colors.gradientInfoStop2,
)

@Composable
@ReadOnlyComposable
fun gradientCriticalColors(): List<Color> = listOf(
    ZenobiaTheme.colors.gradientCriticalStop1,
    ZenobiaTheme.colors.gradientCriticalStop2,
)
