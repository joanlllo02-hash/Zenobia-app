/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.leaveroom.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Inject
import com.zenobia.app.features.leaveroom.api.LeaveRoomEvent
import com.zenobia.app.features.leaveroom.api.LeaveRoomState
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.architecture.Presenter
import com.zenobia.app.libraries.architecture.runCatchingUpdatingState
import com.zenobia.app.libraries.core.coroutine.CoroutineDispatchers
import com.zenobia.app.libraries.matrix.api.MatrixClient
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.room.BaseRoom
import com.zenobia.app.libraries.matrix.api.room.RoomMember
import com.zenobia.app.libraries.matrix.api.room.powerlevels.usersWithRole
import com.zenobia.app.libraries.push.api.notifications.conversations.NotificationConversationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

@Inject
class LeaveRoomPresenter(
    private val client: MatrixClient,
    private val dispatchers: CoroutineDispatchers,
    private val notificationConversationService: NotificationConversationService,
) : Presenter<LeaveRoomState> {
    @Composable
    override fun present(): LeaveRoomState {
        val scope = rememberCoroutineScope()
        val leaveAction = remember { mutableStateOf<AsyncAction<Unit>>(AsyncAction.Uninitialized) }
        return InternalLeaveRoomState(
            leaveAction = leaveAction.value,
        ) { event ->
            when (event) {
                is LeaveRoomEvent.LeaveRoom ->
                    if (event.needsConfirmation) {
                        scope.showLeaveRoomAlert(roomId = event.roomId, leaveAction = leaveAction)
                    } else {
                        scope.leaveRoom(roomId = event.roomId, leaveAction = leaveAction)
                    }
                InternalLeaveRoomEvent.ResetState -> leaveAction.value = AsyncAction.Uninitialized
            }
        }
    }

    private fun CoroutineScope.showLeaveRoomAlert(
        roomId: RoomId,
        leaveAction: MutableState<AsyncAction<Unit>>,
    ) = launch(dispatchers.io) {
        client.getRoom(roomId)?.use { room ->
            val roomInfo = room.roomInfoFlow.first()
            leaveAction.value = when {
                roomInfo.isDm -> Confirmation.Dm(roomId)
                room.isLastOwner() && roomInfo.joinedMembersCount > 1L -> Confirmation.LastOwnerInRoom(roomId)
                // If unknown, assume the room is private
                roomInfo.isPublic == null || roomInfo.isPublic == false -> Confirmation.PrivateRoom(roomId)
                roomInfo.joinedMembersCount == 1L -> Confirmation.LastUserInRoom(roomId)
                else -> Confirmation.Generic(roomId)
            }
        }
    }

    private fun CoroutineScope.leaveRoom(
        roomId: RoomId,
        leaveAction: MutableState<AsyncAction<Unit>>,
    ) = launch(dispatchers.io) {
        leaveAction.runCatchingUpdatingState {
            client.getRoom(roomId)!!.use { room ->
                room
                    .leave()
                    .onSuccess { notificationConversationService.onLeftRoom(client.sessionId, roomId) }
                    .onFailure { Timber.e(it, "Error while leaving room ${room.roomId}") }
                    .getOrThrow()
            }
        }
    }

    private suspend fun BaseRoom.isLastOwner(): Boolean {
        if (roomInfoFlow.value.isDm) {
            // DMs are not owned by the user, so we can return false
            return false
        } else {
            val hasPrivilegedCreatorRole = roomInfoFlow.value.privilegedCreatorRole
            if (!hasPrivilegedCreatorRole) return false
            val owners = usersWithRole { role -> role is RoomMember.Role.Owner }.first()
            return owners.size == 1 && owners.first().userId == sessionId
        }
    }
}
