/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.impl.mapper

import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.api.user.MatrixUser
import org.matrix.rustcomponents.sdk.UserProfile

fun UserProfile.map() = MatrixUser(
    userId = UserId(userId),
    displayName = displayName,
    avatarUrl = avatarUrl,
)
