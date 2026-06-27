/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.libraries.designsystem.components.avatar.Avatar
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarData
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarSize
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarType
import com.zenobia.app.libraries.designsystem.preview.ZenobiaThemedPreview
import com.zenobia.app.libraries.designsystem.theme.components.Icon
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.matrix.ui.model.getAvatarData
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun UnresolvedUserRow(
    avatarData: AvatarData,
    id: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(
            avatarData = avatarData,
            avatarType = AvatarType.User,
        )
        Column(
            modifier = Modifier
                .padding(start = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // ID
            Text(
                text = id,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (enabled) ZenobiaTheme.colors.textPrimary else ZenobiaTheme.colors.textDisabled,
                style = ZenobiaTheme.typography.fontBodyLgMedium,
            )

            // Warning
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 3.dp)
            ) {
                Icon(
                    imageVector = CompoundIcons.ErrorSolid(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.Top)
                        .padding(2.dp),
                    tint = if (enabled) ZenobiaTheme.colors.iconCriticalPrimary else ZenobiaTheme.colors.iconDisabled,
                )
                Text(
                    text = stringResource(CommonStrings.common_invite_unknown_profile),
                    color = if (enabled) ZenobiaTheme.colors.textSecondary else ZenobiaTheme.colors.textDisabled,
                    style = ZenobiaTheme.typography.fontBodySmRegular.copy(lineHeight = 16.sp),
                )
            }
        }
    }
}

@Preview
@Composable
internal fun UnresolvedUserRowPreview() = ZenobiaThemedPreview {
    val matrixUser = aMatrixUser()
    Column {
        UnresolvedUserRow(matrixUser.getAvatarData(size = AvatarSize.UserListItem), matrixUser.userId.value)
        UnresolvedUserRow(matrixUser.getAvatarData(size = AvatarSize.UserListItem), matrixUser.userId.value, enabled = false)
    }
}
