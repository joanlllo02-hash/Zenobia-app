/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.impl.mapper

import com.google.common.truth.Truth.assertThat
import com.zenobia.app.libraries.matrix.api.user.MatrixUser
import com.zenobia.app.libraries.matrix.impl.fixtures.factories.aRustUserProfile
import com.zenobia.app.libraries.matrix.test.A_USER_ID
import org.junit.Test

class UserProfileMapperTest {
    @Test
    fun map() {
        assertThat(aRustUserProfile(A_USER_ID.value, "displayName", "avatarUrl").map())
            .isEqualTo(MatrixUser(A_USER_ID, "displayName", "avatarUrl"))
    }
}
