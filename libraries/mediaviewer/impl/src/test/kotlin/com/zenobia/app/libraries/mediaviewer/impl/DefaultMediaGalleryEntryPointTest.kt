/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.mediaviewer.impl

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.testing.junit4.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import com.zenobia.app.libraries.matrix.api.core.EventId
import com.zenobia.app.libraries.mediaviewer.api.MediaGalleryEntryPoint
import com.zenobia.app.libraries.mediaviewer.impl.gallery.root.MediaGalleryFlowNode
import com.zenobia.app.libraries.mediaviewer.test.FakeMediaViewerEntryPoint
import com.zenobia.app.tests.testutils.lambda.lambdaError
import com.zenobia.app.tests.testutils.node.TestParentNode
import org.junit.Rule
import org.junit.Test

class DefaultMediaGalleryEntryPointTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `test node builder`() {
        val entryPoint = DefaultMediaGalleryEntryPoint()
        val parentNode = TestParentNode.create { buildContext, plugins ->
            MediaGalleryFlowNode(
                buildContext = buildContext,
                plugins = plugins,
                mediaViewerEntryPoint = FakeMediaViewerEntryPoint(),
            )
        }
        val callback = object : MediaGalleryEntryPoint.Callback {
            override fun onBackClick() = lambdaError()
            override fun viewInTimeline(eventId: EventId) = lambdaError()
            override fun forward(eventId: EventId, fromPinnedEvents: Boolean) = lambdaError()
        }
        val result = entryPoint.createNode(
            parentNode = parentNode,
            buildContext = BuildContext.root(null),
            callback = callback,
        )
        assertThat(result).isInstanceOf(MediaGalleryFlowNode::class.java)
        assertThat(result.plugins).contains(callback)
    }
}
