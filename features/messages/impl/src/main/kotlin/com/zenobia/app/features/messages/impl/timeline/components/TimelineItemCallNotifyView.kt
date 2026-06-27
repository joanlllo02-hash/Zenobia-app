/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.timeline.components

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.features.messages.impl.timeline.TimelineRoomInfo
import com.zenobia.app.features.messages.impl.timeline.aTimelineItemEvent
import com.zenobia.app.features.messages.impl.timeline.aTimelineItemReadReceipts
import com.zenobia.app.features.messages.impl.timeline.aTimelineRoomInfo
import com.zenobia.app.features.messages.impl.timeline.components.receipt.ReadReceiptViewState
import com.zenobia.app.features.messages.impl.timeline.components.receipt.TimelineItemReadReceiptView
import com.zenobia.app.features.messages.impl.timeline.components.receipt.aReadReceiptData
import com.zenobia.app.features.messages.impl.timeline.model.TimelineItem
import com.zenobia.app.features.messages.impl.timeline.model.event.RtcNotificationState
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemRtcNotificationContent
import com.zenobia.app.libraries.designsystem.modifiers.onKeyboardContextMenuAction
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.text.toDp
import com.zenobia.app.libraries.matrix.api.notification.CallIntent
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
internal fun TimelineItemCallNotifyView(
    timelineRoomInfo: TimelineRoomInfo,
    event: TimelineItem.Event,
    content: TimelineItemRtcNotificationContent,
    isLastOutgoingMessage: Boolean,
    onLongClick: (TimelineItem.Event) -> Unit,
    onReadReceiptsClick: (TimelineItem.Event) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                .border(1.dp, ZenobiaTheme.colors.borderInteractiveSecondary, RoundedCornerShape(8.dp))
                .combinedClickable(
                    enabled = true,
                    onClick = {},
                    onLongClick = { onLongClick(event) },
                    onLongClickLabel = stringResource(CommonStrings.action_open_context_menu),
                )
                .onKeyboardContextMenuAction { onLongClick(event) }
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(20.sp.toDp()),
                imageVector = getIcon(timelineRoomInfo, content),
                contentDescription = null,
                tint = ZenobiaTheme.colors.iconSecondary,
            )

            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(getTextRes(timelineRoomInfo, content)),
                style = ZenobiaTheme.typography.fontBodyMdRegular,
                color = ZenobiaTheme.colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = event.sentTime,
                style = ZenobiaTheme.typography.fontBodyMdRegular,
                color = ZenobiaTheme.colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        TimelineItemReadReceiptView(
            state = ReadReceiptViewState(
                sendState = event.localSendState,
                isLastOutgoingMessage = isLastOutgoingMessage,
                receipts = event.readReceiptState.receipts,
            ),
            onReadReceiptsClick = { onReadReceiptsClick(event) },
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@StringRes
private fun getTextRes(
    timelineRoomInfo: TimelineRoomInfo,
    content: TimelineItemRtcNotificationContent
): Int = if (timelineRoomInfo.isDm) {
    when (content.state) {
        is RtcNotificationState.Declined -> {
            if (content.state.byMe) CommonStrings.common_call_you_declined else CommonStrings.common_call_declined
        }
        RtcNotificationState.Started -> CommonStrings.common_call_started
    }
} else {
    // In Rooms, do not show declined info.
    CommonStrings.common_call_started
}

@Composable
private fun getIcon(
    timelineRoomInfo: TimelineRoomInfo,
    content: TimelineItemRtcNotificationContent
): ImageVector {
    val showAsDeclined = timelineRoomInfo.isDm && content.state is RtcNotificationState.Declined
    val icon = if (showAsDeclined) {
        if (content.callIntent == CallIntent.AUDIO) CompoundIcons.VoiceCallDeclinedSolid() else CompoundIcons.VideoCallDeclinedSolid()
    } else {
        if (content.callIntent == CallIntent.AUDIO) CompoundIcons.VoiceCallSolid() else CompoundIcons.VideoCallSolid()
    }
    return icon
}

@PreviewsDayNight
@Composable
internal fun TimelineItemCallNotifyViewPreview() = ZenobiaPreview {
    val readReceiptState = mutableListOf(
        aTimelineItemReadReceipts(
            receipts = List(3) { aReadReceiptData(it) },
        )
    )
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        listOf(false, true).forEach { isDm ->
            listOf(CallIntent.AUDIO, CallIntent.VIDEO).forEach { callIntent ->
                listOf(
                    RtcNotificationState.Started,
                    RtcNotificationState.Declined(byMe = false),
                    RtcNotificationState.Declined(byMe = true),
                ).forEach { state ->
                    val content = TimelineItemRtcNotificationContent(callIntent, state)
                    TimelineItemCallNotifyView(
                        timelineRoomInfo = aTimelineRoomInfo(isDm = isDm),
                        event = aTimelineItemEvent(
                            content = content,
                            // Only display read receipts for the first item
                            readReceiptState = readReceiptState.removeFirstOrNull() ?: aTimelineItemReadReceipts(),
                        ),
                        content = content,
                        isLastOutgoingMessage = false,
                        onLongClick = {},
                        onReadReceiptsClick = {},
                    )
                }
            }
        }
    }
}
