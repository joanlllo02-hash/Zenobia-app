/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.home.impl

import com.bumble.appyx.core.modality.BuildContext
import com.google.common.truth.Truth.assertThat
import com.zenobia.app.features.home.api.HomeEntryPoint
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.room.JoinedRoom
import com.zenobia.app.libraries.matrix.test.FakeMatrixClient
import com.zenobia.app.services.analytics.test.FakeAnalyticsService
import com.zenobia.app.tests.testutils.lambda.lambdaError
import com.zenobia.app.tests.testutils.node.TestParentNode
import com.zenobia.app.tests.testutils.robolectric.RobolectricTest
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultHomeEntryPointTest : RobolectricTest() {
    @Test
    fun `test node builder`() = runTest {
        val entryPoint = DefaultHomeEntryPoint()
        val parentNode = TestParentNode.create { buildContext, plugins ->
            HomeFlowNode(
                buildContext = buildContext,
                plugins = plugins,
                matrixClient = FakeMatrixClient(),
                presenter = createHomePresenter(),
                inviteFriendsUseCase = { lambdaError() },
                analyticsService = FakeAnalyticsService(),
                acceptDeclineInviteView = { _, _, _, _ -> lambdaError() },
                directLogoutView = { _ -> lambdaError() },
                reportRoomEntryPoint = { _, _, _ -> lambdaError() },
                declineInviteAndBlockUserEntryPoint = { _, _, _ -> lambdaError() },
                changeRoomMemberRolesEntryPoint = { _, _, _, _ -> lambdaError() },
                leaveRoomRenderer = { _, _, _ -> lambdaError() },
                sessionCoroutineScope = backgroundScope,
            )
        }
        val callback = object : HomeEntryPoint.Callback {
            override fun navigateToRoom(roomId: RoomId, joinedRoom: JoinedRoom?) = lambdaError()
            override fun navigateToCreateRoom() = lambdaError()
            override fun navigateToCreateSpace() = lambdaError()
            override fun navigateToSettings() = lambdaError()
            override fun navigateToSetUpRecovery() = lambdaError()
            override fun navigateToEnterRecoveryKey() = lambdaError()
            override fun navigateToRoomSettings(roomId: RoomId) = lambdaError()
            override fun navigateToBugReport() = lambdaError()
        }
        val result = entryPoint.createNode(
            parentNode = parentNode,
            buildContext = BuildContext.root(null),
            callback = callback,
        )
        assertThat(result).isInstanceOf(HomeFlowNode::class.java)
        assertThat(result.plugins).contains(callback)
    }
}
