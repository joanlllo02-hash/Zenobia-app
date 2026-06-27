/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.di

import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import com.zenobia.app.appnav.di.TimelineBindings
import com.zenobia.app.libraries.architecture.NodeFactoriesBindings
import com.zenobia.app.libraries.di.RoomScope
import com.zenobia.app.libraries.matrix.api.room.BaseRoom
import com.zenobia.app.libraries.matrix.api.room.JoinedRoom

@GraphExtension(RoomScope::class)
interface RoomGraph : NodeFactoriesBindings, TimelineBindings {
    @GraphExtension.Factory
    interface Factory {
        fun create(
            @Provides joinedRoom: JoinedRoom,
            @Provides baseRoom: BaseRoom
        ): RoomGraph
    }
}
