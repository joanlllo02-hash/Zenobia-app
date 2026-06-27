/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.designsystem.atomic.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.libraries.designsystem.colors.gradientCriticalColors
import com.zenobia.app.libraries.designsystem.colors.gradientInfoColors
import com.zenobia.app.libraries.designsystem.components.avatar.Avatar
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarData
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarType
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.text.toAnnotatedString
import com.zenobia.app.libraries.designsystem.theme.components.Button
import com.zenobia.app.libraries.designsystem.theme.components.ButtonSize
import com.zenobia.app.libraries.designsystem.theme.components.Icon
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.ui.strings.CommonStrings

/**
 * Ref: https://www.figma.com/design/G1xy0HDZKJf5TCRFmKb5d5/Compound-Android-Components?node-id=2392-6721
 */
@Composable
fun ComposerAlertMolecule(
    avatar: AvatarData?,
    content: AnnotatedString,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier,
    level: ComposerAlertLevel = ComposerAlertLevel.Info,
    showIcon: Boolean = false,
    submitText: String = stringResource(CommonStrings.action_ok),
) {
    Column(
        modifier.fillMaxWidth()
    ) {
        val lineColor = when (level) {
            ComposerAlertLevel.Info -> ZenobiaTheme.colors.borderInfoSubtle
            ComposerAlertLevel.Critical -> ZenobiaTheme.colors.borderCriticalSubtle
        }

        val textColor = when (level) {
            ComposerAlertLevel.Info -> ZenobiaTheme.colors.textPrimary
            ComposerAlertLevel.Critical -> ZenobiaTheme.colors.textCriticalPrimary
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(lineColor)
        )
        val gradientColors = when (level) {
            ComposerAlertLevel.Info -> gradientInfoColors()
            ComposerAlertLevel.Critical -> gradientCriticalColors()
        }
        Box(
            modifier = Modifier
                .background(Brush.verticalGradient(gradientColors))
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (avatar != null) {
                        Avatar(
                            avatarData = avatar,
                            avatarType = AvatarType.User,
                        )
                    } else if (showIcon) {
                        val icon = when (level) {
                            ComposerAlertLevel.Info -> CompoundIcons.Info()
                            ComposerAlertLevel.Critical -> CompoundIcons.Error()
                        }
                        val iconTint = when (level) {
                            ComposerAlertLevel.Info -> ZenobiaTheme.colors.iconInfoPrimary
                            ComposerAlertLevel.Critical -> ZenobiaTheme.colors.iconCriticalPrimary
                        }
                        Icon(
                            imageVector = icon,
                            tint = iconTint,
                            contentDescription = null,
                        )
                    }
                    Text(
                        text = content,
                        modifier = Modifier.weight(1f),
                        style = ZenobiaTheme.typography.fontBodyMdRegular,
                        color = textColor,
                        textAlign = TextAlign.Start,
                    )
                }
                Button(
                    text = submitText,
                    size = ButtonSize.Medium,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onSubmitClick,
                )
            }
        }
    }
}

enum class ComposerAlertLevel {
    Info,
    Critical
}

@PreviewsDayNight
@Composable
internal fun ComposerAlertMoleculePreview(
    @PreviewParameter(ComposerAlertMoleculeParamsProvider::class) params: ComposerAlertMoleculeParams,
) = ZenobiaPreview {
    ComposerAlertMolecule(
        avatar = params.avatar,
        content = "Alice’s verified identity has changed. Learn more".toAnnotatedString(),
        level = params.level,
        showIcon = params.showIcon,
        onSubmitClick = {},
    )
}
