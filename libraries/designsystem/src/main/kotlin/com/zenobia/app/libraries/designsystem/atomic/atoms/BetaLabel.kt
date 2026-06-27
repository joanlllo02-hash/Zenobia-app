/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.designsystem.atomic.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun BetaLabel(
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(size = 6.dp)
    Text(
        modifier = modifier
            .border(
                width = 1.dp,
                color = ZenobiaTheme.colors.borderInfoSubtle,
                shape = shape,
            )
            .background(
                color = ZenobiaTheme.colors.bgInfoSubtle,
                shape = shape,
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        text = stringResource(CommonStrings.common_beta).uppercase(),
        style = ZenobiaTheme.typography.fontBodySmMedium,
        color = ZenobiaTheme.colors.textInfoPrimary,
    )
}

@PreviewsDayNight
@Composable
internal fun BetaLabelPreview() = ZenobiaPreview {
    BetaLabel()
}
