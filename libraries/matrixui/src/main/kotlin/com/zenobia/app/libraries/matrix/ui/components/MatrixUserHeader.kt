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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.libraries.designsystem.components.avatar.Avatar
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarSize
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarType
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.matrix.api.user.MatrixUser
import com.zenobia.app.libraries.matrix.ui.model.getAvatarData
import com.zenobia.app.libraries.matrix.ui.model.getBestName

@Composable
fun MatrixUserHeader(
    matrixUser: MatrixUser,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(
            modifier = Modifier
                .padding(vertical = 7.dp),
            avatarData = matrixUser.getAvatarData(size = AvatarSize.UserPreference),
            avatarType = AvatarType.User,
        )
        Spacer(modifier = Modifier.width(13.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Name
            Text(
                modifier = Modifier.clipToBounds(),
                text = matrixUser.getBestName(),
                maxLines = 1,
                style = ZenobiaTheme.typography.fontHeadingMdRegular,
                overflow = TextOverflow.Ellipsis,
                color = ZenobiaTheme.colors.textPrimary,
            )
            // Id
            if (matrixUser.displayName.isNullOrEmpty().not()) {
                Text(
                    text = matrixUser.userId.value,
                    style = ZenobiaTheme.typography.fontBodyMdRegular,
                    color = ZenobiaTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun MatrixUserHeaderPreview(@PreviewParameter(MatrixUserProvider::class) matrixUser: MatrixUser) = ZenobiaPreview {
    MatrixUserHeader(matrixUser)
}
