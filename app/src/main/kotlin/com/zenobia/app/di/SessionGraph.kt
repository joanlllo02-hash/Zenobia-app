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
import com.zenobia.app.libraries.architecture.NodeFactoriesBindings
import com.zenobia.app.libraries.di.SessionScope
import com.zenobia.app.libraries.matrix.api.MatrixClient

@GraphExtension(SessionScope::class)
interface SessionGraph : NodeFactoriesBindings {
    val roomGraphFactory: RoomGraph.Factory

    @GraphExtension.Factory
    interface Factory {
        fun create(@Provides matrixClient: MatrixClient): SessionGraph
    }
}
