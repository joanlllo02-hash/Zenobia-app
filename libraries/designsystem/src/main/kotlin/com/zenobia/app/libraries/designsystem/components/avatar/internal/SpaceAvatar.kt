/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.designsystem.components.avatar.internal

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarData
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarType
import com.zenobia.app.libraries.designsystem.components.avatar.anAvatarData
import com.zenobia.app.libraries.designsystem.components.avatar.avatarShape
import com.zenobia.app.libraries.designsystem.preview.ZenobiaThemedPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewGroup
import com.zenobia.app.libraries.designsystem.utils.CommonDrawables

@Composable
internal fun SpaceAvatar(
    avatarData: AvatarData,
    avatarType: AvatarType.Space,
    modifier: Modifier = Modifier,
    forcedAvatarSize: Dp? = null,
    hideAvatarImage: Boolean = false,
    contentDescription: String? = null,
) {
    val size = forcedAvatarSize ?: avatarData.size.dp
    val avatarShape = avatarType.avatarShape(size)
    val commonModifier = modifier
        .border(
            width = 1.dp,
            color = ZenobiaTheme.colors.iconQuaternaryAlpha,
            shape = avatarShape,
        )
    when {
        avatarType.isTombstoned -> TombstonedRoomAvatar(
            size = size,
            avatarShape = avatarShape,
            modifier = commonModifier,
            contentDescription = contentDescription,
        )
        else -> InitialOrImageAvatar(
            avatarData = avatarData,
            hideAvatarImage = hideAvatarImage,
            avatarShape = avatarShape,
            forcedAvatarSize = forcedAvatarSize,
            modifier = commonModifier,
            contentDescription = contentDescription,
        )
    }
}

@Preview(group = PreviewGroup.Avatars)
@Composable
internal fun SpaceAvatarPreview() =
    ZenobiaThemedPreview(
        drawableFallbackForImages = CommonDrawables.sample_avatar,
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SpaceAvatar(
                avatarData = anAvatarData(),
                avatarType = AvatarType.Space(),
            )
            SpaceAvatar(
                avatarData = anAvatarData(),
                avatarType = AvatarType.Space(
                    isTombstoned = true,
                ),
            )
        }
    }
