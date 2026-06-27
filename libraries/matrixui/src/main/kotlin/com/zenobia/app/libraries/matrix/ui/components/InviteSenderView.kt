/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.libraries.designsystem.components.avatar.Avatar
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarData
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarSize
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarType
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.preview.USER_NAME_BOB
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.ui.model.InviteSender

@Composable
fun InviteSenderView(
    inviteSender: InviteSender,
    modifier: Modifier = Modifier,
    hideAvatarImage: Boolean = false,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        Box(modifier = Modifier.padding(vertical = 2.dp)) {
            Avatar(
                avatarData = inviteSender.avatarData,
                avatarType = AvatarType.User,
                hideImage = hideAvatarImage,
            )
        }
        Text(
            text = inviteSender.annotatedString(),
            style = ZenobiaTheme.typography.fontBodyMdRegular,
            color = ZenobiaTheme.colors.textSecondary,
        )
    }
}

@PreviewsDayNight
@Composable
internal fun InviteSenderViewPreview() = ZenobiaPreview {
    InviteSenderView(
        inviteSender = InviteSender(
            userId = UserId("@bob:example.com"),
            displayName = USER_NAME_BOB,
            avatarData = AvatarData(
                id = "@bob:example.com",
                name = USER_NAME_BOB,
                url = null,
                size = AvatarSize.InviteSender,
            ),
            membershipChangeReason = null,
        )
    )
}
