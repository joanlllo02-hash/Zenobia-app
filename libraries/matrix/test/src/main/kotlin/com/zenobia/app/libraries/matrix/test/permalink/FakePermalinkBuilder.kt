/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.test.permalink

import com.zenobia.app.libraries.matrix.api.core.RoomAlias
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.api.permalink.PermalinkBuilder
import com.zenobia.app.tests.testutils.lambda.lambdaError

class FakePermalinkBuilder(
    private val permalinkForUserLambda: (UserId) -> Result<String> = { lambdaError() },
    private val permalinkForRoomAliasLambda: (RoomAlias) -> Result<String> = { lambdaError() },
) : PermalinkBuilder {
    override fun permalinkForUser(userId: UserId): Result<String> {
        return permalinkForUserLambda(userId)
    }

    override fun permalinkForRoomAlias(roomAlias: RoomAlias): Result<String> {
        return permalinkForRoomAliasLambda(roomAlias)
    }
}
