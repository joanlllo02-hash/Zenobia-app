/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.share.impl

import android.net.Uri
import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.zenobia.app.features.share.api.OnSharedData
import com.zenobia.app.features.share.api.ShareIntentData
import com.zenobia.app.features.share.api.UriToShare
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.core.mimetype.MimeTypes
import com.zenobia.app.libraries.matrix.api.MatrixClient
import com.zenobia.app.libraries.matrix.test.A_MESSAGE
import com.zenobia.app.libraries.matrix.test.A_ROOM_ID
import com.zenobia.app.libraries.matrix.test.FakeMatrixClient
import com.zenobia.app.libraries.matrix.test.room.FakeJoinedRoom
import com.zenobia.app.libraries.matrix.test.timeline.FakeTimeline
import com.zenobia.app.libraries.mediaupload.api.MediaOptimizationConfigProvider
import com.zenobia.app.libraries.mediaupload.api.MediaSenderRoomFactory
import com.zenobia.app.libraries.mediaupload.test.FakeMediaOptimizationConfigProvider
import com.zenobia.app.libraries.mediaupload.test.FakeMediaSender
import com.zenobia.app.services.appnavstate.api.ActiveRoomsHolder
import com.zenobia.app.services.appnavstate.impl.DefaultActiveRoomsHolder
import com.zenobia.app.tests.testutils.WarmUpRule
import com.zenobia.app.tests.testutils.lambda.lambdaRecorder
import com.zenobia.app.tests.testutils.robolectric.RobolectricTest
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class SharePresenterTest : RobolectricTest() {
    @get:Rule
    val warmUpRule = WarmUpRule()

    @Test
    fun `present - initial state`() = runTest {
        val presenter = createSharePresenter()
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitItem()
            assertThat(initialState.shareAction.isUninitialized()).isTrue()
        }
    }

    @Test
    fun `present - on room selected error then clear error`() = runTest {
        val presenter = createSharePresenter()
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitItem()
            assertThat(initialState.shareAction.isUninitialized()).isTrue()
            presenter.onRoomSelected(listOf(A_ROOM_ID))
            assertThat(awaitItem().shareAction.isLoading()).isTrue()
            val failure = awaitItem()
            assertThat(failure.shareAction.isFailure()).isTrue()
            failure.eventSink.invoke(ShareEvents.ClearError)
            assertThat(awaitItem().shareAction.isUninitialized()).isTrue()
        }
    }

    @Test
    fun `present - on room selected ok`() = runTest {
        val joinedRoom = FakeJoinedRoom(
            liveTimeline = FakeTimeline().apply {
                sendMessageLambda = { _, _, _, _, _ -> Result.success(Unit) }
            },
        )
        val matrixClient = FakeMatrixClient().apply {
            givenGetRoomResult(A_ROOM_ID, joinedRoom)
        }
        val presenter = createSharePresenter(
            matrixClient = matrixClient,
            shareIntentData = ShareIntentData.PlainText(A_MESSAGE),
        )
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitItem()
            assertThat(initialState.shareAction.isUninitialized()).isTrue()
            presenter.onRoomSelected(listOf(A_ROOM_ID))
            assertThat(awaitItem().shareAction.isLoading()).isTrue()
            val success = awaitItem()
            assertThat(success.shareAction.isSuccess()).isTrue()
            assertThat(success.shareAction).isEqualTo(AsyncAction.Success(listOf(A_ROOM_ID)))
        }
    }

    @Test
    fun `present - send text ok`() = runTest {
        val joinedRoom = FakeJoinedRoom(
            liveTimeline = FakeTimeline().apply {
                sendMessageLambda = { _, _, _, _, _ -> Result.success(Unit) }
            },
        )
        val matrixClient = FakeMatrixClient().apply {
            givenGetRoomResult(A_ROOM_ID, joinedRoom)
        }
        val presenter = createSharePresenter(
            matrixClient = matrixClient,
            shareIntentData = ShareIntentData.PlainText(A_MESSAGE),
        )
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitItem()
            assertThat(initialState.shareAction.isUninitialized()).isTrue()
            presenter.onRoomSelected(listOf(A_ROOM_ID))
            assertThat(awaitItem().shareAction.isLoading()).isTrue()
            val success = awaitItem()
            assertThat(success.shareAction.isSuccess()).isTrue()
            assertThat(success.shareAction).isEqualTo(AsyncAction.Success(listOf(A_ROOM_ID)))
        }
    }

    @Test
    fun `present - send media ok`() = runTest {
        val sendMediaResult = lambdaRecorder<Result<Unit>> { Result.success(Unit) }
        val joinedRoom = FakeJoinedRoom(
            liveTimeline = FakeTimeline(),
        )
        val matrixClient = FakeMatrixClient().apply {
            givenGetRoomResult(A_ROOM_ID, joinedRoom)
        }
        val mediaSender = FakeMediaSender(
            sendMediaResult = sendMediaResult,
        )
        val presenter = createSharePresenter(
            matrixClient = matrixClient,
            shareIntentData = ShareIntentData.Uris(
                text = A_MESSAGE,
                listOf(
                    UriToShare(
                        uri = Uri.parse("content://image.jpg"),
                        mimeType = MimeTypes.Jpeg,
                    )
                )
            ),
            mediaSenderRoomFactory = MediaSenderRoomFactory { mediaSender },
        )
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitItem()
            assertThat(initialState.shareAction.isUninitialized()).isTrue()
            presenter.onRoomSelected(listOf(A_ROOM_ID))
            assertThat(awaitItem().shareAction.isLoading()).isTrue()
            val success = awaitItem()
            assertThat(success.shareAction.isSuccess()).isTrue()
            assertThat(success.shareAction).isEqualTo(AsyncAction.Success(listOf(A_ROOM_ID)))
            sendMediaResult.assertions().isCalledOnce()
        }
    }
}

internal fun TestScope.createSharePresenter(
    shareIntentData: ShareIntentData = ShareIntentData.PlainText(A_MESSAGE),
    matrixClient: MatrixClient = FakeMatrixClient(),
    activeRoomsHolder: ActiveRoomsHolder = DefaultActiveRoomsHolder(),
    mediaSenderRoomFactory: MediaSenderRoomFactory = MediaSenderRoomFactory { FakeMediaSender() },
    mediaOptimizationConfigProvider: MediaOptimizationConfigProvider = FakeMediaOptimizationConfigProvider(),
    onSharedData: OnSharedData = OnSharedData {},
): SharePresenter {
    return SharePresenter(
        shareIntentData = shareIntentData,
        sessionCoroutineScope = this,
        matrixClient = matrixClient,
        activeRoomsHolder = activeRoomsHolder,
        mediaSenderRoomFactory = mediaSenderRoomFactory,
        mediaOptimizationConfigProvider = mediaOptimizationConfigProvider,
        onSharedData = onSharedData,
    )
}
