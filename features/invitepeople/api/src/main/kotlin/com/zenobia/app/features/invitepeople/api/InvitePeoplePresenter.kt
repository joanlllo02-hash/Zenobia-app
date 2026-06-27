/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.invitepeople.api

import com.zenobia.app.libraries.architecture.Presenter
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.room.JoinedRoom

interface InvitePeoplePresenter : Presenter<InvitePeopleState> {
    interface Factory {
        fun create(
            joinedRoom: JoinedRoom?,
            roomId: RoomId,
        ): InvitePeoplePresenter
    }
}
