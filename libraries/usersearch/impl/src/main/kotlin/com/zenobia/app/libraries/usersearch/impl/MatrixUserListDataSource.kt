/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.usersearch.impl

import dev.zacsweers.metro.ContributesBinding
import com.zenobia.app.libraries.di.SessionScope
import com.zenobia.app.libraries.matrix.api.MatrixClient
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.api.user.MatrixUser
import com.zenobia.app.libraries.usersearch.api.UserListDataSource

@ContributesBinding(SessionScope::class)
class MatrixUserListDataSource(
    private val client: MatrixClient
) : UserListDataSource {
    override suspend fun search(query: String, count: Long): List<MatrixUser> {
        val res = client.searchUsers(query, count)
        return res.getOrNull()?.results.orEmpty()
    }

    override suspend fun getProfile(userId: UserId): MatrixUser? {
        return client.getProfile(userId).getOrNull()
    }
}
