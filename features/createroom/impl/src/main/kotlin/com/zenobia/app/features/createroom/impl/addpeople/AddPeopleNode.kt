/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.createroom.impl.addpeople

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import com.zenobia.app.annotations.ContributesNode
import com.zenobia.app.features.invitepeople.api.InvitePeoplePresenter
import com.zenobia.app.features.invitepeople.api.InvitePeopleRenderer
import com.zenobia.app.libraries.architecture.NodeInputs
import com.zenobia.app.libraries.architecture.callback
import com.zenobia.app.libraries.architecture.inputs
import com.zenobia.app.libraries.di.SessionScope
import com.zenobia.app.libraries.matrix.api.core.RoomId

@ContributesNode(SessionScope::class)
@AssistedInject
class AddPeopleNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    invitePeoplePresenterFactory: InvitePeoplePresenter.Factory,
    private val invitePeopleRenderer: InvitePeopleRenderer,
) : Node(buildContext, plugins = plugins) {
    data class Inputs(
        val roomId: RoomId,
    ) : NodeInputs

    interface Callback : Plugin {
        fun onFinish()
    }

    private val callback: Callback = callback()
    private val roomId = inputs<Inputs>().roomId
    private val invitePeoplePresenter = invitePeoplePresenterFactory.create(
        joinedRoom = null,
        roomId = roomId,
    )

    @Composable
    override fun View(modifier: Modifier) {
        val state = invitePeoplePresenter.present()
        AddPeopleView(
            state = state,
            onFinish = callback::onFinish,
        ) {
            invitePeopleRenderer.Render(state, Modifier)
        }
    }
}
