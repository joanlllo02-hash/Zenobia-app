/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.bumble.appyx.core.lifecycle.subscribe
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.pop
import com.bumble.appyx.navmodel.backstack.operation.push
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import im.vector.app.features.analytics.plan.Interaction
import com.zenobia.app.annotations.ContributesNode
import com.zenobia.app.features.call.api.CallData
import com.zenobia.app.features.call.api.ElementCallEntryPoint
import com.zenobia.app.features.forward.api.ForwardEntryPoint
import com.zenobia.app.features.knockrequests.api.list.KnockRequestsListEntryPoint
import com.zenobia.app.features.location.api.LocationService
import com.zenobia.app.features.location.api.RenderingMapsNotSupportedDialog
import com.zenobia.app.features.location.api.ShareLocationEntryPoint
import com.zenobia.app.features.location.api.ShowLocationEntryPoint
import com.zenobia.app.features.location.api.ShowLocationMode
import com.zenobia.app.features.messages.api.MessagesEntryPoint
import com.zenobia.app.features.messages.impl.attachments.Attachment
import com.zenobia.app.features.messages.impl.attachments.preview.AttachmentsPreviewNode
import com.zenobia.app.features.messages.impl.pinned.DefaultPinnedEventsTimelineProvider
import com.zenobia.app.features.messages.impl.pinned.list.PinnedMessagesListNode
import com.zenobia.app.features.messages.impl.report.ReportMessageNode
import com.zenobia.app.features.messages.impl.threads.ThreadedMessagesNode
import com.zenobia.app.features.messages.impl.threads.list.ThreadsListNode
import com.zenobia.app.features.messages.impl.timeline.TimelineController
import com.zenobia.app.features.messages.impl.timeline.debug.EventDebugInfoNode
import com.zenobia.app.features.messages.impl.timeline.model.TimelineItem
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemAudioContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemEventContentWithAttachment
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemFileContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemImageContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemLocationContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemVideoContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemVoiceContent
import com.zenobia.app.features.messages.impl.timeline.model.event.duration
import com.zenobia.app.features.poll.api.create.CreatePollEntryPoint
import com.zenobia.app.features.poll.api.create.CreatePollMode
import com.zenobia.app.libraries.androidutils.system.DeviceHasVulkanSupport
import com.zenobia.app.libraries.architecture.BackstackWithOverlayBox
import com.zenobia.app.libraries.architecture.BaseFlowNode
import com.zenobia.app.libraries.architecture.callback
import com.zenobia.app.libraries.architecture.createNode
import com.zenobia.app.libraries.architecture.overlay.Overlay
import com.zenobia.app.libraries.architecture.overlay.operation.hide
import com.zenobia.app.libraries.architecture.overlay.operation.show
import com.zenobia.app.libraries.core.coroutine.CoroutineDispatchers
import com.zenobia.app.libraries.dateformatter.api.DateFormatter
import com.zenobia.app.libraries.dateformatter.api.DateFormatterMode
import com.zenobia.app.libraries.dateformatter.api.toHumanReadableDuration
import com.zenobia.app.libraries.di.RoomScope
import com.zenobia.app.libraries.matrix.api.core.EventId
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.core.SessionId
import com.zenobia.app.libraries.matrix.api.core.ThreadId
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.api.core.toRoomIdOrAlias
import com.zenobia.app.libraries.matrix.api.media.MediaSource
import com.zenobia.app.libraries.matrix.api.permalink.PermalinkData
import com.zenobia.app.libraries.matrix.api.room.BaseRoom
import com.zenobia.app.libraries.matrix.api.room.alias.matches
import com.zenobia.app.libraries.matrix.api.room.joinedRoomMembers
import com.zenobia.app.libraries.matrix.api.roomlist.RoomListService
import com.zenobia.app.libraries.matrix.api.timeline.Timeline
import com.zenobia.app.libraries.matrix.api.timeline.item.TimelineItemDebugInfo
import com.zenobia.app.libraries.matrix.ui.messages.RoomMemberProfilesCache
import com.zenobia.app.libraries.matrix.ui.messages.RoomNamesCache
import com.zenobia.app.libraries.mediaviewer.api.MediaInfo
import com.zenobia.app.libraries.mediaviewer.api.MediaViewerEntryPoint
import com.zenobia.app.libraries.textcomposer.mentions.LocalMentionSpanUpdater
import com.zenobia.app.libraries.textcomposer.mentions.MentionSpanTheme
import com.zenobia.app.libraries.textcomposer.mentions.MentionSpanUpdater
import com.zenobia.app.services.analytics.api.AnalyticsService
import com.zenobia.app.services.analyticsproviders.api.trackers.captureInteraction
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import kotlin.time.Duration.Companion.milliseconds

@ContributesNode(RoomScope::class)
@AssistedInject
class MessagesFlowNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val roomListService: RoomListService,
    private val sessionId: SessionId,
    private val shareLocationEntryPoint: ShareLocationEntryPoint,
    private val showLocationEntryPoint: ShowLocationEntryPoint,
    private val createPollEntryPoint: CreatePollEntryPoint,
    private val elementCallEntryPoint: ElementCallEntryPoint,
    private val mediaViewerEntryPoint: MediaViewerEntryPoint,
    private val forwardEntryPoint: ForwardEntryPoint,
    private val analyticsService: AnalyticsService,
    private val locationService: LocationService,
    private val room: BaseRoom,
    private val roomMemberProfilesCache: RoomMemberProfilesCache,
    private val roomNamesCache: RoomNamesCache,
    private val mentionSpanUpdater: MentionSpanUpdater,
    private val mentionSpanTheme: MentionSpanTheme,
    private val pinnedEventsTimelineProvider: DefaultPinnedEventsTimelineProvider,
    private val timelineController: TimelineController,
    private val knockRequestsListEntryPoint: KnockRequestsListEntryPoint,
    private val dateFormatter: DateFormatter,
    private val coroutineDispatchers: CoroutineDispatchers,
    private val hasVulkanSupport: DeviceHasVulkanSupport,
) : BaseFlowNode<MessagesFlowNode.NavTarget>(
    backstack = BackStack(
        initialElement = plugins.filterIsInstance<MessagesEntryPoint.Params>().first().initialTarget.toNavTarget(),
        savedStateMap = buildContext.savedStateMap,
    ),
    overlay = Overlay(
        savedStateMap = buildContext.savedStateMap,
    ),
    buildContext = buildContext,
    plugins = plugins,
), MessagesEntryPoint.NodeProxy {
    sealed interface NavTarget : Parcelable {
        @Parcelize
        data class Messages(val focusedEventId: EventId?) : NavTarget

        @Parcelize
        data class MediaViewer(
            val mode: MediaViewerEntryPoint.MediaViewerMode,
            val eventId: EventId?,
            val mediaInfo: MediaInfo,
            val mediaSource: MediaSource,
            val thumbnailSource: MediaSource?,
            val canUseOverlay: Boolean,
        ) : NavTarget

        @Parcelize
        data class AttachmentPreview(val timelineMode: Timeline.Mode, val attachment: Attachment, val inReplyToEventId: EventId?) : NavTarget

        @Parcelize
        data class LocationViewer(val mode: ShowLocationMode) : NavTarget

        @Parcelize
        data class EventDebugInfo(val eventId: EventId?, val debugInfo: TimelineItemDebugInfo) : NavTarget

        @Parcelize
        data class ForwardEvent(
            val eventId: EventId,
            val fromPinnedEvents: Boolean,
        ) : NavTarget

        @Parcelize
        data class ReportMessage(val eventId: EventId, val senderId: UserId) : NavTarget

        @Parcelize
        data class SendLocation(val timelineMode: Timeline.Mode) : NavTarget

        @Parcelize
        data class CreatePoll(val timelineMode: Timeline.Mode) : NavTarget

        @Parcelize
        data class EditPoll(val timelineMode: Timeline.Mode, val eventId: EventId) : NavTarget

        @Parcelize
        data object PinnedMessagesList : NavTarget

        @Parcelize
        data object KnockRequestsList : NavTarget

        @Parcelize
        data class Thread(val threadRootId: ThreadId, val focusedEventId: EventId?) : NavTarget

        @Parcelize
        data object ThreadsList : NavTarget

        @Parcelize
        data class AvatarPreview(val name: String, val avatarUrl: String) : NavTarget
    }

    private val callback: MessagesEntryPoint.Callback = callback()

    private var displayVulkanNotSupportedError by mutableStateOf(false)

    override fun onBuilt() {
        super.onBuilt()
        lifecycle.subscribe(
            onDestroy = {
                timelineController.close()
            }
        )
        setupCacheUpdaters()

        pinnedEventsTimelineProvider.launchIn(lifecycleScope)
    }

    private fun setupCacheUpdaters() {
        room.membersStateFlow
            .onEach { membersState ->
                withContext(coroutineDispatchers.computation) {
                    roomMemberProfilesCache.replace(membersState.joinedRoomMembers())
                }
            }
            .launchIn(lifecycleScope)

        roomListService
            .allRooms
            .summaries
            .onEach {
                withContext(coroutineDispatchers.computation) {
                    roomNamesCache.replace(it)
                }
            }
            .launchIn(lifecycleScope)
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            is NavTarget.Messages -> {
                val callback = object : MessagesNode.Callback {
                    override fun navigateToRoomDetails() {
                        callback.navigateToRoomDetails()
                    }

                    override fun handleEventClick(timelineMode: Timeline.Mode, event: TimelineItem.Event, canUseOverlay: Boolean): Boolean {
                        return processEventClick(
                            timelineMode = timelineMode,
                            event = event,
                            canUseOverlay = canUseOverlay,
                        )
                    }

                    override fun navigateToPreviewAttachments(attachments: ImmutableList<Attachment>, inReplyToEventId: EventId?) {
                        backstack.push(
                            NavTarget.AttachmentPreview(
                                attachment = attachments.first(),
                                timelineMode = Timeline.Mode.Live,
                                inReplyToEventId = inReplyToEventId,
                            )
                        )
                    }

                    override fun navigateToRoomMemberDetails(userId: UserId) {
                        callback.navigateToRoomMemberDetails(userId)
                    }

                    override fun handlePermalinkClick(data: PermalinkData) {
                        callback.handlePermalinkClick(data, pushToBackstack = true)
                    }

                    override fun navigateToEventDebugInfo(eventId: EventId?, debugInfo: TimelineItemDebugInfo) {
                        backstack.push(NavTarget.EventDebugInfo(eventId, debugInfo))
                    }

                    override fun forwardEvent(eventId: EventId) {
                        backstack.push(NavTarget.ForwardEvent(eventId, fromPinnedEvents = false))
                    }

                    override fun navigateToReportMessage(eventId: EventId, senderId: UserId) {
                        backstack.push(NavTarget.ReportMessage(eventId, senderId))
                    }

                    override fun navigateToSendLocation() {
                        if (hasVulkanSupport()) {
                            backstack.push(NavTarget.SendLocation(Timeline.Mode.Live))
                        } else {
                            displayVulkanNotSupportedError = true
                        }
                    }

                    override fun navigateToCreatePoll() {
                        backstack.push(NavTarget.CreatePoll(Timeline.Mode.Live))
                    }

                    override fun navigateToEditPoll(eventId: EventId) {
                        backstack.push(NavTarget.EditPoll(Timeline.Mode.Live, eventId))
                    }

                    override fun navigateToCurrentLiveLocation() {
                        if (hasVulkanSupport()) {
                            backstack.push(NavTarget.LocationViewer(ShowLocationMode.Live(senderId = sessionId)))
                        } else {
                            displayVulkanNotSupportedError = true
                        }
                    }

                    override fun navigateToRoomCall(roomId: RoomId, isAudioCall: Boolean) {
                        val callData = CallData(
                            sessionId = sessionId,
                            roomId = roomId,
                            isAudioCall = isAudioCall,
                        )
                        analyticsService.captureInteraction(Interaction.Name.MobileRoomCallButton)
                        elementCallEntryPoint.startCall(callData)
                    }

                    override fun navigateToPinnedMessagesList() {
                        backstack.push(NavTarget.PinnedMessagesList)
                    }

                    override fun navigateToKnockRequestsList() {
                        backstack.push(NavTarget.KnockRequestsList)
                    }

                    override fun navigateToThread(threadRootId: ThreadId, focusedEventId: EventId?) {
                        backstack.push(NavTarget.Thread(threadRootId, focusedEventId))
                    }

                    override fun navigateToThreadsList() {
                        backstack.push(NavTarget.ThreadsList)
                    }

                    override fun navigateToDeveloperSettings() {
                        callback.navigateToDeveloperSettings()
                    }

                    override fun navigateToAvatarPreview(username: String, avatarUrl: String) {
                        overlay.show(NavTarget.AvatarPreview(username, avatarUrl))
                    }
                }
                val inputs = MessagesNode.Inputs(focusedEventId = navTarget.focusedEventId)
                createNode<MessagesNode>(buildContext, listOf(callback, inputs))
            }
            is NavTarget.MediaViewer -> {
                val params = MediaViewerEntryPoint.Params(
                    mode = navTarget.mode,
                    eventId = navTarget.eventId,
                    mediaInfo = navTarget.mediaInfo,
                    mediaSource = navTarget.mediaSource,
                    thumbnailSource = navTarget.thumbnailSource,
                    canShowInfo = true,
                )
                val callback = object : MediaViewerEntryPoint.Callback {
                    override fun onDone() {
                        if (navTarget.canUseOverlay) {
                            overlay.hide()
                        } else {
                            backstack.pop()
                        }
                    }

                    override fun viewInTimeline(eventId: EventId) {
                        this@MessagesFlowNode.viewInTimeline(eventId)
                    }

                    override fun forwardEvent(eventId: EventId, fromPinnedEvents: Boolean) {
                        // Need to go to the parent because of the overlay
                        callback.forwardEvent(eventId, fromPinnedEvents)
                    }
                }
                mediaViewerEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    params = params,
                    callback = callback
                )
            }
            is NavTarget.AttachmentPreview -> {
                val inputs = AttachmentsPreviewNode.Inputs(
                    attachment = navTarget.attachment,
                    timelineMode = navTarget.timelineMode,
                    inReplyToEventId = navTarget.inReplyToEventId,
                )
                createNode<AttachmentsPreviewNode>(buildContext, listOf(inputs))
            }
            is NavTarget.LocationViewer -> {
                val inputs = ShowLocationEntryPoint.Inputs(navTarget.mode)
                showLocationEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    inputs = inputs,
                )
            }
            is NavTarget.EventDebugInfo -> {
                val inputs = EventDebugInfoNode.Inputs(navTarget.eventId, navTarget.debugInfo)
                createNode<EventDebugInfoNode>(buildContext, listOf(inputs))
            }
            is NavTarget.ForwardEvent -> {
                val timelineProvider = if (navTarget.fromPinnedEvents) {
                    pinnedEventsTimelineProvider
                } else {
                    timelineController
                }
                val params = ForwardEntryPoint.Params(navTarget.eventId, timelineProvider)
                val callback = object : ForwardEntryPoint.Callback {
                    override fun onDone(roomIds: List<RoomId>) {
                        backstack.pop()
                        roomIds.singleOrNull()?.let { roomId ->
                            callback.navigateToRoom(roomId)
                        }
                    }
                }
                forwardEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    params = params,
                    callback = callback,
                )
            }
            is NavTarget.ReportMessage -> {
                val inputs = ReportMessageNode.Inputs(navTarget.eventId, navTarget.senderId)
                createNode<ReportMessageNode>(buildContext, listOf(inputs))
            }
            is NavTarget.SendLocation -> {
                shareLocationEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    timelineMode = navTarget.timelineMode,
                )
            }
            is NavTarget.CreatePoll -> {
                createPollEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    params = CreatePollEntryPoint.Params(
                        timelineMode = navTarget.timelineMode,
                        mode = CreatePollMode.NewPoll
                    ),
                )
            }
            is NavTarget.EditPoll -> {
                createPollEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    params = CreatePollEntryPoint.Params(
                        timelineMode = navTarget.timelineMode,
                        mode = CreatePollMode.EditPoll(eventId = navTarget.eventId)
                    ),
                )
            }
            NavTarget.PinnedMessagesList -> {
                val callback = object : PinnedMessagesListNode.Callback {
                    override fun handleEventClick(event: TimelineItem.Event, canUseOverlay: Boolean) {
                        processEventClick(
                            timelineMode = Timeline.Mode.PinnedEvents,
                            event = event,
                            canUseOverlay = canUseOverlay,
                        )
                    }

                    override fun navigateToRoomMemberDetails(userId: UserId) {
                        callback.navigateToRoomMemberDetails(userId)
                    }

                    override fun viewInTimeline(eventId: EventId) {
                        this@MessagesFlowNode.viewInTimeline(eventId)
                    }

                    override fun handlePermalinkClick(data: PermalinkData.RoomLink) {
                        callback.handlePermalinkClick(data, pushToBackstack = !room.matches(data.roomIdOrAlias))
                    }

                    override fun navigateToEventDebugInfo(eventId: EventId?, debugInfo: TimelineItemDebugInfo) {
                        backstack.push(NavTarget.EventDebugInfo(eventId, debugInfo))
                    }

                    override fun handleForwardEventClick(eventId: EventId) {
                        backstack.push(NavTarget.ForwardEvent(eventId = eventId, fromPinnedEvents = true))
                    }

                    override fun navigateToThread(threadRootId: ThreadId) {
                        backstack.push(NavTarget.Thread(threadRootId, null))
                    }
                }
                createNode<PinnedMessagesListNode>(buildContext, plugins = listOf(callback))
            }
            NavTarget.KnockRequestsList -> {
                knockRequestsListEntryPoint.createNode(this, buildContext)
            }
            is NavTarget.Thread -> {
                val inputs = ThreadedMessagesNode.Inputs(
                    threadRootEventId = navTarget.threadRootId,
                    focusedEventId = navTarget.focusedEventId,
                )
                val callback = object : ThreadedMessagesNode.Callback {
                    override fun handleEventClick(timelineMode: Timeline.Mode, event: TimelineItem.Event, canUseOverlay: Boolean): Boolean {
                        return processEventClick(
                            timelineMode = timelineMode,
                            event = event,
                            canUseOverlay = canUseOverlay,
                        )
                    }

                    override fun navigateToPreviewAttachments(attachments: ImmutableList<Attachment>, inReplyToEventId: EventId?) {
                        backstack.push(
                            NavTarget.AttachmentPreview(
                                attachment = attachments.first(),
                                timelineMode = Timeline.Mode.Thread(navTarget.threadRootId),
                                inReplyToEventId = inReplyToEventId,
                            )
                        )
                    }

                    override fun navigateToRoomMemberDetails(userId: UserId) {
                        callback.navigateToRoomMemberDetails(userId)
                    }

                    override fun handlePermalinkClick(data: PermalinkData) {
                        callback.handlePermalinkClick(data, pushToBackstack = true)
                    }

                    override fun navigateToEventDebugInfo(eventId: EventId?, debugInfo: TimelineItemDebugInfo) {
                        backstack.push(NavTarget.EventDebugInfo(eventId, debugInfo))
                    }

                    override fun handleForwardEventClick(eventId: EventId) {
                        backstack.push(NavTarget.ForwardEvent(eventId, fromPinnedEvents = false))
                    }

                    override fun navigateToReportMessage(eventId: EventId, senderId: UserId) {
                        backstack.push(NavTarget.ReportMessage(eventId, senderId))
                    }

                    override fun navigateToSendLocation() {
                        if (hasVulkanSupport()) {
                            backstack.push(NavTarget.SendLocation(Timeline.Mode.Thread(navTarget.threadRootId)))
                        } else {
                            displayVulkanNotSupportedError = true
                        }
                    }

                    override fun navigateToCreatePoll() {
                        backstack.push(NavTarget.CreatePoll(Timeline.Mode.Thread(navTarget.threadRootId)))
                    }

                    override fun navigateToEditPoll(eventId: EventId) {
                        backstack.push(NavTarget.EditPoll(Timeline.Mode.Thread(navTarget.threadRootId), eventId))
                    }

                    override fun navigateToCurrentLiveLocation() {
                        if (hasVulkanSupport()) {
                            backstack.push(NavTarget.LocationViewer(ShowLocationMode.Live(senderId = sessionId)))
                        } else {
                            displayVulkanNotSupportedError = true
                        }
                    }

                    override fun navigateToRoomCall(roomId: RoomId, isAudioCall: Boolean) {
                        val callData = CallData(
                            sessionId = sessionId,
                            roomId = roomId,
                            isAudioCall = isAudioCall
                        )
                        analyticsService.captureInteraction(Interaction.Name.MobileRoomCallButton)
                        elementCallEntryPoint.startCall(callData)
                    }

                    override fun navigateToThread(threadRootId: ThreadId, focusedEventId: EventId?) {
                        backstack.push(NavTarget.Thread(threadRootId, focusedEventId))
                    }

                    override fun navigateToDeveloperSettings() {
                        callback.navigateToDeveloperSettings()
                    }

                    override fun navigateToAvatarPreview(username: String, avatarUrl: String) {
                        overlay.show(NavTarget.AvatarPreview(username, avatarUrl))
                    }
                }
                createNode<ThreadedMessagesNode>(buildContext, listOf(inputs, callback))
            }
            NavTarget.ThreadsList -> {
                val callback = object : ThreadsListNode.Callback {
                    override fun openThread(threadId: ThreadId) {
                        backstack.push(NavTarget.Thread(threadId, focusedEventId = null))
                    }
                }
                createNode<ThreadsListNode>(buildContext, listOf(callback))
            }
            is NavTarget.AvatarPreview -> {
                val callback = object : MediaViewerEntryPoint.Callback {
                    override fun onDone() {
                        overlay.hide()
                    }

                    override fun viewInTimeline(eventId: EventId) {
                        // Cannot happen
                    }

                    override fun forwardEvent(eventId: EventId, fromPinnedEvents: Boolean) {
                        // Cannot happen
                    }
                }
                val params = mediaViewerEntryPoint.createParamsForAvatar(
                    filename = navTarget.name,
                    avatarUrl = navTarget.avatarUrl,
                )
                mediaViewerEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    params = params,
                    callback = callback,
                )
            }
        }
    }

    private fun viewInTimeline(eventId: EventId) {
        val permalinkData = PermalinkData.RoomLink(
            roomIdOrAlias = room.roomId.toRoomIdOrAlias(),
            eventId = eventId,
        )
        callback.handlePermalinkClick(permalinkData, pushToBackstack = false)
    }

    private fun processEventClick(
        timelineMode: Timeline.Mode,
        event: TimelineItem.Event,
        canUseOverlay: Boolean,
    ): Boolean {
        val navTarget = when (event.content) {
            is TimelineItemImageContent -> {
                buildMediaViewerNavTarget(
                    mode = MediaViewerEntryPoint.MediaViewerMode.TimelineImagesAndVideos(timelineMode),
                    event = event,
                    content = event.content,
                    mediaSource = event.content.mediaSource,
                    thumbnailSource = event.content.thumbnailSource,
                    canUseOverlay = canUseOverlay,
                )
            }
            is TimelineItemVideoContent -> {
                buildMediaViewerNavTarget(
                    mode = MediaViewerEntryPoint.MediaViewerMode.TimelineImagesAndVideos(timelineMode),
                    event = event,
                    content = event.content,
                    mediaSource = event.content.mediaSource,
                    thumbnailSource = event.content.thumbnailSource,
                    canUseOverlay = canUseOverlay,
                )
            }
            is TimelineItemFileContent -> {
                buildMediaViewerNavTarget(
                    mode = MediaViewerEntryPoint.MediaViewerMode.TimelineFilesAndAudios(timelineMode),
                    event = event,
                    content = event.content,
                    mediaSource = event.content.mediaSource,
                    thumbnailSource = event.content.thumbnailSource,
                    canUseOverlay = canUseOverlay,
                )
            }
            is TimelineItemAudioContent -> {
                buildMediaViewerNavTarget(
                    mode = MediaViewerEntryPoint.MediaViewerMode.TimelineFilesAndAudios(timelineMode),
                    event = event,
                    content = event.content,
                    mediaSource = event.content.mediaSource,
                    thumbnailSource = null,
                    canUseOverlay = canUseOverlay,
                )
            }
            is TimelineItemLocationContent -> {
                if (hasVulkanSupport()) {
                    val mode = when (event.content.mode) {
                        is TimelineItemLocationContent.Mode.Live -> ShowLocationMode.Live(event.senderId)
                        is TimelineItemLocationContent.Mode.Static -> ShowLocationMode.Static(
                            location = event.content.mode.location,
                            senderName = event.safeSenderName,
                            senderId = event.senderId,
                            senderAvatarUrl = event.senderAvatar.url,
                            timestamp = event.sentTimeMillis,
                            assetType = event.content.assetType,
                        )
                    }
                    NavTarget.LocationViewer(mode = mode).takeIf { locationService.isServiceAvailable() }
                } else {
                    displayVulkanNotSupportedError = true
                    null
                }
            }
            else -> null
        }
        return when (navTarget) {
            is NavTarget.MediaViewer -> {
                if (canUseOverlay) {
                    overlay.show(navTarget)
                } else {
                    backstack.push(navTarget)
                }
                true
            }
            is NavTarget.LocationViewer -> {
                backstack.push(navTarget)
                true
            }
            else -> false
        }
    }

    private fun buildMediaViewerNavTarget(
        mode: MediaViewerEntryPoint.MediaViewerMode,
        event: TimelineItem.Event,
        content: TimelineItemEventContentWithAttachment,
        mediaSource: MediaSource,
        thumbnailSource: MediaSource?,
        canUseOverlay: Boolean,
    ): NavTarget {
        return NavTarget.MediaViewer(
            mode = mode,
            eventId = event.eventId,
            mediaInfo = MediaInfo(
                filename = content.filename,
                fileSize = content.fileSize,
                caption = content.caption,
                formattedCaption = content.formattedCaption,
                mimeType = content.mimeType,
                formattedFileSize = content.formattedFileSize,
                fileExtension = content.fileExtension,
                senderId = event.senderId,
                senderName = event.safeSenderName,
                senderAvatar = event.senderAvatar.url,
                dateSent = dateFormatter.format(
                    event.sentTimeMillis,
                    mode = DateFormatterMode.Day,
                ),
                dateSentFull = dateFormatter.format(
                    timestamp = event.sentTimeMillis,
                    mode = DateFormatterMode.Full,
                ),
                waveform = (content as? TimelineItemVoiceContent)?.waveform,
                duration = content.duration()?.toHumanReadableDuration(),
            ),
            mediaSource = mediaSource,
            thumbnailSource = thumbnailSource,
            canUseOverlay = canUseOverlay,
        )
    }

    override suspend fun attachThread(threadId: ThreadId, focusedEventId: EventId?) {
        // Wait until we have the UI for the main timeline attached
        waitForChildAttached<MessagesNode>()
        // Give some time for the items in the main timeline to be received, otherwise loading the focused thread root id won't work
        // (look at TimelineItemIndexer and firstProcessLatch for more info)
        delay(10.milliseconds)
        // Then push the new threads screen on top
        backstack.push(NavTarget.Thread(threadId, focusedEventId))
    }

    @Composable
    override fun View(modifier: Modifier) {
        mentionSpanTheme.updateStyles()

        if (displayVulkanNotSupportedError) {
            RenderingMapsNotSupportedDialog { displayVulkanNotSupportedError = false }
        }

        CompositionLocalProvider(
            LocalMentionSpanUpdater provides mentionSpanUpdater
        ) {
            BackstackWithOverlayBox(modifier)
        }
    }
}
