/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.designsystem.components.button

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.libraries.designsystem.preview.ZenobiaThemedPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewGroup
import com.zenobia.app.libraries.designsystem.theme.components.Icon
import com.zenobia.app.libraries.designsystem.theme.components.IconButton
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    // TODO Handle RTL languages
    imageVector: ImageVector = CompoundIcons.ArrowLeft(),
    contentDescription: String = stringResource(CommonStrings.action_back),
    enabled: Boolean = true,
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
    ) {
        Icon(imageVector, contentDescription = contentDescription)
    }
}

@Preview(group = PreviewGroup.Buttons)
@Composable
internal fun BackButtonPreview() = ZenobiaThemedPreview {
    Column {
        BackButton(onClick = { }, enabled = true)
        BackButton(onClick = { }, enabled = false)
    }
}
