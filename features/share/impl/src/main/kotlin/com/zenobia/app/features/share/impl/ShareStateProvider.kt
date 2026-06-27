/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.share.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.matrix.api.core.RoomId

open class ShareStateProvider : PreviewParameterProvider<ShareState> {
    override val values: Sequence<ShareState>
        get() = sequenceOf(
            aShareState(),
            aShareState(
                shareAction = AsyncAction.Loading,
            ),
            aShareState(
                shareAction = AsyncAction.Success(
                    listOf(RoomId("!room2:domain")),
                )
            ),
            aShareState(
                shareAction = AsyncAction.Failure(RuntimeException("error")),
            ),
        )
}

fun aShareState(
    shareAction: AsyncAction<List<RoomId>> = AsyncAction.Uninitialized,
    eventSink: (ShareEvents) -> Unit = {}
) = ShareState(
    shareAction = shareAction,
    eventSink = eventSink
)
