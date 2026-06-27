/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.poll.api.pollcontent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.libraries.designsystem.components.dialogs.ConfirmationDialog
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.Button
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.matrix.api.core.EventId
import com.zenobia.app.libraries.matrix.api.poll.PollAnswer
import com.zenobia.app.libraries.matrix.api.poll.PollKind
import com.zenobia.app.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.ImmutableList

@Composable
fun PollContentView(
    state: PollContentState,
    onSelectAnswer: (pollStartId: EventId, answerId: String) -> Unit,
    onEditPoll: (pollStartId: EventId) -> Unit,
    onEndPoll: (pollStartId: EventId) -> Unit,
    modifier: Modifier = Modifier,
) {
    PollContentView(
        eventId = state.eventId,
        question = state.question,
        answerItems = state.answerItems,
        pollKind = state.pollKind,
        isPollEditable = state.isPollEditable,
        isPollEnded = state.isPollEnded,
        isMine = state.isMine,
        onEditPoll = onEditPoll,
        onSelectAnswer = onSelectAnswer,
        onEndPoll = onEndPoll,
        modifier = modifier,
    )
}

@Composable
fun PollContentView(
    eventId: EventId?,
    question: String,
    answerItems: ImmutableList<PollAnswerItem>,
    pollKind: PollKind,
    isPollEditable: Boolean,
    isPollEnded: Boolean,
    isMine: Boolean,
    onSelectAnswer: (pollStartId: EventId, answerId: String) -> Unit,
    onEditPoll: (pollStartId: EventId) -> Unit,
    onEndPoll: (pollStartId: EventId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val votesCount = remember(answerItems) { answerItems.sumOf { it.votesCount } }

    fun onSelectAnswer(pollAnswer: PollAnswer) {
        eventId?.let { onSelectAnswer(it, pollAnswer.id) }
    }

    fun onEditPoll() {
        eventId?.let { onEditPoll(it) }
    }

    fun onEndPoll() {
        eventId?.let { onEndPoll(it) }
    }

    var showConfirmation: Boolean by remember { mutableStateOf(false) }

    if (showConfirmation) {
        ConfirmationDialog(
            content = stringResource(id = CommonStrings.common_poll_end_confirmation),
            onSubmitClick = {
                onEndPoll()
                showConfirmation = false
            },
            onDismiss = { showConfirmation = false },
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        PollTitleView(title = question, isPollEnded = isPollEnded)

        PollAnswers(answerItems = answerItems, onSelectAnswer = ::onSelectAnswer)

        if (isPollEnded || pollKind == PollKind.Disclosed) {
            DisclosedPollBottomNotice(votesCount = votesCount)
        } else {
            UndisclosedPollBottomNotice()
        }

        if (isMine) {
            CreatorView(
                isPollEnded = isPollEnded,
                isPollEditable = isPollEditable,
                onEditPoll = ::onEditPoll,
                onEndPoll = { showConfirmation = true },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun PollAnswers(
    answerItems: ImmutableList<PollAnswerItem>,
    onSelectAnswer: (PollAnswer) -> Unit,
) {
    Column(
        modifier = Modifier.selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        answerItems.forEach {
            PollAnswerView(
                answerItem = it,
                modifier = Modifier
                    .selectable(
                        selected = it.isSelected,
                        enabled = it.isEnabled,
                        onClick = { onSelectAnswer(it.answer) },
                        role = Role.RadioButton,
                    ),
            )
        }
    }
}

@Composable
private fun ColumnScope.DisclosedPollBottomNotice(
    votesCount: Int,
) {
    Text(
        modifier = Modifier.align(Alignment.End),
        style = ZenobiaTheme.typography.fontBodyXsRegular,
        color = ZenobiaTheme.colors.textSecondary,
        text = stringResource(CommonStrings.common_poll_total_votes, votesCount),
    )
}

@Composable
private fun ColumnScope.UndisclosedPollBottomNotice() {
    Text(
        modifier = Modifier
            .align(Alignment.Start)
            .padding(start = 34.dp),
        style = ZenobiaTheme.typography.fontBodyXsRegular,
        color = ZenobiaTheme.colors.textSecondary,
        text = stringResource(CommonStrings.common_poll_undisclosed_text),
    )
}

@Composable
private fun CreatorView(
    isPollEnded: Boolean,
    isPollEditable: Boolean,
    onEditPoll: () -> Unit,
    onEndPoll: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        isPollEditable ->
            Button(
                text = stringResource(id = CommonStrings.action_edit_poll),
                onClick = onEditPoll,
                modifier = modifier,
            )
        !isPollEnded ->
            Button(
                text = stringResource(id = CommonStrings.action_end_poll),
                onClick = onEndPoll,
                modifier = modifier,
            )
    }
}

@PreviewsDayNight
@Composable
internal fun PollContentViewUndisclosedPreview() = ZenobiaPreview {
    PollContentView(
        eventId = EventId("\$anEventId"),
        question = "What type of food should we have at the party?",
        answerItems = aPollAnswerItemList(showVotes = false),
        pollKind = PollKind.Undisclosed,
        isPollEnded = false,
        isPollEditable = false,
        isMine = false,
        onSelectAnswer = { _, _ -> },
        onEditPoll = {},
        onEndPoll = {},
    )
}

@PreviewsDayNight
@Composable
internal fun PollContentViewDisclosedPreview() = ZenobiaPreview {
    PollContentView(
        eventId = EventId("\$anEventId"),
        question = "What type of food should we have at the party?",
        answerItems = aPollAnswerItemList(),
        pollKind = PollKind.Disclosed,
        isPollEnded = false,
        isPollEditable = false,
        isMine = false,
        onSelectAnswer = { _, _ -> },
        onEditPoll = {},
        onEndPoll = {},
    )
}

@PreviewsDayNight
@Composable
internal fun PollContentViewEndedPreview() = ZenobiaPreview {
    PollContentView(
        eventId = EventId("\$anEventId"),
        question = "What type of food should we have at the party?",
        answerItems = aPollAnswerItemList(isEnded = true),
        pollKind = PollKind.Disclosed,
        isPollEnded = true,
        isPollEditable = false,
        isMine = false,
        onSelectAnswer = { _, _ -> },
        onEditPoll = {},
        onEndPoll = {},
    )
}

@PreviewsDayNight
@Composable
internal fun PollContentViewCreatorEditablePreview() = ZenobiaPreview {
    PollContentView(
        eventId = EventId("\$anEventId"),
        question = "What type of food should we have at the party?",
        answerItems = aPollAnswerItemList(hasVotes = false, isEnded = false),
        pollKind = PollKind.Disclosed,
        isPollEnded = false,
        isPollEditable = true,
        isMine = true,
        onSelectAnswer = { _, _ -> },
        onEditPoll = {},
        onEndPoll = {},
    )
}

@PreviewsDayNight
@Composable
internal fun PollContentViewCreatorPreview() = ZenobiaPreview {
    PollContentView(
        eventId = EventId("\$anEventId"),
        question = "What type of food should we have at the party?",
        answerItems = aPollAnswerItemList(isEnded = false),
        pollKind = PollKind.Disclosed,
        isPollEnded = false,
        isPollEditable = false,
        isMine = true,
        onSelectAnswer = { _, _ -> },
        onEditPoll = {},
        onEndPoll = {},
    )
}

@PreviewsDayNight
@Composable
internal fun PollContentViewCreatorEndedPreview() = ZenobiaPreview {
    PollContentView(
        eventId = EventId("\$anEventId"),
        question = "What type of food should we have at the party?",
        answerItems = aPollAnswerItemList(isEnded = true),
        pollKind = PollKind.Disclosed,
        isPollEnded = true,
        isPollEditable = false,
        isMine = true,
        onSelectAnswer = { _, _ -> },
        onEditPoll = {},
        onEndPoll = {},
    )
}
