/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.invite.impl

import com.zenobia.app.features.invite.api.SeenInvitesStore
import com.zenobia.app.libraries.matrix.api.core.SessionId
import kotlinx.coroutines.CoroutineScope

interface SeenInvitesStoreFactory {
    fun getOrCreate(
        sessionId: SessionId,
        sessionCoroutineScope: CoroutineScope,
    ): SeenInvitesStore
}
