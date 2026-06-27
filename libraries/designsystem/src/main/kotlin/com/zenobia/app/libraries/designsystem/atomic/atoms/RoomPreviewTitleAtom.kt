/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.designsystem.atomic.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.libraries.designsystem.theme.components.Text

@Composable
fun RoomPreviewTitleAtom(
    title: String,
    modifier: Modifier = Modifier,
    fontStyle: FontStyle? = null,
) {
    Text(
        modifier = modifier,
        text = title,
        style = ZenobiaTheme.typography.fontHeadingLgBold,
        textAlign = TextAlign.Center,
        fontStyle = fontStyle,
        color = ZenobiaTheme.colors.textPrimary,
    )
}
