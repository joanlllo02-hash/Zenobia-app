/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.pushstore.test.userpushstore

import com.zenobia.app.libraries.matrix.api.core.SessionId
import com.zenobia.app.libraries.pushstore.api.UserPushStore
import com.zenobia.app.libraries.pushstore.api.UserPushStoreFactory

class FakeUserPushStoreFactory(
    val userPushStore: (SessionId) -> UserPushStore = { FakeUserPushStore() }
) : UserPushStoreFactory {
    override fun getOrCreate(userId: SessionId): UserPushStore {
        return userPushStore(userId)
    }
}
