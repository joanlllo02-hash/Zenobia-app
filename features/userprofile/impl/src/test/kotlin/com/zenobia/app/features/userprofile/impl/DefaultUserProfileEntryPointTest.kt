/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.userprofile.impl

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.testing.junit4.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import com.zenobia.app.features.call.test.FakeElementCallEntryPoint
import com.zenobia.app.features.userprofile.api.UserProfileEntryPoint
import com.zenobia.app.features.verifysession.test.FakeOutgoingVerificationEntryPoint
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.test.A_SESSION_ID
import com.zenobia.app.libraries.matrix.test.A_USER_ID
import com.zenobia.app.libraries.mediaviewer.test.FakeMediaViewerEntryPoint
import com.zenobia.app.tests.testutils.lambda.lambdaError
import com.zenobia.app.tests.testutils.node.TestParentNode
import org.junit.Rule
import org.junit.Test

class DefaultUserProfileEntryPointTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `test node builder`() {
        val entryPoint = DefaultUserProfileEntryPoint()

        val parentNode = TestParentNode.create { buildContext, plugins ->
            UserProfileFlowNode(
                buildContext = buildContext,
                plugins = plugins,
                sessionId = A_SESSION_ID,
                elementCallEntryPoint = FakeElementCallEntryPoint(),
                mediaViewerEntryPoint = FakeMediaViewerEntryPoint(),
                outgoingVerificationEntryPoint = FakeOutgoingVerificationEntryPoint(),
            )
        }
        val callback = object : UserProfileEntryPoint.Callback {
            override fun navigateToRoom(roomId: RoomId) {
                lambdaError()
            }
        }
        val params = UserProfileEntryPoint.Params(
            userId = A_USER_ID,
        )
        val result = entryPoint.createNode(
            parentNode = parentNode,
            buildContext = BuildContext.root(null),
            params = params,
            callback = callback,
        )
        assertThat(result).isInstanceOf(UserProfileFlowNode::class.java)
        assertThat(result.plugins).contains(params)
        assertThat(result.plugins).contains(callback)
    }
}
