/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.logout.impl.direct

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import dev.zacsweers.metro.ContributesBinding
import com.zenobia.app.features.logout.api.direct.DirectLogoutEvents
import com.zenobia.app.features.logout.api.direct.DirectLogoutState
import com.zenobia.app.features.logout.api.direct.DirectLogoutStateProvider
import com.zenobia.app.features.logout.api.direct.DirectLogoutView
import com.zenobia.app.features.logout.impl.ui.LogoutActionDialog
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.di.SessionScope

@ContributesBinding(SessionScope::class)
class DefaultDirectLogoutView : DirectLogoutView {
    @Composable
    override fun Render(state: DirectLogoutState) {
        val eventSink = state.eventSink
        LogoutActionDialog(
            state.logoutAction,
            onConfirmClick = {
                eventSink(DirectLogoutEvents.Logout(ignoreSdkError = false))
            },
            onForceLogoutClick = {
                eventSink(DirectLogoutEvents.Logout(ignoreSdkError = true))
            },
            onDismissDialog = {
                eventSink(DirectLogoutEvents.CloseDialogs)
            },
        )
    }
}

@PreviewsDayNight
@Composable
internal fun DefaultDirectLogoutViewPreview(
    @PreviewParameter(DirectLogoutStateProvider::class) state: DirectLogoutState,
) = ZenobiaPreview {
    DefaultDirectLogoutView().Render(state = state)
}
