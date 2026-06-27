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
import com.zenobia.app.features.messages.impl.timeline.TimelineEvent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemPollContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemPollContentProvider
import com.zenobia.app.features.poll.api.pollcontent.PollContentView
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.matrix.api.core.EventId
import kotlinx.collections.immutable.toImmutableList

@Composable
fun TimelineItemPollView(
    content: TimelineItemPollContent,
    eventSink: (TimelineEvent.TimelineItemPollEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    fun onSelectAnswer(pollStartId: EventId, answerId: String) {
        eventSink(TimelineEvent.SelectPollAnswer(pollStartId, answerId))
    }

    fun onEndPoll(pollStartId: EventId) {
        eventSink(TimelineEvent.EndPoll(pollStartId))
    }

    fun onEditPoll(pollStartId: EventId) {
        eventSink(TimelineEvent.EditPoll(pollStartId))
    }

    PollContentView(
        eventId = content.eventId,
        question = content.question,
        answerItems = content.answerItems.toImmutableList(),
        pollKind = content.pollKind,
        isPollEnded = content.isEnded,
        isPollEditable = content.isEditable,
        isMine = content.isMine,
        onSelectAnswer = ::onSelectAnswer,
        onEditPoll = ::onEditPoll,
        onEndPoll = ::onEndPoll,
        modifier = modifier,
    )
}

@PreviewsDayNight
@Composable
internal fun TimelineItemPollViewPreview(@PreviewParameter(TimelineItemPollContentProvider::class) content: TimelineItemPollContent) =
    ZenobiaPreview {
        TimelineItemPollView(
            content = content,
            eventSink = {},
        )
    }
