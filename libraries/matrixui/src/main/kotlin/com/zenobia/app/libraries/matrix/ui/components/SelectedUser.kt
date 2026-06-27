/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.LayoutDirection
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarSize
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarType
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.preview.USER_NAME_JOHN_DOE
import com.zenobia.app.libraries.matrix.api.user.MatrixUser
import com.zenobia.app.libraries.matrix.ui.model.getAvatarData
import com.zenobia.app.libraries.matrix.ui.model.getBestName

@Composable
fun SelectedUser(
    matrixUser: MatrixUser,
    canRemove: Boolean,
    onUserRemove: (MatrixUser) -> Unit,
    modifier: Modifier = Modifier,
) {
    SelectedItem(
        avatarData = matrixUser.getAvatarData(size = AvatarSize.SelectedUser),
        avatarType = AvatarType.User,
        text = matrixUser.getBestName(),
        maxLines = 2,
        a11yContentDescription = matrixUser.getBestName(),
        canRemove = canRemove,
        onRemoveClick = { onUserRemove(matrixUser) },
        modifier = modifier,
    )
}

@PreviewsDayNight
@Composable
internal fun SelectedUserPreview(@PreviewParameter(MatrixUserWithAvatarProvider::class) user: MatrixUser) = ZenobiaPreview {
    SelectedUser(
        matrixUser = user,
        canRemove = true,
        onUserRemove = {},
    )
}

@PreviewsDayNight
@Composable
internal fun SelectedUserRtlPreview() = CompositionLocalProvider(
    LocalLayoutDirection provides LayoutDirection.Rtl,
) {
    ZenobiaPreview {
        SelectedUser(
            matrixUser = aMatrixUser(displayName = USER_NAME_JOHN_DOE),
            canRemove = true,
            onUserRemove = {},
        )
    }
}

@PreviewsDayNight
@Composable
internal fun SelectedUserCannotRemovePreview() = ZenobiaPreview {
    SelectedUser(
        matrixUser = aMatrixUser(),
        canRemove = false,
        onUserRemove = {},
    )
}
