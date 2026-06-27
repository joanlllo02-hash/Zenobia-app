/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.timeline.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.zenobia.app.features.messages.impl.timeline.TimelineEvent
import com.zenobia.app.features.messages.impl.timeline.aTimelineItemEvent
import com.zenobia.app.features.messages.impl.timeline.components.event.TimelineItemEventContentView
import com.zenobia.app.features.messages.impl.timeline.components.receipt.ReadReceiptViewState
import com.zenobia.app.features.messages.impl.timeline.components.receipt.TimelineItemReadReceiptView
import com.zenobia.app.features.messages.impl.timeline.components.receipt.aReadReceiptData
import com.zenobia.app.features.messages.impl.timeline.model.TimelineItem
import com.zenobia.app.features.messages.impl.timeline.model.TimelineItemGroupPosition
import com.zenobia.app.features.messages.impl.timeline.model.TimelineItemReadReceipts
import com.zenobia.app.features.messages.impl.timeline.model.event.aTimelineItemStateEventContent
import com.zenobia.app.features.messages.impl.timeline.util.defaultTimelineContentPadding
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import kotlinx.collections.immutable.persistentListOf

@Composable
fun TimelineItemStateEventRow(
    event: TimelineItem.Event,
    isLastOutgoingMessage: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onReadReceiptsClick: (event: TimelineItem.Event) -> Unit,
    eventSink: (TimelineEvent.TimelineItemEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 2.dp)
                .wrapContentHeight(),
            contentAlignment = Alignment.Center
        ) {
            MessageStateEventContainer(
                interactionSource = interactionSource,
                onClick = onClick,
                onLongClick = onLongClick,
                modifier = Modifier
                    .zIndex(-1f)
                    .widthIn(max = 320.dp)
            ) {
                TimelineItemEventContentView(
                    content = event.content,
                    onLinkClick = {},
                    onLinkLongClick = {},
                    hideMediaContent = false,
                    onShowContentClick = {},
                    eventSink = eventSink,
                    onContentClick = null,
                    onLongClick = null,
                    modifier = Modifier.defaultTimelineContentPadding()
                )
            }
        }
        TimelineItemReadReceiptView(
            state = ReadReceiptViewState(
                sendState = event.localSendState,
                isLastOutgoingMessage = isLastOutgoingMessage,
                receipts = event.readReceiptState.receipts,
            ),
            onReadReceiptsClick = { onReadReceiptsClick(event) },
        )
    }
}

@PreviewsDayNight
@Composable
internal fun TimelineItemStateEventRowPreview() = ZenobiaPreview {
    TimelineItemStateEventRow(
        event = aTimelineItemEvent(
            isMine = false,
            content = aTimelineItemStateEventContent(),
            groupPosition = TimelineItemGroupPosition.None,
            readReceiptState = TimelineItemReadReceipts(
                receipts = persistentListOf(aReadReceiptData(0)),
            )
        ),
        isLastOutgoingMessage = false,
        onClick = {},
        onLongClick = {},
        onReadReceiptsClick = {},
        eventSink = {}
    )
}
