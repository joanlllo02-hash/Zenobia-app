/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.location.impl.show

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bumble.appyx.core.modality.BuildContext
import com.google.common.truth.Truth.assertThat
import com.zenobia.app.features.location.api.Location
import com.zenobia.app.features.location.api.ShowLocationEntryPoint
import com.zenobia.app.features.location.api.ShowLocationMode
import com.zenobia.app.features.location.impl.common.FakeUserLocationStateFactory
import com.zenobia.app.features.location.impl.common.actions.FakeLocationActions
import com.zenobia.app.features.location.impl.common.permissions.FakePermissionsPresenter
import com.zenobia.app.features.location.test.FakeActiveLiveLocationShareManager
import com.zenobia.app.libraries.dateformatter.test.FakeDateFormatter
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.test.FakeMatrixClient
import com.zenobia.app.libraries.matrix.test.core.aBuildMeta
import com.zenobia.app.libraries.matrix.test.room.FakeJoinedRoom
import com.zenobia.app.services.analytics.test.FakeAnalyticsService
import com.zenobia.app.services.toolbox.test.strings.FakeStringProvider
import com.zenobia.app.tests.testutils.node.TestParentNode
import org.junit.Rule
import org.junit.Test

class DefaultShowLocationEntryPointTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `test node builder`() {
        val entryPoint = DefaultShowLocationEntryPoint()
        val parentNode = TestParentNode.create { buildContext, plugins ->
            val joinedRoom = FakeJoinedRoom()
            ShowLocationNode(
                buildContext = buildContext,
                plugins = plugins,
                presenterFactory = object : ShowLocationPresenter.Factory {
                    override fun create(mode: ShowLocationMode) = ShowLocationPresenter(
                        mode = mode,
                        permissionsPresenterFactory = { FakePermissionsPresenter() },
                        locationActions = FakeLocationActions(),
                        buildMeta = aBuildMeta(),
                        dateFormatter = FakeDateFormatter(),
                        stringProvider = FakeStringProvider(),
                        joinedRoom = joinedRoom,
                        client = FakeMatrixClient(),
                        liveLocationShareManager = FakeActiveLiveLocationShareManager(),
                        userLocationStateFactory = FakeUserLocationStateFactory(),
                    )
                },
                analyticsService = FakeAnalyticsService(),
            )
        }
        val inputs = ShowLocationEntryPoint.Inputs(
            mode = ShowLocationMode.Static(
                location = Location(37.4219983, -122.084, 10f),
                senderName = "Alice",
                senderId = UserId("@alice:matrix.org"),
                senderAvatarUrl = null,
                timestamp = System.currentTimeMillis(),
                assetType = null,
            ),
        )
        val result = entryPoint.createNode(
            parentNode = parentNode,
            buildContext = BuildContext.root(null),
            inputs = inputs,
        )
        assertThat(result).isInstanceOf(ShowLocationNode::class.java)
        assertThat(result.plugins).contains(inputs)
    }
}
