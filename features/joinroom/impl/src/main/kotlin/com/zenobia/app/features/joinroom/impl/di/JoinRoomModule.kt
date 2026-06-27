/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.joinroom.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import im.vector.app.features.analytics.plan.JoinedRoom
import com.zenobia.app.features.invite.api.SeenInvitesStore
import com.zenobia.app.features.invite.api.acceptdecline.AcceptDeclineInviteState
import com.zenobia.app.features.joinroom.impl.JoinRoomPresenter
import com.zenobia.app.features.roomdirectory.api.RoomDescription
import com.zenobia.app.libraries.architecture.Presenter
import com.zenobia.app.libraries.core.meta.BuildMeta
import com.zenobia.app.libraries.di.SessionScope
import com.zenobia.app.libraries.matrix.api.MatrixClient
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.core.RoomIdOrAlias
import com.zenobia.app.libraries.matrix.api.room.join.JoinRoom
import java.util.Optional

@BindingContainer
@ContributesTo(SessionScope::class)
object JoinRoomModule {
    @Provides
    fun providesJoinRoomPresenterFactory(
        client: MatrixClient,
        joinRoom: JoinRoom,
        knockRoom: KnockRoom,
        cancelKnockRoom: CancelKnockRoom,
        forgetRoom: ForgetRoom,
        acceptDeclineInvitePresenter: Presenter<AcceptDeclineInviteState>,
        buildMeta: BuildMeta,
        seenInvitesStore: SeenInvitesStore,
    ): JoinRoomPresenter.Factory {
        return object : JoinRoomPresenter.Factory {
            override fun create(
                roomId: RoomId,
                roomIdOrAlias: RoomIdOrAlias,
                roomDescription: Optional<RoomDescription>,
                serverNames: List<String>,
                trigger: JoinedRoom.Trigger,
            ): JoinRoomPresenter {
                return JoinRoomPresenter(
                    roomId = roomId,
                    roomIdOrAlias = roomIdOrAlias,
                    roomDescription = roomDescription,
                    serverNames = serverNames,
                    trigger = trigger,
                    matrixClient = client,
                    joinRoom = joinRoom,
                    knockRoom = knockRoom,
                    forgetRoom = forgetRoom,
                    cancelKnockRoom = cancelKnockRoom,
                    acceptDeclineInvitePresenter = acceptDeclineInvitePresenter,
                    buildMeta = buildMeta,
                    seenInvitesStore = seenInvitesStore,
                )
            }
        }
    }
}
