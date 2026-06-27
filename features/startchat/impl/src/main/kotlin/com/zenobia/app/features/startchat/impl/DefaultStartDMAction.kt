/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.startchat.impl

import androidx.compose.runtime.MutableState
import dev.zacsweers.metro.ContributesBinding
import im.vector.app.features.analytics.plan.CreatedRoom
import com.zenobia.app.features.startchat.api.ConfirmingStartDmWithMatrixUser
import com.zenobia.app.features.startchat.api.StartDMAction
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.di.SessionScope
import com.zenobia.app.libraries.matrix.api.MatrixClient
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.room.StartDMResult
import com.zenobia.app.libraries.matrix.api.room.startDM
import com.zenobia.app.libraries.matrix.api.user.MatrixUser
import com.zenobia.app.services.analytics.api.AnalyticsService

@ContributesBinding(SessionScope::class)
class DefaultStartDMAction(
    private val matrixClient: MatrixClient,
    private val analyticsService: AnalyticsService,
) : StartDMAction {
    override suspend fun execute(
        matrixUser: MatrixUser,
        createIfDmDoesNotExist: Boolean,
        actionState: MutableState<AsyncAction<RoomId>>,
    ) {
        actionState.value = AsyncAction.Loading
        when (val result = matrixClient.startDM(matrixUser.userId, createIfDmDoesNotExist)) {
            is StartDMResult.Success -> {
                if (result.isNew) {
                    analyticsService.capture(CreatedRoom(isDM = true))
                }
                actionState.value = AsyncAction.Success(result.roomId)
            }
            is StartDMResult.Failure -> {
                actionState.value = AsyncAction.Failure(result.throwable)
            }
            StartDMResult.DmDoesNotExist -> {
                val identityState = matrixClient.encryptionService.getUserIdentity(matrixUser.userId, fallbackToServer = false).getOrNull()
                actionState.value = ConfirmingStartDmWithMatrixUser(
                    matrixUser = matrixUser,
                    isUserIdentityUnknown = identityState == null
                )
            }
        }
    }
}
