/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.startchat.impl.root

import com.zenobia.app.features.startchat.impl.userlist.UserListState
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.matrix.api.core.RoomId

data class StartChatState(
    val applicationName: String,
    val userListState: UserListState,
    val startDmAction: AsyncAction<RoomId>,
    val eventSink: (StartChatEvents) -> Unit,
)
