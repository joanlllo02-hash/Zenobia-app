/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.threads

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.bumble.appyx.core.lifecycle.subscribe
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import com.zenobia.app.annotations.ContributesNode
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.features.messages.impl.MessagesNavigator
import com.zenobia.app.features.messages.impl.MessagesPresenter
import com.zenobia.app.features.messages.impl.MessagesState
import com.zenobia.app.features.messages.impl.MessagesView
import com.zenobia.app.features.messages.impl.actionlist.ActionListPresenter
import com.zenobia.app.features.messages.impl.actionlist.model.TimelineItemActionPostProcessor
import com.zenobia.app.features.messages.impl.attachments.Attachment
import com.zenobia.app.features.messages.impl.messagecomposer.MessageComposerEvent
import com.zenobia.app.features.messages.impl.messagecomposer.MessageComposerPresenter
import com.zenobia.app.features.messages.impl.timeline.TimelineController
import com.zenobia.app.features.messages.impl.timeline.TimelineEvent
import com.zenobia.app.features.messages.impl.timeline.TimelinePresenter
import com.zenobia.app.features.messages.impl.timeline.di.LocalTimelineItemPresenterFactories
import com.zenobia.app.features.messages.impl.timeline.di.TimelineItemPresenterFactories
import com.zenobia.app.features.messages.impl.timeline.model.TimelineItem
import com.zenobia.app.features.roommembermoderation.api.ModerationAction
import com.zenobia.app.features.roommembermoderation.api.RoomMemberModerationEvents
import com.zenobia.app.features.roommembermoderation.api.RoomMemberModerationRenderer
import com.zenobia.app.libraries.androidutils.browser.openUrlInChromeCustomTab
import com.zenobia.app.libraries.androidutils.system.openUrlInExternalApp
import com.zenobia.app.libraries.architecture.NodeInputs
import com.zenobia.app.libraries.architecture.Presenter
import com.zenobia.app.libraries.architecture.callback
import com.zenobia.app.libraries.architecture.inputs
import com.zenobia.app.libraries.designsystem.utils.OnLifecycleEvent
import com.zenobia.app.libraries.di.RoomScope
import com.zenobia.app.libraries.matrix.api.analytics.toAnalyticsViewRoom
import com.zenobia.app.libraries.matrix.api.core.EventId
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.core.ThreadId
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.api.core.toRoomIdOrAlias
import com.zenobia.app.libraries.matrix.api.permalink.PermalinkData
import com.zenobia.app.libraries.matrix.api.permalink.PermalinkParser
import com.zenobia.app.libraries.matrix.api.room.CreateTimelineParams
import com.zenobia.app.libraries.matrix.api.room.JoinedRoom
import com.zenobia.app.libraries.matrix.api.room.alias.matches
import com.zenobia.app.libraries.matrix.api.timeline.Timeline
import com.zenobia.app.libraries.matrix.api.timeline.item.TimelineItemDebugInfo
import com.zenobia.app.libraries.matrix.ui.model.getBestName
import com.zenobia.app.libraries.ui.utils.a11y.hasExternalKeyboard
import com.zenobia.app.libraries.ui.utils.a11y.isTalkbackActive
import com.zenobia.app.services.analytics.api.AnalyticsService
import com.zenobia.app.services.appnavstate.api.AppNavigationStateService
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@ContributesNode(RoomScope::class)
@AssistedInject
class ThreadedMessagesNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val room: JoinedRoom,
    private val analyticsService: AnalyticsService,
    private val messageComposerPresenterFactory: MessageComposerPresenter.Factory,
    private val timelinePresenterFactory: TimelinePresenter.Factory,
    private val presenterFactory: MessagesPresenter.Factory,
    private val actionListPresenterFactory: ActionListPresenter.Factory,
    private val timelineItemPresenterFactories: TimelineItemPresenterFactories,
    private val permalinkParser: PermalinkParser,
    private val appNavigationStateService: AppNavigationStateService,
    private val roomMemberModerationRenderer: RoomMemberModerationRenderer,
) : Node(buildContext, plugins = plugins), MessagesNavigator {
    data class Inputs(
        val threadRootEventId: ThreadId,
        val focusedEventId: EventId?,
    ) : NodeInputs

    private val inputs = inputs<Inputs>()
    private val callback: Callback = callback()

    private var timelineController: TimelineController? by mutableStateOf(null)
    private var presenter: Presenter<MessagesState>? by mutableStateOf(null)

    /**
     * This should be fast to load, but not faster than several UI frames, which will cause ANRs.
     * We'll load the [presenter] in an async way to prevent this.
     */
    private suspend fun createPresenter(): Presenter<MessagesState> {
        val threadedTimeline = room.createTimeline(CreateTimelineParams.Threaded(threadRootEventId = inputs.threadRootEventId)).getOrThrow()
        val timelineController = TimelineController(room, threadedTimeline)
        this.timelineController = timelineController
        return presenterFactory.create(
            navigator = this,
            composerPresenter = messageComposerPresenterFactory.create(timelineController, this, threadRoot = inputs.threadRootEventId),
            timelinePresenter = timelinePresenterFactory.create(timelineController = timelineController, this),
            // TODO add special processor for threaded timeline
            actionListPresenter = actionListPresenterFactory.create(
                postProcessor = TimelineItemActionPostProcessor.Default,
                timelineMode = timelineController.mainTimelineMode(),
            ),
            timelineController = timelineController,
        )
    }

    interface Callback : Plugin {
        fun handleEventClick(timelineMode: Timeline.Mode, event: TimelineItem.Event, canUseOverlay: Boolean): Boolean
        fun navigateToPreviewAttachments(attachments: ImmutableList<Attachment>, inReplyToEventId: EventId?)
        fun navigateToRoomMemberDetails(userId: UserId)
        fun handlePermalinkClick(data: PermalinkData)
        fun navigateToEventDebugInfo(eventId: EventId?, debugInfo: TimelineItemDebugInfo)
        fun handleForwardEventClick(eventId: EventId)
        fun navigateToReportMessage(eventId: EventId, senderId: UserId)
        fun navigateToSendLocation()
        fun navigateToCreatePoll()
        fun navigateToEditPoll(eventId: EventId)
        fun navigateToCurrentLiveLocation()
        fun navigateToRoomCall(roomId: RoomId, isAudioCall: Boolean)
        fun navigateToThread(threadRootId: ThreadId, focusedEventId: EventId?)
        fun navigateToDeveloperSettings()

        fun navigateToAvatarPreview(username: String, avatarUrl: String)
    }

    override fun onBuilt() {
        super.onBuilt()
        lifecycle.subscribe(
            onCreate = {
                analyticsService.capture(room.toAnalyticsViewRoom())
                lifecycleScope.launch {
                    presenter = createPresenter()
                }
            },
            onStart = {
                appNavigationStateService.onNavigateToThread(id, inputs.threadRootEventId)
            },
            onStop = {
                appNavigationStateService.onLeavingThread(id)
            },
        )
    }

    private fun onLinkClick(
        activity: Activity,
        darkTheme: Boolean,
        url: String,
        eventSink: (TimelineEvent) -> Unit,
        customTab: Boolean
    ) {
        when (val permalink = permalinkParser.parse(url)) {
            is PermalinkData.UserLink -> {
                // Open the room member profile, it will fallback to
                // the user profile if the user is not in the room
                callback.navigateToRoomMemberDetails(permalink.userId)
            }
            is PermalinkData.RoomLink -> {
                handleRoomLinkClick(permalink, eventSink)
            }
            is PermalinkData.FallbackLink -> {
                if (customTab) {
                    activity.openUrlInChromeCustomTab(null, darkTheme, url)
                } else {
                    activity.openUrlInExternalApp(url)
                }
            }
            is PermalinkData.RoomEmailInviteLink -> {
                activity.openUrlInChromeCustomTab(null, darkTheme, url)
            }
        }
    }

    private fun handleRoomLinkClick(
        roomLink: PermalinkData.RoomLink,
        eventSink: (TimelineEvent) -> Unit,
    ) {
        if (room.matches(roomLink.roomIdOrAlias)) {
            val eventId = roomLink.eventId
            if (eventId != null) {
                eventSink(TimelineEvent.FocusOnEvent(eventId))
            } else {
                // Click on the same room, navigate up
                // Note that it can not be enough to go back to the room if the thread has been opened
                // following a permalink from another thread. In this case navigating up will go back
                // to the previous thread. But this should not happen often.
                navigateUp()
            }
        } else {
            callback.handlePermalinkClick(roomLink)
        }
    }

    override fun navigateToEventDebugInfo(eventId: EventId?, debugInfo: TimelineItemDebugInfo) {
        callback.navigateToEventDebugInfo(eventId, debugInfo)
    }

    override fun forwardEvent(eventId: EventId) {
        callback.handleForwardEventClick(eventId)
    }

    override fun navigateToReportMessage(eventId: EventId, senderId: UserId) {
        callback.navigateToReportMessage(eventId, senderId)
    }

    override fun navigateToEditPoll(eventId: EventId) {
        callback.navigateToEditPoll(eventId)
    }

    override fun navigateToPreviewAttachments(attachments: ImmutableList<Attachment>, inReplyToEventId: EventId?) {
        callback.navigateToPreviewAttachments(attachments, inReplyToEventId)
    }

    override fun navigateToRoom(roomId: RoomId, eventId: EventId?, serverNames: List<String>) {
        val permalinkData = PermalinkData.RoomLink(roomId.toRoomIdOrAlias(), eventId, viaParameters = serverNames.toImmutableList())
        callback.handlePermalinkClick(permalinkData)
    }

    override fun navigateToMember(userId: UserId) {
        callback.navigateToRoomMemberDetails(userId)
    }

    override fun navigateToThread(threadRootId: ThreadId, focusedEventId: EventId?) {
        callback.navigateToThread(threadRootId, focusedEventId)
    }

    override fun navigateToDeveloperSettings() {
        callback.navigateToDeveloperSettings()
    }

    override fun navigateToCurrentLiveLocation() {
        // Shouldn't happen because LiveLocationSharingBanner is not shown in threads.
        callback.navigateToCurrentLiveLocation()
    }

    override fun close() = navigateUp()

    @Composable
    override fun View(modifier: Modifier) {
        val activity = requireNotNull(LocalActivity.current)
        val isDark = ZenobiaTheme.isLightTheme.not()
        val canUseOverlay = !isTalkbackActive() && !hasExternalKeyboard()
        CompositionLocalProvider(
            LocalTimelineItemPresenterFactories provides timelineItemPresenterFactories,
        ) {
            // Only display the actual UI and lifecycle logic if the presenter is loaded
            presenter?.present()?.let { state ->
                OnLifecycleEvent { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_PAUSE -> state.composerState.eventSink(MessageComposerEvent.SaveDraft)
                        else -> Unit
                    }
                }

                MessagesView(
                    state = state,
                    onBackClick = this::navigateUp,
                    onRoomDetailsClick = {},
                    onEventContentClick = { isLive, event ->
                        timelineController?.let { controller ->
                            if (isLive) {
                                callback.handleEventClick(controller.mainTimelineMode(), event, canUseOverlay)
                            } else {
                                val detachedTimelineMode = controller.detachedTimelineMode()
                                if (detachedTimelineMode != null) {
                                    callback.handleEventClick(detachedTimelineMode, event, canUseOverlay)
                                } else {
                                    false
                                }
                            }
                        } == true
                    },
                    onUserDataClick = callback::navigateToRoomMemberDetails,
                    onLinkClick = { url, customTab ->
                        onLinkClick(
                            activity = activity,
                            darkTheme = isDark,
                            url = url,
                            eventSink = state.timelineState.eventSink,
                            customTab = customTab,
                        )
                    },
                    onSendLocationClick = callback::navigateToSendLocation,
                    onCreatePollClick = callback::navigateToCreatePoll,
                    onJoinCallClick = { isAudioCall ->
                        callback.navigateToRoomCall(room.roomId, isAudioCall)
                    },
                    onViewAllPinnedMessagesClick = {},
                    modifier = modifier,
                    knockRequestsBannerView = {},
                    onThreadsListClick = {},
                )

                roomMemberModerationRenderer.Render(
                    state = state.roomMemberModerationState,
                    onSelectAction = { action, target ->
                        when (action) {
                            is ModerationAction.DisplayProfile -> callback.navigateToRoomMemberDetails(target.userId)
                            else -> state.roomMemberModerationState.eventSink(RoomMemberModerationEvents.ProcessAction(action, target))
                        }
                    },
                    onAvatarClick = { user ->
                        user.avatarUrl?.let { url ->
                            callback.navigateToAvatarPreview(user.getBestName(), url)
                        }
                    },
                    modifier = Modifier,
                )

                var focusedEventId by rememberSaveable {
                    mutableStateOf(inputs.focusedEventId)
                }
                LaunchedEffect(Unit) {
                    focusedEventId?.also { eventId ->
                        state.timelineState.eventSink(TimelineEvent.FocusOnEvent(eventId))
                    }
                    // Reset the focused event id to null to avoid refocusing when restoring node.
                    focusedEventId = null
                }
            }
        }
    }
}
