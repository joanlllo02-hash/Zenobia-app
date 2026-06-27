/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.lockscreen.impl.device

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import com.zenobia.app.annotations.ContributesNode
import com.zenobia.app.features.lockscreen.impl.unlock.PinUnlockPresenter
import com.zenobia.app.features.lockscreen.impl.unlock.PinUnlockView
import com.zenobia.app.libraries.di.SessionScope

@ContributesNode(SessionScope::class)
@AssistedInject
class DeviceUnlockNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: DeviceUnlockPresenter,
    private val pinUnlockPresenterFactory: PinUnlockPresenter.Factory,
) : Node(buildContext, plugins = plugins) {
    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        if (state.showApplicationPinCode) {
            val pinUnlockPresenter = remember {
                pinUnlockPresenterFactory.create(forDeviceUnlock = true)
            }
            val pinState = pinUnlockPresenter.present()
            PinUnlockView(
                state = pinState,
                isInAppUnlock = true,
                onCancel = {
                    state.eventSink(DeviceUnlockEvent.CancelPinCode)
                },
                modifier = modifier,
            )
        }
    }
}
