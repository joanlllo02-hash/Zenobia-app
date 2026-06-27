/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.designsystem.atomic.atoms

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.libraries.designsystem.components.Badge
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight

/**
 * https://www.figma.com/design/G1xy0HDZKJf5TCRFmKb5d5/Compound-Android-Components?node-id=1960-491
 */
object MatrixBadgeAtom {
    data class MatrixBadgeData(
        val text: String,
        val icon: ImageVector,
        val type: Type,
    )

    enum class Type {
        Positive,
        Neutral,
        Negative,
        Info,
    }

    @Composable
    fun View(
        data: MatrixBadgeData,
    ) {
        val backgroundColor = when (data.type) {
            Type.Positive -> ZenobiaTheme.colors.bgBadgeAccent
            Type.Neutral -> ZenobiaTheme.colors.bgBadgeDefault
            Type.Negative -> ZenobiaTheme.colors.bgCriticalSubtle
            Type.Info -> ZenobiaTheme.colors.bgBadgeInfo
        }
        val borderStroke = when (data.type) {
            Type.Positive -> null
            Type.Neutral -> BorderStroke(1.dp, ZenobiaTheme.colors.borderInteractiveSecondary)
            Type.Negative -> null
            Type.Info -> null
        }
        val textColor = when (data.type) {
            Type.Positive -> ZenobiaTheme.colors.textBadgeAccent
            Type.Neutral -> ZenobiaTheme.colors.textPrimary
            Type.Negative -> ZenobiaTheme.colors.textCriticalPrimary
            Type.Info -> ZenobiaTheme.colors.textBadgeInfo
        }
        val iconColor = when (data.type) {
            Type.Positive -> ZenobiaTheme.colors.iconAccentPrimary
            Type.Neutral -> ZenobiaTheme.colors.iconPrimary
            Type.Negative -> ZenobiaTheme.colors.iconCriticalPrimary
            Type.Info -> ZenobiaTheme.colors.iconInfoPrimary
        }
        Badge(
            text = data.text,
            icon = data.icon,
            backgroundColor = backgroundColor,
            iconColor = iconColor,
            textColor = textColor,
            borderStroke = borderStroke,
        )
    }
}

@PreviewsDayNight
@Composable
internal fun MatrixBadgeAtomPositivePreview() = ZenobiaPreview {
    MatrixBadgeAtom.View(
        MatrixBadgeAtom.MatrixBadgeData(
            text = "Trusted",
            icon = CompoundIcons.Verified(),
            type = MatrixBadgeAtom.Type.Positive,
        )
    )
}

@PreviewsDayNight
@Composable
internal fun MatrixBadgeAtomNeutralPreview() = ZenobiaPreview {
    MatrixBadgeAtom.View(
        MatrixBadgeAtom.MatrixBadgeData(
            text = "Public room",
            icon = CompoundIcons.Public(),
            type = MatrixBadgeAtom.Type.Neutral,
        )
    )
}

@PreviewsDayNight
@Composable
internal fun MatrixBadgeAtomNegativePreview() = ZenobiaPreview {
    MatrixBadgeAtom.View(
        MatrixBadgeAtom.MatrixBadgeData(
            text = "Not trusted",
            icon = CompoundIcons.ErrorSolid(),
            type = MatrixBadgeAtom.Type.Negative,
        )
    )
}

@PreviewsDayNight
@Composable
internal fun MatrixBadgeAtomNeutralWrappingPreview() = ZenobiaPreview {
    MatrixBadgeAtom.View(
        MatrixBadgeAtom.MatrixBadgeData(
            text = "How much wood could a wood chuck chuck if a wood chuck could chuck wood",
            icon = CompoundIcons.LockOff(),
            type = MatrixBadgeAtom.Type.Info,
        )
    )
}

@PreviewsDayNight
@Composable
internal fun MatrixBadgeAtomInfoPreview() = ZenobiaPreview {
    MatrixBadgeAtom.View(
        MatrixBadgeAtom.MatrixBadgeData(
            text = "Not encrypted",
            icon = CompoundIcons.LockOff(),
            type = MatrixBadgeAtom.Type.Info,
        )
    )
}
