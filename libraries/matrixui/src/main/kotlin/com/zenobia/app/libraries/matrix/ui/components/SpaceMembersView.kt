/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.libraries.designsystem.atomic.molecules.MembersCountMolecule
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarData
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarRow
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarSize
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarType
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.preview.USER_NAME_ALICE
import com.zenobia.app.libraries.designsystem.preview.USER_NAME_BOB
import com.zenobia.app.libraries.designsystem.preview.USER_NAME_CHARLIE
import com.zenobia.app.libraries.designsystem.preview.USER_NAME_DAVID
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.designsystem.utils.CommonDrawables
import com.zenobia.app.libraries.matrix.api.user.MatrixUser
import com.zenobia.app.libraries.matrix.ui.model.getAvatarData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * Ref: https://www.figma.com/design/G1xy0HDZKJf5TCRFmKb5d5/Compound-Android-Components?node-id=3729-605&m=dev
 */
@Composable
fun SpaceMembersView(
    heroes: ImmutableList<MatrixUser>,
    numberOfMembers: Int,
    modifier: Modifier = Modifier,
) {
    if (heroes.isEmpty()) {
        MembersCountMolecule(
            memberCount = numberOfMembers,
            modifier = modifier,
        )
    } else {
        SpaceMembersWithAvatar(
            heroes = heroes
                .take(3)
                .map {
                    it.getAvatarData(AvatarSize.SpaceMember)
                }
                .toImmutableList(),
            numberOfMembers = numberOfMembers,
            modifier = modifier,
        )
    }
}

@Composable
private fun SpaceMembersWithAvatar(
    heroes: ImmutableList<AvatarData>,
    numberOfMembers: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        AvatarRow(
            avatarDataList = heroes,
            avatarType = AvatarType.User,
            lastOnTop = true,
        )
        Text(
            text = "$numberOfMembers",
            style = ZenobiaTheme.typography.fontBodyMdRegular,
            color = ZenobiaTheme.colors.textSecondary,
        )
    }
}

@Composable
@PreviewsDayNight
internal fun SpaceMembersViewNoHeroesPreview() = ZenobiaPreview {
    SpaceMembersView(
        heroes = persistentListOf(),
        numberOfMembers = 123,
    )
}

@Composable
@PreviewsDayNight
internal fun SpaceMembersViewPreview() = ZenobiaPreview(
    drawableFallbackForImages = CommonDrawables.sample_avatar,
) {
    SpaceMembersView(
        heroes = persistentListOf(
            aMatrixUser(id = "@1:d", displayName = USER_NAME_ALICE, avatarUrl = "aUrl"),
            aMatrixUser(id = "@2:d", displayName = USER_NAME_BOB),
            aMatrixUser(id = "@3:d", displayName = USER_NAME_CHARLIE, avatarUrl = "aUrl"),
            aMatrixUser(id = "@4:d", displayName = USER_NAME_DAVID),
        ),
        numberOfMembers = 123,
    )
}
