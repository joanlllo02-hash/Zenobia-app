/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.features.login.impl.R
import com.zenobia.app.libraries.designsystem.atomic.atoms.RoundedIconAtom
import com.zenobia.app.libraries.designsystem.atomic.atoms.RoundedIconAtomSize
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.HorizontalDivider
import com.zenobia.app.libraries.designsystem.theme.components.Text

/**
 * https://www.figma.com/file/o9p34zmiuEpZRyvZXJZAYL/FTUE?type=design&node-id=604-60817
 */
@Composable
fun AccountProviderOtherView(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        HorizontalDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 44.dp)
                .padding(vertical = 4.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoundedIconAtom(
                size = RoundedIconAtomSize.Medium,
                imageVector = CompoundIcons.Search(),
                tint = ZenobiaTheme.colors.iconPrimary,
            )
            Text(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f),
                text = stringResource(R.string.screen_change_account_provider_other),
                style = ZenobiaTheme.typography.fontBodyLgMedium,
                color = ZenobiaTheme.colors.textPrimary,
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun AccountProviderOtherViewPreview() = ZenobiaPreview {
    AccountProviderOtherView(
        onClick = { },
    )
}
