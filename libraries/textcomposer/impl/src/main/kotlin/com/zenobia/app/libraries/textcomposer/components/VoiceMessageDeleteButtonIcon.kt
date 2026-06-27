/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.textcomposer.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.Icon
import com.zenobia.app.libraries.designsystem.theme.components.IconButton
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun VoiceMessageDeleteButtonIcon(
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Icon(
        modifier = modifier.size(24.dp),
        imageVector = CompoundIcons.Delete(),
        contentDescription = stringResource(CommonStrings.a11y_delete),
        tint = if (enabled) {
            ZenobiaTheme.colors.iconCriticalPrimary
        } else {
            ZenobiaTheme.colors.iconDisabled
        },
    )
}

@PreviewsDayNight
@Composable
internal fun VoiceMessageDeleteButtonIconPreview() = ZenobiaPreview {
    Row {
        IconButton(onClick = {}) {
            VoiceMessageDeleteButtonIcon(
                enabled = true,
            )
        }
        IconButton(onClick = {}) {
            VoiceMessageDeleteButtonIcon(
                enabled = false,
            )
        }
    }
}
