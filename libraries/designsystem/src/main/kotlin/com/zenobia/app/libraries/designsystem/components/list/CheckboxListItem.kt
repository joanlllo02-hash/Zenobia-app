/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.designsystem.components.list

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zenobia.app.libraries.designsystem.theme.components.ListItem
import com.zenobia.app.libraries.designsystem.theme.components.ListItemStyle
import com.zenobia.app.libraries.designsystem.theme.components.Text

@Composable
fun CheckboxListItem(
    headline: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    trailingContent: ListItemContent? = null,
    enabled: Boolean = true,
    style: ListItemStyle = ListItemStyle.Default,
    compactLayout: Boolean = false,
) {
    ListItem(
        modifier = modifier,
        headlineContent = { Text(headline) },
        supportingContent = supportingText?.let { @Composable { Text(it) } },
        leadingContent = ListItemContent.Checkbox(
            checked = checked,
            enabled = enabled,
            compact = compactLayout,
        ),
        trailingContent = trailingContent,
        style = style,
        enabled = enabled,
        onClick = { onChange(!checked) },
    )
}
