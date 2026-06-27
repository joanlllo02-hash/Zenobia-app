/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.forward.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.zenobia.app.libraries.designsystem.components.async.AsyncActionView
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun ForwardMessagesView(
    state: ForwardMessagesState,
    onForwardSuccess: (List<RoomId>) -> Unit,
) {
    AsyncActionView(
        async = state.forwardAction,
        onSuccess = {
            onForwardSuccess(it)
        },
        errorMessage = {
            stringResource(id = CommonStrings.error_unknown)
        },
        onErrorDismiss = {
            state.eventSink(ForwardMessagesEvents.ClearError)
        },
    )
}

@PreviewsDayNight
@Composable
internal fun ForwardMessagesViewPreview(@PreviewParameter(ForwardMessagesStateProvider::class) state: ForwardMessagesState) = ZenobiaPreview {
    ForwardMessagesView(
        state = state,
        onForwardSuccess = {}
    )
}
