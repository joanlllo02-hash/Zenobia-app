/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.invitepeople.api

import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.matrix.api.core.RoomId

interface InvitePeopleState {
    val canInvite: Boolean
    val isSearchActive: Boolean
    val sendInvitesAction: AsyncAction<Unit>
    val createRoomFromDmAction: AsyncAction<RoomId>
    val eventSink: (InvitePeopleEvents) -> Unit
}
