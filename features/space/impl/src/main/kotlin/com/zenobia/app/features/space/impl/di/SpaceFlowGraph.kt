/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.space.impl.di

import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import com.zenobia.app.libraries.architecture.NodeFactoriesBindings
import com.zenobia.app.libraries.di.RoomScope
import com.zenobia.app.libraries.matrix.api.spaces.SpaceRoomList

@GraphExtension(SpaceFlowScope::class)
interface SpaceFlowGraph : NodeFactoriesBindings {
    @ContributesTo(RoomScope::class)
    @GraphExtension.Factory
    interface Factory {
        fun create(@Provides spaceRoomList: SpaceRoomList): SpaceFlowGraph
    }
}
