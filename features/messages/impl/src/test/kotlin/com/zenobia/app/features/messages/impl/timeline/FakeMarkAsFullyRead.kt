/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.timeline

import com.zenobia.app.libraries.core.extensions.runCatchingExceptions
import com.zenobia.app.libraries.matrix.api.core.EventId
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.tests.testutils.lambda.lambdaError

class FakeMarkAsFullyRead(
    private val invokeResult: (RoomId, EventId) -> Unit = { _, _ -> lambdaError() },
) : MarkAsFullyRead {
    override suspend fun invoke(roomId: RoomId, eventId: EventId): Result<Unit> {
        return runCatchingExceptions { invokeResult(roomId, eventId) }
    }
}
