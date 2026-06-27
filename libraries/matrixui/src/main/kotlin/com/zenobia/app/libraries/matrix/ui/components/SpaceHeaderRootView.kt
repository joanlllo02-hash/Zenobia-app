/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.libraries.designsystem.components.BigIcon
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.ui.strings.CommonStrings

/**
 * Ref: https://www.figma.com/design/G1xy0HDZKJf5TCRFmKb5d5/Compound-Android-Components?node-id=3643-2048
 */
@Composable
fun SpaceHeaderRootView(
    numberOfSpaces: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BigIcon(
            style = BigIcon.Style.Default(CompoundIcons.SpaceSolid())
        )
        Text(
            text = stringResource(CommonStrings.screen_space_list_title),
            style = ZenobiaTheme.typography.fontHeadingLgBold,
            color = ZenobiaTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        SpaceInfoRow(
            leftText = numberOfSpaces(numberOfSpaces),
            rightText = null,
        )
        Text(
            text = stringResource(CommonStrings.screen_space_list_description),
            style = ZenobiaTheme.typography.fontBodyMdRegular,
            color = ZenobiaTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

@PreviewsDayNight
@Composable
internal fun SpaceHeaderRootViewPreview() = ZenobiaPreview {
    SpaceHeaderRootView(
        numberOfSpaces = 3,
    )
}
