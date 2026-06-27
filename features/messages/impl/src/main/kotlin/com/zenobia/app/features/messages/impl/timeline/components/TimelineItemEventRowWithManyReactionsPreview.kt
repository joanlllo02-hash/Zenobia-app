/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.timeline.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.zenobia.app.features.messages.impl.timeline.aTimelineItemEvent
import com.zenobia.app.features.messages.impl.timeline.aTimelineItemReactions
import com.zenobia.app.features.messages.impl.timeline.model.event.aTimelineItemTextContent
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight

@PreviewsDayNight
@Composable
internal fun TimelineItemEventRowWithManyReactionsPreview() = ZenobiaPreview {
    Column {
        listOf(false, true).forEach { isMine ->
            ATimelineItemEventRow(
                event = aTimelineItemEvent(
                    isMine = isMine,
                    content = aTimelineItemTextContent(
                        body = "A couple of multi-line messages with many reactions attached." +
                            " One sent by me and another from someone else."
                    ),
                    timelineItemReactions = aTimelineItemReactions(count = 20),
                ),
            )
        }
    }
}
