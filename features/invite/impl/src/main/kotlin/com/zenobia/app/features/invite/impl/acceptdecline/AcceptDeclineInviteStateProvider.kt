/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.invite.impl.acceptdecline

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.zenobia.app.features.invite.api.InviteData
import com.zenobia.app.features.invite.api.acceptdecline.AcceptDeclineInviteState
import com.zenobia.app.features.invite.api.acceptdecline.ConfirmingDeclineInvite
import com.zenobia.app.features.invite.api.acceptdecline.anAcceptDeclineInviteState
import com.zenobia.app.features.invite.impl.AcceptInvite
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.designsystem.preview.ROOM_NAME
import com.zenobia.app.libraries.matrix.api.core.RoomId

open class AcceptDeclineInviteStateProvider : PreviewParameterProvider<AcceptDeclineInviteState> {
    override val values: Sequence<AcceptDeclineInviteState>
        get() = sequenceOf(
            anAcceptDeclineInviteState(),
            anAcceptDeclineInviteState(
                declineAction = ConfirmingDeclineInvite(
                    InviteData(
                        roomId = RoomId("!room:matrix.org"),
                        isDm = true,
                        roomName = ROOM_NAME,
                    ),
                    blockUser = false,
                ),
            ),
            anAcceptDeclineInviteState(
                declineAction = ConfirmingDeclineInvite(
                    InviteData(
                        roomId = RoomId("!room:matrix.org"),
                        isDm = true,
                        roomName = ROOM_NAME,
                    ),
                    blockUser = true,
                ),
            ),
            anAcceptDeclineInviteState(
                acceptAction = AsyncAction.Failure(RuntimeException("Error while accepting invite")),
            ),
            anAcceptDeclineInviteState(
                acceptAction = AsyncAction.Failure(AcceptInvite.Failures.InvalidInvite),
            ),
            anAcceptDeclineInviteState(
                declineAction = AsyncAction.Failure(RuntimeException("Error while declining invite")),
            ),
        )
}
