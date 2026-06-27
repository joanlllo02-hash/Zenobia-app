/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.location.impl.share

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bumble.appyx.core.modality.BuildContext
import com.google.common.truth.Truth.assertThat
import com.zenobia.app.features.location.impl.common.FakeUserLocationStateFactory
import com.zenobia.app.features.location.impl.common.actions.FakeLocationActions
import com.zenobia.app.features.location.impl.common.permissions.FakePermissionsPresenter
import com.zenobia.app.features.location.impl.live.LiveLocationStore
import com.zenobia.app.features.location.test.FakeActiveLiveLocationShareManager
import com.zenobia.app.features.messages.test.FakeMessageComposerContext
import com.zenobia.app.libraries.dateformatter.test.FakeDurationFormatter
import com.zenobia.app.libraries.matrix.api.timeline.Timeline
import com.zenobia.app.libraries.matrix.test.FakeMatrixClient
import com.zenobia.app.libraries.matrix.test.core.aBuildMeta
import com.zenobia.app.libraries.matrix.test.room.FakeJoinedRoom
import com.zenobia.app.libraries.preferences.test.FakePreferenceDataStoreFactory
import com.zenobia.app.services.analytics.test.FakeAnalyticsService
import com.zenobia.app.tests.testutils.node.TestParentNode
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class DefaultShareLocationEntryPointTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `test node builder`() = runTest {
        val entryPoint = DefaultShareLocationEntryPoint()
        val parentNode = TestParentNode.create { buildContext, plugins ->
            val room = FakeJoinedRoom()
            ShareLocationNode(
                buildContext = buildContext,
                plugins = plugins,
                presenterFactory = { timelineMode: Timeline.Mode ->
                    ShareLocationPresenter(
                        permissionsPresenterFactory = { FakePermissionsPresenter() },
                        room = room,
                        timelineMode = timelineMode,
                        analyticsService = FakeAnalyticsService(),
                        messageComposerContext = FakeMessageComposerContext(),
                        locationActions = FakeLocationActions(),
                        buildMeta = aBuildMeta(),
                        client = FakeMatrixClient(),
                        durationFormatter = FakeDurationFormatter(),
                        liveLocationShareManager = FakeActiveLiveLocationShareManager(),
                        liveLocationStore = LiveLocationStore(
                            preferenceDataStoreFactory = FakePreferenceDataStoreFactory(),
                            sessionId = room.sessionId,
                        ),
                        userLocationStateFactory = FakeUserLocationStateFactory(),
                    )
                },
                analyticsService = FakeAnalyticsService(),
            )
        }
        val timelineMode = Timeline.Mode.Live
        val result = entryPoint.createNode(
            parentNode = parentNode,
            buildContext = BuildContext.root(null),
            timelineMode = timelineMode,
        )
        assertThat(result).isInstanceOf(ShareLocationNode::class.java)
        assertThat(result.plugins).contains(ShareLocationNode.Inputs(timelineMode))
    }
}
