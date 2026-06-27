/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.joinroom.impl

import com.zenobia.app.features.joinroom.impl.di.KnockRoom
import com.zenobia.app.libraries.matrix.api.core.RoomIdOrAlias
import com.zenobia.app.tests.testutils.simulateLongTask

class FakeKnockRoom(
    var lambda: (RoomIdOrAlias, String, List<String>) -> Result<Unit> = { _, _, _ -> Result.success(Unit) }
) : KnockRoom {
    override suspend fun invoke(roomIdOrAlias: RoomIdOrAlias, message: String, serverNames: List<String>): Result<Unit> = simulateLongTask {
        lambda(roomIdOrAlias, message, serverNames)
    }
}
