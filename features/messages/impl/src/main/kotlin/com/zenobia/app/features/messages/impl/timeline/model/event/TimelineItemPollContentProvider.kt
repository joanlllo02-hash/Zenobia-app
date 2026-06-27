/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.timeline.model.event

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.zenobia.app.features.poll.api.pollcontent.PollAnswerItem
import com.zenobia.app.features.poll.api.pollcontent.aPollAnswerItemList
import com.zenobia.app.features.poll.api.pollcontent.aPollQuestion
import com.zenobia.app.libraries.matrix.api.core.EventId
import com.zenobia.app.libraries.matrix.api.poll.PollKind

open class TimelineItemPollContentProvider : PreviewParameterProvider<TimelineItemPollContent> {
    override val values: Sequence<TimelineItemPollContent>
        get() = sequenceOf(
            aTimelineItemPollContent(),
            aTimelineItemPollContent().copy(pollKind = PollKind.Undisclosed),
            aTimelineItemPollContent().copy(isMine = true),
            aTimelineItemPollContent().copy(isMine = true, isEditable = true),
        )
}

fun aTimelineItemPollContent(
    question: String = aPollQuestion(),
    answerItems: List<PollAnswerItem> = aPollAnswerItemList(),
    isMine: Boolean = false,
    isEditable: Boolean = false,
    isEnded: Boolean = false,
    isEdited: Boolean = false,
): TimelineItemPollContent {
    return TimelineItemPollContent(
        eventId = EventId("\$anEventId"),
        pollKind = PollKind.Disclosed,
        question = question,
        answerItems = answerItems,
        isMine = isMine,
        isEditable = isEditable,
        isEnded = isEnded,
        isEdited = isEdited,
    )
}
