/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.lockscreen.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import com.zenobia.app.features.lockscreen.api.DeviceUnlockEntryPoint
import com.zenobia.app.features.lockscreen.impl.device.DeviceUnlockCallbackHolder
import com.zenobia.app.features.lockscreen.impl.device.DeviceUnlockNode
import com.zenobia.app.libraries.architecture.createNode

@ContributesBinding(AppScope::class)
class DefaultDeviceUnlockEntryPoint(
    private val deviceUnlockCallbackHolder: DeviceUnlockCallbackHolder,
) : DeviceUnlockEntryPoint {
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
    ): Node {
        return parentNode.createNode<DeviceUnlockNode>(
            buildContext = buildContext,
        )
    }

    override fun requestUnlock(callback: DeviceUnlockEntryPoint.Callback) {
        deviceUnlockCallbackHolder.requestUnlock(callback)
    }
}
