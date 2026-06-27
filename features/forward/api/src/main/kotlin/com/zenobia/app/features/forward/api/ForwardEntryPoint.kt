/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.forward.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.zenobia.app.libraries.architecture.FeatureEntryPoint
import com.zenobia.app.libraries.architecture.NodeInputs
import com.zenobia.app.libraries.matrix.api.core.EventId
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.timeline.TimelineProvider

interface ForwardEntryPoint : FeatureEntryPoint {
    interface Callback : Plugin {
        fun onDone(roomIds: List<RoomId>)
    }

    data class Params(
        val eventId: EventId,
        val timelineProvider: TimelineProvider,
    ) : NodeInputs

    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: Params,
        callback: Callback,
    ): Node
}
