/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.designsystem.icons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.Icon
import com.zenobia.app.libraries.designsystem.theme.components.Text
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@PreviewsDayNight
@Composable
internal fun IconsOtherPreview() = ZenobiaPreview {
    IconsPreview(
        title = "Other icons",
        iconsList = iconsOther.toImmutableList(),
        iconNameTransform = { name ->
            name.removePrefix("ic_")
                .replace("_", " ")
        }
    )
}

@Composable
private fun IconsPreview(
    title: String,
    iconsList: ImmutableList<Int>,
    iconNameTransform: (String) -> String,
) = ZenobiaPreview {
    val context = LocalContext.current
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            style = ZenobiaTheme.typography.fontHeadingSmMedium,
            text = title,
            textAlign = TextAlign.Center,
        )
        iconsList.chunked(6).forEach { iconsRow ->
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                iconsRow.forEach { icon ->
                    Column(
                        modifier = Modifier.width(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            modifier = Modifier.padding(2.dp),
                            resourceId = icon,
                            contentDescription = null,
                        )
                        Text(
                            text = iconNameTransform(
                                context.resources
                                    .getResourceEntryName(icon)
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = ZenobiaTheme.typography.fontBodyXsMedium,
                            color = ZenobiaTheme.colors.textSecondary,
                        )
                    }
                }
            }
        }
    }
}
