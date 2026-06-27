/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.designsystem.atomic.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.Icon
import com.zenobia.app.libraries.designsystem.theme.components.Text

@Composable
fun MembersCountMolecule(
    memberCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(color = ZenobiaTheme.colors.bgSubtleSecondary, shape = CircleShape)
            .padding(start = 2.dp, end = 8.dp, top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = CompoundIcons.UserProfile(),
            contentDescription = null,
            tint = ZenobiaTheme.colors.iconSecondary,
        )
        Text(
            text = "$memberCount",
            style = ZenobiaTheme.typography.fontBodySmMedium,
            color = ZenobiaTheme.colors.textSecondary,
        )
    }
}

@PreviewsDayNight
@Composable
internal fun MembersCountMoleculePreview() = ZenobiaPreview {
    Column(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MembersCountMolecule(memberCount = 1)
        MembersCountMolecule(memberCount = 888)
        MembersCountMolecule(memberCount = 123_456)
    }
}
