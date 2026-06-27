/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.designsystem.components.avatar.internal

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.AvatarColors
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewGroup

@Composable
internal fun TombstonedRoomAvatar(
    size: Dp,
    avatarShape: Shape,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    TextAvatar(
        text = "!",
        size = size,
        colors = AvatarColors(
            background = ZenobiaTheme.colors.bgSubtlePrimary,
            foreground = ZenobiaTheme.colors.iconTertiary
        ),
        modifier = modifier,
        avatarShape = avatarShape,
        contentDescription = contentDescription,
    )
}

@Preview(group = PreviewGroup.Avatars)
@Composable
internal fun TombstonedRoomAvatarPreview() = ZenobiaPreview {
    TombstonedRoomAvatar(
        size = 52.dp,
        avatarShape = CircleShape,
        contentDescription = null,
    )
}
