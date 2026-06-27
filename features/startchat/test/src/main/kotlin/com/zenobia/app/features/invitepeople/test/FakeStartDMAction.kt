/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.invitepeople.test

import androidx.compose.runtime.MutableState
import com.zenobia.app.features.startchat.api.StartDMAction
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.user.MatrixUser
import com.zenobia.app.tests.testutils.lambda.lambdaError

class FakeStartDMAction(
    private val executeResult: (MatrixUser, Boolean, MutableState<AsyncAction<RoomId>>) -> Unit = { _, _, _ ->
        lambdaError()
    }
) : StartDMAction {
    override suspend fun execute(
        matrixUser: MatrixUser,
        createIfDmDoesNotExist: Boolean,
        actionState: MutableState<AsyncAction<RoomId>>,
    ) {
        executeResult(matrixUser, createIfDmDoesNotExist, actionState)
    }
}
