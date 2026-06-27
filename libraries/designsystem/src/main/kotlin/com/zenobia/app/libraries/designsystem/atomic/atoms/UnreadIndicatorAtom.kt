/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.designsystem.atomic.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.unreadIndicator

@Composable
fun UnreadIndicatorAtom(
    modifier: Modifier = Modifier,
    size: Dp = 12.dp,
    count: Long? = null,
    color: Color = ZenobiaTheme.colors.unreadIndicator,
    isVisible: Boolean = true,
    contentDescription: String? = null,
) {
    when {
        !isVisible -> Spacer(modifier = modifier.size(size))
        count != null && count >= 1 -> CounterAtom(
            count = count.toInt(),
            modifier = modifier.semantics {
                contentDescription?.let { this.contentDescription = it }
            },
            containerColor = color,
            contentColor = ZenobiaTheme.colors.bgCanvasDefault,
            textStyle = ZenobiaTheme.typography.fontBodySmMedium,
        )
        else -> Box(
            modifier = modifier
                .semantics {
                    contentDescription?.let { this.contentDescription = it }
                }
                .size(size)
                .clip(CircleShape)
                .background(color),
        )
    }
}

@PreviewsDayNight
@Composable
internal fun UnreadIndicatorAtomPreview() = ZenobiaPreview {
    UnreadIndicatorAtom()
}
