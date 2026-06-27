/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.recentemojis.impl

import dev.zacsweers.metro.ContributesBinding
import com.zenobia.app.libraries.core.coroutine.CoroutineDispatchers
import com.zenobia.app.libraries.di.SessionScope
import com.zenobia.app.libraries.matrix.api.MatrixClient
import com.zenobia.app.libraries.recentemojis.api.AddRecentEmoji
import kotlinx.coroutines.withContext

@ContributesBinding(SessionScope::class)
class DefaultAddRecentEmoji(
    private val client: MatrixClient,
    private val dispatchers: CoroutineDispatchers,
) : AddRecentEmoji {
    override suspend operator fun invoke(emoji: String): Result<Unit> = withContext(dispatchers.io) {
        client.addRecentEmoji(emoji)
    }
}
