/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.verifysession.impl.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.libraries.designsystem.components.avatar.Avatar
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarSize
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarType
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.preview.USER_NAME_ALICE
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.designsystem.utils.CommonDrawables
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.api.user.MatrixUser
import com.zenobia.app.libraries.matrix.ui.model.getAvatarData
import com.zenobia.app.libraries.matrix.ui.model.getBestName

/**
 * Ref: https://www.figma.com/design/lMrKOhS8BEb75GXVq7FnNI/ER-96--User-Verification-by-Emoji?node-id=116-52049
 */
@Composable
fun VerificationUserProfileContent(
    user: MatrixUser,
    modifier: Modifier = Modifier,
) {
    val avatarData = remember(user) {
        user.getAvatarData(AvatarSize.UserVerification)
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ZenobiaTheme.colors.bgSubtleSecondary)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(
            avatarData = avatarData,
            avatarType = AvatarType.User,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = user.getBestName(),
                style = ZenobiaTheme.typography.fontBodyLgMedium,
                color = ZenobiaTheme.colors.textPrimary,
            )

            if (user.displayName.isNullOrEmpty().not()) {
                Text(
                    text = user.userId.value,
                    style = ZenobiaTheme.typography.fontBodyMdRegular,
                    color = ZenobiaTheme.colors.textSecondary,
                )
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun VerificationUserProfileContentPreview() = ZenobiaPreview(
    drawableFallbackForImages = CommonDrawables.sample_avatar
) {
    VerificationUserProfileContent(
        user = MatrixUser(
            userId = UserId("@alice:example.com"),
            displayName = USER_NAME_ALICE,
            avatarUrl = "https://example.com/avatar.png",
        )
    )
}
