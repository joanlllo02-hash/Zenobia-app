/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.designsystem.atomic.atoms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.Icon

@Composable
fun SelectedIndicatorAtom(
    checked: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    if (checked) {
        Icon(
            modifier = modifier.toggleable(
                value = true,
                role = Role.Checkbox,
                enabled = enabled,
                onValueChange = {},
            ),
            imageVector = CompoundIcons.CheckCircleSolid(),
            contentDescription = null,
            tint = if (enabled) {
                ZenobiaTheme.colors.iconAccentPrimary
            } else {
                ZenobiaTheme.colors.iconDisabled
            },
        )
    } else {
        Box(modifier)
    }
}

@Composable
@PreviewsDayNight
internal fun SelectedIndicatorAtomPreview() = ZenobiaPreview {
    Column(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SelectedIndicatorAtom(
            modifier = Modifier.size(24.dp),
            checked = false,
            enabled = false,
        )
        SelectedIndicatorAtom(
            modifier = Modifier.size(24.dp),
            checked = true,
            enabled = false,
        )
        SelectedIndicatorAtom(
            modifier = Modifier.size(24.dp),
            checked = false,
            enabled = true,
        )
        SelectedIndicatorAtom(
            modifier = Modifier.size(24.dp),
            checked = true,
            enabled = true,
        )
    }
}
