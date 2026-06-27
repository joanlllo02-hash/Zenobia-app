/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.share.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.ContributesBinding
import com.zenobia.app.features.share.api.ShareEntryPoint
import com.zenobia.app.libraries.architecture.createNode
import com.zenobia.app.libraries.di.SessionScope

@ContributesBinding(SessionScope::class)
class DefaultShareEntryPoint : ShareEntryPoint {
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: ShareEntryPoint.Params,
        callback: ShareEntryPoint.Callback,
    ): Node {
        return parentNode.createNode<ShareNode>(
            buildContext = buildContext,
            plugins = listOf(
                ShareNode.Inputs(shareIntentData = params.shareIntentData),
                callback,
            )
        )
    }
}
