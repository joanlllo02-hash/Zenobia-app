/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.timeline.components.event

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemStateContent
import com.zenobia.app.features.messages.impl.timeline.model.event.aTimelineItemStateEventContent
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.Text

@Composable
fun TimelineItemStateView(
    content: TimelineItemStateContent,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        color = ZenobiaTheme.colors.textSecondary,
        style = ZenobiaTheme.typography.fontBodyMdRegular,
        text = content.body,
        textAlign = TextAlign.Center,
    )
}

@PreviewsDayNight
@Composable
internal fun TimelineItemStateViewPreview() = ZenobiaPreview {
    TimelineItemStateView(
        content = aTimelineItemStateEventContent(),
    )
}
