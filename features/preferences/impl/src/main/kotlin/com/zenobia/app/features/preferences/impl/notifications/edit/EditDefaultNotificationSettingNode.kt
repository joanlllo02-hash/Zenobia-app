/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.preferences.impl.notifications.edit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import com.zenobia.app.annotations.ContributesNode
import com.zenobia.app.libraries.architecture.NodeInputs
import com.zenobia.app.libraries.architecture.callback
import com.zenobia.app.libraries.architecture.inputs
import com.zenobia.app.libraries.di.SessionScope
import com.zenobia.app.libraries.matrix.api.core.RoomId

@ContributesNode(SessionScope::class)
@AssistedInject
class EditDefaultNotificationSettingNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: EditDefaultNotificationSettingPresenter.Factory
) : Node(buildContext, plugins = plugins) {
    interface Callback : Plugin {
        fun navigateToRoomNotificationSettings(roomId: RoomId)
    }

    data class Inputs(
        val isDm: Boolean
    ) : NodeInputs

    private val callback: Callback = callback()
    private val inputs = inputs<Inputs>()
    private val presenter = presenterFactory.create(inputs.isDm)

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        EditDefaultNotificationSettingView(
            state = state,
            openRoomNotificationSettings = callback::navigateToRoomNotificationSettings,
            onBackClick = ::navigateUp,
            modifier = modifier,
        )
    }
}
