/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.reportroom.impl

import dev.zacsweers.metro.ContributesBinding
import com.zenobia.app.libraries.di.SessionScope
import com.zenobia.app.libraries.matrix.api.MatrixClient
import com.zenobia.app.libraries.matrix.api.core.RoomId

interface ReportRoom {
    suspend operator fun invoke(
        roomId: RoomId,
        shouldReport: Boolean,
        reason: String,
        shouldLeave: Boolean,
    ): Result<Unit>

    sealed class Exception : kotlin.Exception() {
        data object RoomNotFound : Exception()
        data object LeftRoomFailed : Exception()
        data object ReportRoomFailed : Exception()
    }
}

@ContributesBinding(SessionScope::class)
class DefaultReportRoom(
    private val client: MatrixClient,
) : ReportRoom {
    override suspend operator fun invoke(
        roomId: RoomId,
        shouldReport: Boolean,
        reason: String,
        shouldLeave: Boolean
    ): Result<Unit> {
        val room = client.getRoom(roomId)
            ?: return Result.failure(ReportRoom.Exception.RoomNotFound)

        if (shouldReport) {
            room
                .reportRoom(reason.takeIf { it.isNotBlank() })
                .onFailure {
                    return Result.failure(ReportRoom.Exception.ReportRoomFailed)
                }
        }
        if (shouldLeave) {
            room
                .leave()
                .onFailure {
                    return Result.failure(ReportRoom.Exception.LeftRoomFailed)
                }
        }
        return Result.success(Unit)
    }
}
