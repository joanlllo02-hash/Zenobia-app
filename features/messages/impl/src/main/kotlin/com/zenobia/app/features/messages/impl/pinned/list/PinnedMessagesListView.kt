/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.pinned.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import im.vector.app.features.analytics.plan.Interaction
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.features.messages.impl.actionlist.ActionListEvent
import com.zenobia.app.features.messages.impl.actionlist.ActionListView
import com.zenobia.app.features.messages.impl.actionlist.model.TimelineItemAction
import com.zenobia.app.features.messages.impl.link.LinkEvent
import com.zenobia.app.features.messages.impl.link.LinkView
import com.zenobia.app.features.messages.impl.timeline.TimelineEvent
import com.zenobia.app.features.messages.impl.timeline.components.TimelineItemRow
import com.zenobia.app.features.messages.impl.timeline.components.event.TimelineItemEventContentView
import com.zenobia.app.features.messages.impl.timeline.components.layout.ContentAvoidingLayoutData
import com.zenobia.app.features.messages.impl.timeline.model.TimelineItem
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemPollContent
import com.zenobia.app.features.messages.impl.timeline.protection.TimelineProtectionEvent
import com.zenobia.app.features.messages.impl.timeline.protection.TimelineProtectionState
import com.zenobia.app.features.poll.api.pollcontent.PollTitleView
import com.zenobia.app.libraries.designsystem.atomic.molecules.IconTitleSubtitleMolecule
import com.zenobia.app.libraries.designsystem.components.BigIcon
import com.zenobia.app.libraries.designsystem.components.button.BackButton
import com.zenobia.app.libraries.designsystem.components.dialogs.ErrorDialog
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.CircularProgressIndicator
import com.zenobia.app.libraries.designsystem.theme.components.Scaffold
import com.zenobia.app.libraries.designsystem.theme.components.TopAppBar
import com.zenobia.app.libraries.matrix.api.timeline.Timeline
import com.zenobia.app.libraries.matrix.api.user.MatrixUser
import com.zenobia.app.libraries.ui.strings.CommonStrings
import com.zenobia.app.services.analytics.compose.LocalAnalyticsService
import com.zenobia.app.services.analyticsproviders.api.trackers.captureInteraction
import com.zenobia.app.wysiwyg.link.Link

@Composable
fun PinnedMessagesListView(
    state: PinnedMessagesListState,
    onBackClick: () -> Unit,
    onEventClick: (event: TimelineItem.Event) -> Unit,
    onUserDataClick: (MatrixUser) -> Unit,
    onLinkClick: (Link) -> Unit,
    onLinkLongClick: (Link) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            val analyticsService = LocalAnalyticsService.current
            PinnedMessagesListTopBar(
                state = state,
                onBackClick = {
                    analyticsService.captureInteraction(Interaction.Name.PinnedMessageBannerCloseListButton)
                    onBackClick()
                }
            )
        },
        content = { padding ->
            PinnedMessagesListContent(
                state = state,
                onEventClick = onEventClick,
                onUserDataClick = onUserDataClick,
                onLinkClick = onLinkClick,
                onLinkLongClick = onLinkLongClick,
                onErrorDismiss = onBackClick,
                modifier = Modifier
                    .padding(padding)
                    .consumeWindowInsets(padding),
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PinnedMessagesListTopBar(
    state: PinnedMessagesListState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        titleStr = state.title(),
        navigationIcon = { BackButton(onClick = onBackClick) },
        modifier = modifier,
    )
}

@Composable
private fun PinnedMessagesListContent(
    state: PinnedMessagesListState,
    onEventClick: (event: TimelineItem.Event) -> Unit,
    onUserDataClick: (MatrixUser) -> Unit,
    onLinkClick: (Link) -> Unit,
    onLinkLongClick: (Link) -> Unit,
    onErrorDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize()) {
        when (state) {
            PinnedMessagesListState.Failed -> {
                ErrorDialog(
                    title = stringResource(id = CommonStrings.error_unknown),
                    content = stringResource(id = CommonStrings.error_failed_loading_messages),
                    onSubmit = onErrorDismiss
                )
            }
            PinnedMessagesListState.Empty -> PinnedMessagesListEmpty()
            is PinnedMessagesListState.Filled -> PinnedMessagesListLoaded(
                state = state,
                displayThreadSummaries = state.displayThreadSummaries,
                onEventClick = onEventClick,
                onUserDataClick = onUserDataClick,
                onLinkClick = onLinkClick,
                onLinkLongClick = onLinkLongClick,
            )
            PinnedMessagesListState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun PinnedMessagesListEmpty(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.padding(
            horizontal = 32.dp,
            vertical = 48.dp,
        ),
        contentAlignment = Alignment.Center,
    ) {
        val pinActionText = stringResource(id = CommonStrings.action_pin)
        IconTitleSubtitleMolecule(
            title = stringResource(id = CommonStrings.screen_pinned_timeline_empty_state_headline),
            subTitle = stringResource(id = CommonStrings.screen_pinned_timeline_empty_state_description, pinActionText),
            iconStyle = BigIcon.Style.Default(CompoundIcons.Pin()),
        )
    }
}

@Composable
private fun PinnedMessagesListLoaded(
    state: PinnedMessagesListState.Filled,
    displayThreadSummaries: Boolean,
    onEventClick: (event: TimelineItem.Event) -> Unit,
    onUserDataClick: (MatrixUser) -> Unit,
    onLinkClick: (Link) -> Unit,
    onLinkLongClick: (Link) -> Unit,
    modifier: Modifier = Modifier,
) {
    fun onActionSelected(timelineItemAction: TimelineItemAction, event: TimelineItem.Event) {
        state.actionListState.eventSink(
            ActionListEvent.Clear
        )
        state.eventSink(
            PinnedMessagesListEvent.HandleAction(
                action = timelineItemAction,
                event = event,
            )
        )
    }

    fun onMessageLongClick(event: TimelineItem.Event) {
        state.actionListState.eventSink(
            ActionListEvent.ComputeForMessage(
                event = event,
                userEventPermissions = state.userEventPermissions,
            )
        )
    }

    ActionListView(
        state = state.actionListState,
        onSelectAction = ::onActionSelected,
        onCustomReactionClick = {},
        onEmojiReactionClick = { _, _ -> },
        onVerifiedUserSendFailureClick = {}
    )
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = rememberLazyListState(),
        reverseLayout = true,
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        items(
            items = state.timelineItems,
            contentType = { timelineItem -> timelineItem.contentType() },
            key = { timelineItem -> timelineItem.identifier() },
        ) { timelineItem ->
            TimelineItemRow(
                timelineItem = timelineItem,
                timelineMode = Timeline.Mode.PinnedEvents,
                timelineRoomInfo = state.timelineRoomInfo,
                timelineProtectionState = state.timelineProtectionState,
                isLastOutgoingMessage = false,
                focusedEventId = null,
                onUserDataClick = onUserDataClick,
                onLinkClick = { link ->
                    state.linkState.eventSink(LinkEvent.OnLinkClick(link))
                },
                onLinkLongClick = onLinkLongClick,
                onContentClick = onEventClick,
                onLongClick = ::onMessageLongClick,
                displayThreadSummaries = displayThreadSummaries,
                inReplyToClick = {},
                onReactionClick = { _, _ -> },
                onReactionLongClick = { _, _ -> },
                onMoreReactionsClick = {},
                onReadReceiptClick = {},
                onSwipeToReply = {},
                eventSink = { timelineItemEvent ->
                    when (timelineItemEvent) {
                        is TimelineEvent.OpenThread -> state.eventSink(PinnedMessagesListEvent.OpenThread(timelineItemEvent.threadRootEventId))
                        else -> Unit
                    }
                },
                eventContentView = { event, contentModifier, onContentLayoutChange ->
                    TimelineItemEventContentViewWrapper(
                        event = event,
                        timelineProtectionState = state.timelineProtectionState,
                        onContentClick = { onEventClick(event) },
                        onLongClick = { onMessageLongClick(event) },
                        onLinkClick = { link ->
                            state.linkState.eventSink(LinkEvent.OnLinkClick(link))
                        },
                        onLinkLongClick = onLinkLongClick,
                        modifier = contentModifier,
                        onContentLayoutChange = onContentLayoutChange
                    )
                },
            )
        }
    }
    LinkView(
        state.linkState,
        onLinkValid = onLinkClick,
    )
}

@Composable
private fun TimelineItemEventContentViewWrapper(
    event: TimelineItem.Event,
    timelineProtectionState: TimelineProtectionState,
    onContentClick: () -> Unit,
    onLinkClick: (Link) -> Unit,
    onLinkLongClick: (Link) -> Unit,
    onLongClick: (() -> Unit)?,
    onContentLayoutChange: (ContentAvoidingLayoutData) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (event.content is TimelineItemPollContent) {
        PollTitleView(
            title = event.content.question,
            isPollEnded = event.content.isEnded,
            modifier = modifier
        )
    } else {
        TimelineItemEventContentView(
            content = event.content,
            hideMediaContent = timelineProtectionState.hideMediaContent(event.eventId, event.isMine),
            onShowContentClick = { timelineProtectionState.eventSink(TimelineProtectionEvent.ShowContent(event.eventId)) },
            onLinkClick = onLinkClick,
            onLinkLongClick = onLinkLongClick,
            eventSink = { },
            modifier = modifier,
            onContentClick = onContentClick,
            onLongClick = onLongClick,
            onContentLayoutChange = onContentLayoutChange
        )
    }
}

@PreviewsDayNight
@Composable
internal fun PinnedMessagesListViewPreview(@PreviewParameter(PinnedMessagesListStateProvider::class) state: PinnedMessagesListState) =
    ZenobiaPreview {
        PinnedMessagesListView(
            state = state,
            onBackClick = {},
            onEventClick = { },
            onUserDataClick = {},
            onLinkClick = {},
            onLinkLongClick = {},
        )
    }
