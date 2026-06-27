/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.designsystem.theme.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.zenobia.app.compound.theme.ZenobiaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleChoiceSegmentedButtonRowScope.SegmentedButton(
    index: Int,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
) {
    SegmentedButton(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        interactionSource = interactionSource,
        enabled = enabled,
        shape = SegmentedButtonDefaults.itemShape(index = index, count = count),
        label = {
            Text(
                text = text,
                style = ZenobiaTheme.typography.fontBodyMdMedium,
            )
        },
        colors = SegmentedButtonDefaults.colors(
            activeContainerColor = ZenobiaTheme.materialColors.primary,
            activeContentColor = ZenobiaTheme.materialColors.onPrimary,
            activeBorderColor = ZenobiaTheme.materialColors.primary,
            inactiveContainerColor = ZenobiaTheme.materialColors.surface,
            inactiveContentColor = ZenobiaTheme.materialColors.onSurface,
            inactiveBorderColor = ZenobiaTheme.materialColors.primary,
            disabledActiveContainerColor = ZenobiaTheme.colors.bgActionPrimaryDisabled,
            disabledActiveContentColor = ZenobiaTheme.colors.textOnSolidPrimary,
            disabledActiveBorderColor = ZenobiaTheme.colors.bgActionPrimaryDisabled,
            disabledInactiveContainerColor = ZenobiaTheme.materialColors.surface,
            disabledInactiveContentColor = ZenobiaTheme.colors.textDisabled,
            disabledInactiveBorderColor = Color.Transparent,
        )
    )
}
