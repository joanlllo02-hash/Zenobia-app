/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.reportroom.impl

import com.google.common.truth.Truth.assertThat
import com.zenobia.app.features.reportroom.impl.fakes.FakeReportRoom
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.test.A_ROOM_ID
import com.zenobia.app.tests.testutils.lambda.any
import com.zenobia.app.tests.testutils.lambda.assert
import com.zenobia.app.tests.testutils.lambda.lambdaRecorder
import com.zenobia.app.tests.testutils.lambda.value
import com.zenobia.app.tests.testutils.test
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ReportRoomPresenterTest {
    @Test
    fun `present - initial state`() = runTest {
        val presenter = createReportRoomPresenter()
        presenter.test {
            awaitItem().also { state ->
                assertThat(state.reason).isEmpty()
                assertThat(state.leaveRoom).isFalse()
                assertThat(state.reportAction).isEqualTo(AsyncAction.Uninitialized)
                assertThat(state.canReport).isFalse()
            }
        }
    }

    @Test
    fun `present - update form values`() = runTest {
        val presenter = createReportRoomPresenter()
        presenter.test {
            awaitItem().also { state ->
                assertThat(state.reason).isEmpty()
                assertThat(state.canReport).isFalse()
                assertThat(state.leaveRoom).isFalse()
                state.eventSink(ReportRoomEvents.UpdateReason("Spam"))
            }
            awaitItem().also { state ->
                assertThat(state.reason).isEqualTo("Spam")
                assertThat(state.canReport).isTrue()
                assertThat(state.leaveRoom).isFalse()
                state.eventSink(ReportRoomEvents.ToggleLeaveRoom)
            }
            awaitItem().also { state ->
                assertThat(state.leaveRoom).isTrue()
                assertThat(state.canReport).isTrue()
                assertThat(state.canReport).isTrue()
            }
        }
    }

    @Test
    fun `present - report room success`() = runTest {
        val roomId = A_ROOM_ID
        val reportRoomLambda = lambdaRecorder<RoomId, Boolean, String, Boolean, Result<Unit>> { _, _, _, _ -> Result.success(Unit) }
        val reportRoom = FakeReportRoom(
            lambda = reportRoomLambda
        )
        val presenter = createReportRoomPresenter(roomId = roomId, reportRoom = reportRoom)
        presenter.test {
            awaitItem().eventSink(ReportRoomEvents.ToggleLeaveRoom)
            awaitItem().eventSink(ReportRoomEvents.Report)
            awaitItem().also { state ->
                assertThat(state.reportAction).isInstanceOf(AsyncAction.Loading::class.java)
            }
            awaitItem().also { state ->
                assertThat(state.reportAction).isInstanceOf(AsyncAction.Success::class.java)
            }
            assert(reportRoomLambda)
                .isCalledOnce()
                .with(value(roomId), value(true), any(), value(true))
        }
    }

    @Test
    fun `present - report failed`() = runTest {
        val roomId = A_ROOM_ID
        val reportRoomLambda = lambdaRecorder<RoomId, Boolean, String, Boolean, Result<Unit>> { _, _, _, _ ->
            Result.failure(ReportRoom.Exception.ReportRoomFailed)
        }
        val reportRoom = FakeReportRoom(
            lambda = reportRoomLambda
        )
        val presenter = createReportRoomPresenter(roomId = roomId, reportRoom = reportRoom)
        presenter.test {
            awaitItem().eventSink(ReportRoomEvents.Report)
            awaitItem().also { state ->
                assertThat(state.reportAction).isInstanceOf(AsyncAction.Loading::class.java)
            }
            awaitItem().also { state ->
                assertThat(state.reportAction).isInstanceOf(AsyncAction.Failure::class.java)
            }
            assert(reportRoomLambda)
                .isCalledOnce()
                .with(value(roomId), value(true), any(), any())
        }
    }

    @Test
    fun `present - leave room failed after report room success`() = runTest {
        val roomId = A_ROOM_ID
        val reportRoomLambda = lambdaRecorder<RoomId, Boolean, String, Boolean, Result<Unit>> { _, _, _, _ ->
            Result.failure(ReportRoom.Exception.LeftRoomFailed)
        }
        val reportRoom = FakeReportRoom(
            lambda = reportRoomLambda
        )
        val presenter = createReportRoomPresenter(roomId = roomId, reportRoom = reportRoom)
        presenter.test {
            awaitItem().eventSink(ReportRoomEvents.ToggleLeaveRoom)
            awaitItem().eventSink(ReportRoomEvents.Report)
            awaitItem().also { state ->
                assertThat(state.reportAction).isInstanceOf(AsyncAction.Loading::class.java)
            }
            awaitItem().also { state ->
                assertThat(state.reportAction).isInstanceOf(AsyncAction.Failure::class.java)
                state.eventSink(ReportRoomEvents.Report)
            }
            awaitItem().also { state ->
                assertThat(state.reportAction).isInstanceOf(AsyncAction.Loading::class.java)
            }
            awaitItem().also { state ->
                assertThat(state.reportAction).isInstanceOf(AsyncAction.Failure::class.java)
            }
            assert(reportRoomLambda)
                .isCalledExactly(2)
                .withSequence(
                    // The first call should report the room and try leaving it
                    listOf(value(roomId), value(true), any(), value(true)),
                    // The second call should not report the room again
                    listOf(value(roomId), value(false), any(), value(true))
                )
        }
    }
}

internal fun createReportRoomPresenter(
    roomId: RoomId = A_ROOM_ID,
    reportRoom: ReportRoom = FakeReportRoom()
): ReportRoomPresenter {
    return ReportRoomPresenter(roomId, reportRoom)
}
