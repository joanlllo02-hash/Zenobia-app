/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.appnav.loggedin

import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.sync.SyncState
import com.zenobia.app.libraries.matrix.test.FakeMatrixClient
import com.zenobia.app.libraries.matrix.test.room.FakeJoinedRoom
import com.zenobia.app.libraries.matrix.test.sync.FakeSyncService
import com.zenobia.app.tests.testutils.lambda.assert
import com.zenobia.app.tests.testutils.lambda.lambdaRecorder
import com.zenobia.app.tests.testutils.lambda.value
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SendQueuesTest {
    private val matrixClient = FakeMatrixClient()
    private val syncService = FakeSyncService(initialSyncState = SyncState.Running)
    private val sut = SendQueues(matrixClient, syncService)

    @Test
    fun `test network status online and sending queue failed`() = runTest {
        val sendQueueDisabledFlow = MutableSharedFlow<RoomId>(replay = 1)
        val setAllSendQueuesEnabledLambda = lambdaRecorder { _: Boolean -> }
        matrixClient.sendQueueDisabledFlow = sendQueueDisabledFlow
        matrixClient.setAllSendQueuesEnabledLambda = setAllSendQueuesEnabledLambda
        val setRoomSendQueueEnabledLambda = lambdaRecorder { _: Boolean -> }
        val room = FakeJoinedRoom(
            setSendQueueEnabledResult = setRoomSendQueueEnabledLambda
        )
        matrixClient.givenGetRoomResult(room.roomId, room)
        sut.launchIn(backgroundScope)

        sendQueueDisabledFlow.emit(room.roomId)
        advanceTimeBy(SEND_QUEUES_RETRY_DELAY_MILLIS)
        runCurrent()

        assert(setAllSendQueuesEnabledLambda)
            .isCalledOnce()
            .with(value(true))

        assert(setRoomSendQueueEnabledLambda).isNeverCalled()
    }

    @Test
    fun `test sync state offline and sending queue failed`() = runTest {
        val sendQueueDisabledFlow = MutableSharedFlow<RoomId>(replay = 1)

        val setAllSendQueuesEnabledLambda = lambdaRecorder { _: Boolean -> }
        matrixClient.sendQueueDisabledFlow = sendQueueDisabledFlow
        matrixClient.setAllSendQueuesEnabledLambda = setAllSendQueuesEnabledLambda
        syncService.emitSyncState(SyncState.Offline)
        val setRoomSendQueueEnabledLambda = lambdaRecorder { _: Boolean -> }
        val room = FakeJoinedRoom(
            setSendQueueEnabledResult = setRoomSendQueueEnabledLambda
        )
        matrixClient.givenGetRoomResult(room.roomId, room)

        sut.launchIn(backgroundScope)

        sendQueueDisabledFlow.emit(room.roomId)
        advanceTimeBy(SEND_QUEUES_RETRY_DELAY_MILLIS)
        runCurrent()

        assert(setAllSendQueuesEnabledLambda).isNeverCalled()
        assert(setRoomSendQueueEnabledLambda).isNeverCalled()
    }
}
