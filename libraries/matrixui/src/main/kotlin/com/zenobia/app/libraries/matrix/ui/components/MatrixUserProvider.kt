/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.ui.components

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.zenobia.app.libraries.designsystem.preview.USER_NAME_ALICE
import com.zenobia.app.libraries.designsystem.preview.USER_NAME_BOB
import com.zenobia.app.libraries.designsystem.preview.USER_NAME_CAROL
import com.zenobia.app.libraries.designsystem.preview.USER_NAME_DAVID
import com.zenobia.app.libraries.designsystem.preview.USER_NAME_EVE
import com.zenobia.app.libraries.designsystem.preview.USER_NAME_JOHN_DOE
import com.zenobia.app.libraries.designsystem.preview.USER_NAME_JUSTIN
import com.zenobia.app.libraries.designsystem.preview.USER_NAME_MALLORY
import com.zenobia.app.libraries.designsystem.preview.USER_NAME_SUSIE
import com.zenobia.app.libraries.designsystem.preview.USER_NAME_VICTOR
import com.zenobia.app.libraries.designsystem.preview.USER_NAME_WALTER
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.api.user.MatrixUser

open class MatrixUserProvider : PreviewParameterProvider<MatrixUser> {
    override val values: Sequence<MatrixUser>
        get() = sequenceOf(
            aMatrixUser(),
            aMatrixUser(displayName = null),
        )
}

open class MatrixUserWithAvatarProvider : PreviewParameterProvider<MatrixUser?> {
    override val values: Sequence<MatrixUser?>
        get() = sequenceOf(
            aMatrixUser(displayName = USER_NAME_JOHN_DOE),
            aMatrixUser(displayName = USER_NAME_JOHN_DOE, avatarUrl = "anUrl"),
        )
}

fun aMatrixUser(
    id: String? = null,
    displayName: String? = USER_NAME_ALICE,
    avatarUrl: String? = null,
) = MatrixUser(
    userId = UserId(id ?: "@${displayName?.lowercase()?.replace(" ", "_") ?: "id"}:server.org"),
    displayName = displayName,
    avatarUrl = avatarUrl,
)

fun aMatrixUserList() = listOf(
    aMatrixUser(displayName = USER_NAME_ALICE),
    aMatrixUser(displayName = USER_NAME_BOB),
    aMatrixUser(displayName = USER_NAME_CAROL),
    aMatrixUser(displayName = USER_NAME_DAVID),
    aMatrixUser(displayName = USER_NAME_EVE),
    aMatrixUser(displayName = USER_NAME_JUSTIN),
    aMatrixUser(displayName = USER_NAME_MALLORY),
    aMatrixUser(displayName = USER_NAME_SUSIE),
    aMatrixUser(displayName = USER_NAME_VICTOR),
    aMatrixUser(displayName = USER_NAME_WALTER),
)
