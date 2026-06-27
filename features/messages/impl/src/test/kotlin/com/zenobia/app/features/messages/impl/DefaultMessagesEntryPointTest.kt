/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.Composable
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.testing.junit4.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import com.zenobia.app.features.call.test.FakeElementCallEntryPoint
import com.zenobia.app.features.forward.test.FakeForwardEntryPoint
import com.zenobia.app.features.knockrequests.test.FakeKnockRequestsListEntryPoint
import com.zenobia.app.features.location.test.FakeLocationService
import com.zenobia.app.features.location.test.FakeShareLocationEntryPoint
import com.zenobia.app.features.location.test.FakeShowLocationEntryPoint
import com.zenobia.app.features.messages.api.MessagesEntryPoint
import com.zenobia.app.features.messages.impl.pinned.banner.createPinnedEventsTimelineProvider
import com.zenobia.app.features.messages.impl.timeline.createTimelineController
import com.zenobia.app.features.poll.test.create.FakeCreatePollEntryPoint
import com.zenobia.app.libraries.androidutils.system.DeviceHasVulkanSupport
import com.zenobia.app.libraries.dateformatter.test.FakeDateFormatter
import com.zenobia.app.libraries.matrix.api.core.EventId
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.api.permalink.PermalinkData
import com.zenobia.app.libraries.matrix.test.AN_EVENT_ID
import com.zenobia.app.libraries.matrix.test.A_SESSION_ID
import com.zenobia.app.libraries.matrix.test.A_USER_ID
import com.zenobia.app.libraries.matrix.test.room.FakeBaseRoom
import com.zenobia.app.libraries.matrix.test.roomlist.FakeRoomListService
import com.zenobia.app.libraries.matrix.ui.messages.RoomMemberProfilesCache
import com.zenobia.app.libraries.matrix.ui.messages.RoomNamesCache
import com.zenobia.app.libraries.mediaviewer.test.FakeMediaViewerEntryPoint
import com.zenobia.app.libraries.textcomposer.mentions.MentionSpanTheme
import com.zenobia.app.libraries.textcomposer.mentions.MentionSpanUpdater
import com.zenobia.app.services.analytics.test.FakeAnalyticsService
import com.zenobia.app.tests.testutils.lambda.lambdaError
import com.zenobia.app.tests.testutils.node.TestParentNode
import com.zenobia.app.tests.testutils.testCoroutineDispatchers
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class DefaultMessagesEntryPointTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `test node builder`() = runTest {
        val entryPoint = DefaultMessagesEntryPoint()
        val parentNode = TestParentNode.create { buildContext, plugins ->
            MessagesFlowNode(
                buildContext = buildContext,
                plugins = plugins,
                roomListService = FakeRoomListService(),
                sessionId = A_SESSION_ID,
                shareLocationEntryPoint = FakeShareLocationEntryPoint(),
                showLocationEntryPoint = FakeShowLocationEntryPoint(),
                createPollEntryPoint = FakeCreatePollEntryPoint(),
                elementCallEntryPoint = FakeElementCallEntryPoint(),
                mediaViewerEntryPoint = FakeMediaViewerEntryPoint(),
                forwardEntryPoint = FakeForwardEntryPoint(),
                analyticsService = FakeAnalyticsService(),
                locationService = FakeLocationService(),
                room = FakeBaseRoom(),
                roomMemberProfilesCache = RoomMemberProfilesCache(),
                roomNamesCache = RoomNamesCache(),
                mentionSpanUpdater = object : MentionSpanUpdater {
                    override fun updateMentionSpans(text: CharSequence) = text

                    @Composable
                    override fun rememberMentionSpans(text: CharSequence) = text
                },
                mentionSpanTheme = MentionSpanTheme(A_USER_ID),
                pinnedEventsTimelineProvider = createPinnedEventsTimelineProvider(),
                timelineController = createTimelineController(),
                knockRequestsListEntryPoint = FakeKnockRequestsListEntryPoint(),
                dateFormatter = FakeDateFormatter(),
                coroutineDispatchers = testCoroutineDispatchers(),
                hasVulkanSupport = DeviceHasVulkanSupport(mockk(relaxed = true))
            )
        }
        val callback = object : MessagesEntryPoint.Callback {
            override fun navigateToRoomDetails() = lambdaError()
            override fun navigateToRoomMemberDetails(userId: UserId) = lambdaError()
            override fun handlePermalinkClick(data: PermalinkData, pushToBackstack: Boolean) = lambdaError()
            override fun forwardEvent(eventId: EventId, fromPinnedEvents: Boolean) = lambdaError()
            override fun navigateToRoom(roomId: RoomId) = lambdaError()
            override fun navigateToDeveloperSettings() = lambdaError()
        }
        val initialTarget = MessagesEntryPoint.InitialTarget.Messages(focusedEventId = AN_EVENT_ID)
        val params = MessagesEntryPoint.Params(initialTarget)
        val result = entryPoint.createNode(
            parentNode = parentNode,
            buildContext = BuildContext.root(null),
            params = params,
            callback = callback,
        )
        assertThat(result).isInstanceOf(MessagesFlowNode::class.java)
        assertThat(result.plugins).contains(MessagesEntryPoint.Params(initialTarget))
        assertThat(result.plugins).contains(callback)
    }

    @Test
    fun `test initial target to nav target mapping`() {
        assertThat(MessagesEntryPoint.InitialTarget.Messages(focusedEventId = AN_EVENT_ID).toNavTarget())
            .isEqualTo(MessagesFlowNode.NavTarget.Messages(focusedEventId = AN_EVENT_ID))
        assertThat(MessagesEntryPoint.InitialTarget.PinnedMessages.toNavTarget())
            .isEqualTo(MessagesFlowNode.NavTarget.PinnedMessagesList)
    }
}
