/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.invite.impl.declineandblock

import com.zenobia.app.libraries.architecture.AsyncAction

data class DeclineAndBlockState(
    val reportRoom: Boolean,
    val reportReason: String,
    val blockUser: Boolean,
    val declineAction: AsyncAction<Unit>,
    val eventSink: (DeclineAndBlockEvents) -> Unit
) {
    val canDecline = blockUser || reportRoom && reportReason.isNotEmpty()
}
