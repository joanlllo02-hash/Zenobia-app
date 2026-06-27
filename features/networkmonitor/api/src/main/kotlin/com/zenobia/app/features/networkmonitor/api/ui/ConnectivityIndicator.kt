/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.networkmonitor.api.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.text.toDp
import com.zenobia.app.libraries.designsystem.theme.components.Icon
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
internal fun ConnectivityIndicator(
    verticalPadding: Dp,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .background(ZenobiaTheme.colors.bgSubtlePrimary)
            .statusBarsPadding()
            .padding(vertical = verticalPadding),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = CompoundIcons.Offline(),
            contentDescription = null,
            tint = ZenobiaTheme.colors.iconPrimary,
            modifier = Modifier.size(16.sp.toDp()),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(CommonStrings.common_offline),
            style = ZenobiaTheme.typography.fontBodyMdMedium,
            color = ZenobiaTheme.colors.textPrimary,
        )
    }
}

@PreviewsDayNight
@Composable
internal fun ConnectivityIndicatorPreview() = ZenobiaPreview {
    ConnectivityIndicator(verticalPadding = 6.dp)
}
