/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.knockrequests.impl.data

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import com.zenobia.app.features.knockrequests.api.KnockRequestPermissions
import com.zenobia.app.features.knockrequests.api.knockRequestPermissions
import com.zenobia.app.libraries.di.RoomScope
import com.zenobia.app.libraries.matrix.api.room.JoinedRoom
import com.zenobia.app.libraries.matrix.api.room.powerlevels.permissionsFlow

@BindingContainer
@ContributesTo(RoomScope::class)
object KnockRequestsModule {
    @Provides
    @SingleIn(RoomScope::class)
    fun knockRequestsService(room: JoinedRoom): KnockRequestsService {
        return KnockRequestsService(
            knockRequestsFlow = room.knockRequestsFlow,
            permissionsFlow = room.permissionsFlow(KnockRequestPermissions.DEFAULT) { perms ->
                perms.knockRequestPermissions()
            },
            coroutineScope = room.roomCoroutineScope,
        )
    }
}
