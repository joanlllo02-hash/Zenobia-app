/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.pushstore.api

import com.zenobia.app.libraries.matrix.api.core.SessionId

/**
 * Store data related to push about a user.
 */
interface UserPushStoreFactory {
    fun getOrCreate(userId: SessionId): UserPushStore
}
