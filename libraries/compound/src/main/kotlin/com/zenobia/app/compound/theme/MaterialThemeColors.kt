/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.compound.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.zenobia.app.compound.previews.ColorsSchemePreview
import com.zenobia.app.compound.tokens.generated.SemanticColors
import com.zenobia.app.compound.tokens.generated.compoundColorsHcDark
import com.zenobia.app.compound.tokens.generated.compoundColorsHcLight

fun SemanticColors.toMaterialColorScheme(): ColorScheme {
    return if (isLight) {
        toMaterialColorSchemeLight()
    } else {
        toMaterialColorSchemeDark()
    }
}

@Preview(heightDp = 1200)
@Composable
internal fun ColorsSchemeLightPreview() = ZenobiaTheme {
    ColorsSchemePreview(
        Color.Black,
        Color.White,
        ZenobiaTheme.materialColors,
    )
}

@Preview(heightDp = 1200)
@Composable
internal fun ColorsSchemeLightHcPreview() = ZenobiaTheme(
    compoundLight = compoundColorsHcLight,
) {
    ColorsSchemePreview(
        Color.Black,
        Color.White,
        ZenobiaTheme.materialColors,
    )
}

@Preview(heightDp = 1200)
@Composable
internal fun ColorsSchemeDarkPreview() = ZenobiaTheme(
    theme = Theme.Dark,
) {
    ColorsSchemePreview(
        Color.White,
        Color.Black,
        ZenobiaTheme.materialColors,
    )
}

@Preview(heightDp = 1200)
@Composable
internal fun ColorsSchemeDarkHcPreview() = ZenobiaTheme(
    theme = Theme.Dark,
    compoundDark = compoundColorsHcDark,
) {
    ColorsSchemePreview(
        Color.White,
        Color.Black,
        ZenobiaTheme.materialColors,
    )
}

@Preview(heightDp = 1200)
@Composable
internal fun ColorsSchemeBlackPreview() = ZenobiaTheme(
    theme = Theme.Black
) {
    ColorsSchemePreview(
        Color.White,
        Color.Black,
        ZenobiaTheme.materialColors,
    )
}
