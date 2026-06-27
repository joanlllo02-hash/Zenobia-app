/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.login.impl.screens.createaccount

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.matrix.api.core.SessionId

open class CreateAccountStateProvider : PreviewParameterProvider<CreateAccountState> {
    override val values: Sequence<CreateAccountState>
        get() = sequenceOf(
            aCreateAccountState(),
            aCreateAccountState(pageProgress = 33),
            aCreateAccountState(createAction = AsyncAction.Loading),
            aCreateAccountState(createAction = AsyncAction.Failure(RuntimeException("Failed to create account"))),
        )
}

private fun aCreateAccountState(
    pageProgress: Int = 100,
    createAction: AsyncAction<SessionId> = AsyncAction.Uninitialized,
) = CreateAccountState(
    url = "https://example.com",
    isDebugBuild = true,
    pageProgress = pageProgress,
    createAction = createAction,
    eventSink = {}
)
