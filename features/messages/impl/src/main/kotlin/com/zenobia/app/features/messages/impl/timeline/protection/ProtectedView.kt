/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.timeline.protection

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.theme.Theme
import com.zenobia.app.features.messages.impl.timeline.components.event.TimelineItemAspectRatioBox
import com.zenobia.app.libraries.designsystem.components.blurhash.blurHashBackground
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.matrix.ui.components.A_BLUR_HASH
import com.zenobia.app.libraries.ui.strings.CommonStrings

@SuppressWarnings("ModifierClickableOrder")
@Composable
fun ProtectedView(
    hideContent: Boolean,
    onShowClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (hideContent) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0x99000000)),
            contentAlignment = Alignment.Center,
        ) {
            ZenobiaTheme(theme = Theme.Light, applySystemBarsUpdate = false) {
                // Not using a button to be able to have correct size
                Text(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .clickable(
                            onClick = onShowClick,
                            role = Role.Button,
                        )
                        .padding(4.dp)
                        .border(
                            width = 1.dp,
                            color = ZenobiaTheme.colors.borderInteractiveSecondary,
                            shape = CircleShape,
                        )
                        .padding(
                            horizontal = 16.dp,
                            vertical = 4.dp,
                        ),
                    text = stringResource(CommonStrings.action_show),
                    color = ZenobiaTheme.colors.textOnSolidPrimary,
                    style = ZenobiaTheme.typography.fontBodyLgMedium,
                )
            }
        }
    } else {
        content()
    }
}

@PreviewsDayNight
@Composable
internal fun ProtectedViewPreview(
    @PreviewParameter(AspectRatioProvider::class) aspectRatio: Float?,
) = ZenobiaPreview {
    TimelineItemAspectRatioBox(
        modifier = Modifier.blurHashBackground(A_BLUR_HASH, alpha = 0.9f),
        aspectRatio = coerceRatioWhenHidingContent(aspectRatio, true),
    ) {
        ProtectedView(
            hideContent = true,
            onShowClick = {},
            content = {},
        )
    }
}
