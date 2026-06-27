/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.designsystem.theme.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.libraries.designsystem.preview.ZenobiaThemedPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewGroup

// Figma designs: https://www.figma.com/file/G1xy0HDZKJf5TCRFmKb5d5/Compound-Android-Components?type=design&node-id=1032%3A44063&mode=design&t=rsNegTbEVLYAXL76-1

@Composable
fun DropdownMenuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    androidx.compose.material3.DropdownMenuItem(
        text = {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyLarge) {
                text()
            }
        },
        onClick = onClick,
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        enabled = enabled,
        colors = DropDownMenuItemDefaults.colors(),
        contentPadding = DropDownMenuItemDefaults.contentPadding,
        interactionSource = interactionSource
    )
}

internal object DropDownMenuItemDefaults {
    @Composable
    fun colors() = MenuDefaults.itemColors(
        textColor = ZenobiaTheme.colors.textPrimary,
        leadingIconColor = ZenobiaTheme.colors.iconPrimary,
        trailingIconColor = ZenobiaTheme.colors.iconSecondary,
        disabledTextColor = ZenobiaTheme.colors.textDisabled,
        disabledLeadingIconColor = ZenobiaTheme.colors.iconDisabled,
        disabledTrailingIconColor = ZenobiaTheme.colors.iconDisabled,
    )

    val contentPadding = PaddingValues(all = 12.dp)
}

@Preview(group = PreviewGroup.Menus)
@Composable
internal fun DropdownMenuItemPreview() = ZenobiaThemedPreview {
    Column {
        DropdownMenuItem(
            text = { Text(text = "Item") },
            onClick = {},
            trailingIcon = { Icon(imageVector = CompoundIcons.ChevronRight(), contentDescription = null) },
        )
        HorizontalDivider()
        DropdownMenuItem(
            text = { Text(text = "Item") },
            onClick = {},
            leadingIcon = { Icon(imageVector = CompoundIcons.ChatProblem(), contentDescription = null) },
        )
        DropdownMenuItem(
            text = { Text(text = "Item") },
            onClick = {},
            leadingIcon = { Icon(imageVector = CompoundIcons.ChatProblem(), contentDescription = null) },
            trailingIcon = { Icon(imageVector = CompoundIcons.ChevronRight(), contentDescription = null) },
        )
        DropdownMenuItem(
            text = { Text(text = "Item") },
            onClick = {},
            enabled = false,
            leadingIcon = { Icon(imageVector = CompoundIcons.ChatProblem(), contentDescription = null) },
            trailingIcon = { Icon(imageVector = CompoundIcons.ChevronRight(), contentDescription = null) },
        )
        HorizontalDivider()
        DropdownMenuItem(
            text = { Text(text = "Multiline\nItem") },
            onClick = {},
            trailingIcon = { Icon(imageVector = CompoundIcons.ChevronRight(), contentDescription = null) },
        )
    }
}
