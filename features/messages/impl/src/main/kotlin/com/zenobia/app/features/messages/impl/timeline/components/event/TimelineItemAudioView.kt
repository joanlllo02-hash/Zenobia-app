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
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.features.messages.impl.timeline.components.layout.ContentAvoidingLayoutData
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemAudioContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemAudioContentProvider
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight

@Composable
fun TimelineItemAudioView(
    content: TimelineItemAudioContent,
    onContentLayoutChange: (ContentAvoidingLayoutData) -> Unit,
    modifier: Modifier = Modifier,
) {
    TimelineItemAttachmentView(
        icon = CompoundIcons.Audio(),
        iconContentDescription = null,
        filename = content.filename,
        fileExtensionAndSize = content.fileExtensionAndSize,
        caption = content.caption,
        onContentLayoutChange = onContentLayoutChange,
        modifier = modifier,
    )
}

@PreviewsDayNight
@Composable
internal fun TimelineItemAudioViewPreview(@PreviewParameter(TimelineItemAudioContentProvider::class) content: TimelineItemAudioContent) =
    ElementTimelineItemPreview {
        TimelineItemAudioView(
            content,
            onContentLayoutChange = {},
        )
    }
