/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.timeline.components.event

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.features.location.api.StaticMapView
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemLocationContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemLocationContentProvider
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.CircularProgressIndicator
import com.zenobia.app.libraries.designsystem.theme.components.Icon
import com.zenobia.app.libraries.designsystem.theme.components.IconButton
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun TimelineItemLocationView(
    content: TimelineItemLocationContent,
    onStopLiveLocationClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        StaticMapView(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 188.dp),
            pinVariant = content.pinVariant,
            location = content.location,
            zoom = 15.0,
            contentDescription = content.description
        )

        if (content.mode is TimelineItemLocationContent.Mode.Live) {
            LiveLocationOverlay(
                mode = content.mode,
                onStopClick = onStopLiveLocationClick,
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

@Composable
private fun LiveLocationOverlay(
    mode: TimelineItemLocationContent.Mode.Live,
    onStopClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(ZenobiaTheme.colors.bgCanvasDefault.copy(alpha = 0.9f)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val iconShape = RoundedCornerShape(8.dp)
        Box(
            modifier = Modifier
                // Ensure this Box uses same spacings than the Stop IconButton.
                .minimumInteractiveComponentSize()
                .size(32.dp)
                .border(
                    width = 1.dp,
                    color = if (mode.isActive) ZenobiaTheme.colors.iconQuaternaryAlpha else Color.Transparent,
                    shape = iconShape,
                )
                .background(
                    color = if (mode.isActive) {
                        ZenobiaTheme.colors.bgCanvasDefault
                    } else {
                        ZenobiaTheme.colors.bgSubtleSecondary
                    },
                    shape = iconShape
                )
        ) {
            if (mode.isLoading) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    color = ZenobiaTheme.colors.iconSecondary,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(20.dp)
                )
            } else {
                Icon(
                    imageVector = CompoundIcons.LocationPinSolid(),
                    contentDescription = null,
                    tint = if (mode.isActive) {
                        ZenobiaTheme.colors.iconAccentPrimary
                    } else {
                        ZenobiaTheme.colors.iconDisabled
                    },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (mode.isActive) {
                    stringResource(CommonStrings.common_live_location)
                } else {
                    stringResource(CommonStrings.common_live_location_ended)
                },
                style = ZenobiaTheme.typography.fontBodySmMedium,
                color = ZenobiaTheme.colors.textPrimary,
            )
            if (mode.isActive) {
                Text(
                    text = mode.endsAt,
                    style = ZenobiaTheme.typography.fontBodySmRegular,
                    color = ZenobiaTheme.colors.textPrimary,
                )
            }
        }

        if (mode.canStopSharing) {
            IconButton(
                onClick = onStopClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = ZenobiaTheme.colors.bgCriticalPrimary,
                    contentColor = ZenobiaTheme.colors.iconOnSolidPrimary,
                ),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .size(30.dp)
            ) {
                Icon(
                    imageVector = CompoundIcons.Stop(),
                    contentDescription = null,
                )
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun TimelineItemLocationViewPreview(@PreviewParameter(TimelineItemLocationContentProvider::class) content: TimelineItemLocationContent) =
    ZenobiaPreview {
        TimelineItemLocationView(
            content = content,
            onStopLiveLocationClick = {},
        )
    }
