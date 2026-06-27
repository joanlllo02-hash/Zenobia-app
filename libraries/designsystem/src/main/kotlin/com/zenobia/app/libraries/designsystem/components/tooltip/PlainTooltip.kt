/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.designsystem.components.tooltip

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.zenobia.app.compound.theme.ZenobiaTheme
import androidx.compose.material3.PlainTooltip as M3PlainTooltip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TooltipScope.PlainTooltip(
    modifier: Modifier = Modifier,
    contentColor: Color = ZenobiaTheme.colors.textOnSolidPrimary,
    containerColor: Color = ZenobiaTheme.colors.bgActionPrimaryRest,
    shape: Shape = TooltipDefaults.plainTooltipContainerShape,
    content: @Composable () -> Unit,
) = M3PlainTooltip(
    modifier = modifier,
    contentColor = contentColor,
    containerColor = containerColor,
    shape = shape,
    content = content,
)
