/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.login.impl.accountprovider

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.features.login.impl.R
import com.zenobia.app.libraries.designsystem.atomic.atoms.RoundedIconAtom
import com.zenobia.app.libraries.designsystem.atomic.atoms.RoundedIconAtomSize
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.HorizontalDivider
import com.zenobia.app.libraries.designsystem.theme.components.Icon
import com.zenobia.app.libraries.designsystem.theme.components.Text

/**
 * https://www.figma.com/file/o9p34zmiuEpZRyvZXJZAYL/FTUE?type=design&node-id=604-60817
 */
@Composable
fun AccountProviderView(
    item: AccountProvider,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        HorizontalDivider()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 44.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (item.isMatrixOrg) {
                    RoundedIconAtom(
                        size = RoundedIconAtomSize.Medium,
                        resourceId = R.drawable.ic_matrix,
                        tint = Color.Unspecified,
                    )
                } else {
                    RoundedIconAtom(
                        size = RoundedIconAtomSize.Medium,
                        imageVector = CompoundIcons.Host(),
                        tint = ZenobiaTheme.colors.iconPrimary,
                    )
                }
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1f),
                    text = item.title,
                    style = ZenobiaTheme.typography.fontBodyLgMedium,
                    color = ZenobiaTheme.colors.textPrimary,
                )
                if (item.isPublic) {
                    Icon(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .size(16.dp),
                        imageVector = CompoundIcons.Public(),
                        contentDescription = null,
                        tint = ZenobiaTheme.colors.iconSecondary,
                    )
                }
                if (selected) {
                    Icon(
                        modifier = Modifier
                            .padding(start = 10.dp),
                        imageVector = CompoundIcons.Check(),
                        contentDescription = null,
                        tint = ZenobiaTheme.colors.iconAccentPrimary,
                    )
                }
            }
            if (item.subtitle != null) {
                Text(
                    modifier = Modifier
                        .padding(start = 46.dp, bottom = 12.dp, end = 26.dp),
                    text = item.subtitle,
                    style = ZenobiaTheme.typography.fontBodyMdRegular,
                    color = ZenobiaTheme.colors.textSecondary,
                )
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun AccountProviderViewPreview(@PreviewParameter(AccountProviderProvider::class) item: AccountProvider) = ZenobiaPreview {
    AccountProviderView(
        item = item,
        onClick = { }
    )
}
