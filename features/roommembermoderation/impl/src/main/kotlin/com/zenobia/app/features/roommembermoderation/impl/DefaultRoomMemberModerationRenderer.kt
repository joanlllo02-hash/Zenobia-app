/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.roommembermoderation.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import dev.zacsweers.metro.ContributesBinding
import com.zenobia.app.features.roommembermoderation.api.ModerationAction
import com.zenobia.app.features.roommembermoderation.api.RoomMemberModerationRenderer
import com.zenobia.app.features.roommembermoderation.api.RoomMemberModerationState
import com.zenobia.app.libraries.di.RoomScope
import com.zenobia.app.libraries.matrix.api.user.MatrixUser
import timber.log.Timber

@ContributesBinding(RoomScope::class)
class DefaultRoomMemberModerationRenderer : RoomMemberModerationRenderer {
    @Composable
    override fun Render(
        state: RoomMemberModerationState,
        onSelectAction: (ModerationAction, MatrixUser) -> Unit,
        onAvatarClick: ((MatrixUser) -> Unit)?,
        modifier: Modifier
    ) {
        if (state is InternalRoomMemberModerationState) {
            RoomMemberModerationView(modifier = modifier, state = state, onSelectAction = onSelectAction, onAvatarClick = onAvatarClick)
        } else {
            SideEffect {
                Timber.d("RoomMemberModerationRenderer: Render called with unsupported state: $state")
            }
        }
    }
}
