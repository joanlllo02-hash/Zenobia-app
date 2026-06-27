/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.rolesandpermissions.impl.roles

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.model.permanent.PermanentNavModel
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import com.zenobia.app.annotations.ContributesNode
import com.zenobia.app.appnav.di.RoomGraphFactory
import com.zenobia.app.features.rolesandpermissions.api.ChangeRoomMemberRolesEntryPoint
import com.zenobia.app.features.rolesandpermissions.api.ChangeRoomMemberRolesListType
import com.zenobia.app.libraries.architecture.NodeInputs
import com.zenobia.app.libraries.architecture.createNode
import com.zenobia.app.libraries.architecture.inputs
import com.zenobia.app.libraries.di.DependencyInjectionGraphOwner
import com.zenobia.app.libraries.di.SessionScope
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.room.JoinedRoom
import kotlinx.parcelize.Parcelize

@ContributesNode(SessionScope::class)
@AssistedInject
class ChangeRoomMemberRolesRootNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    roomGraphFactory: RoomGraphFactory,
) : ParentNode<ChangeRoomMemberRolesRootNode.NavTarget>(
    navModel = PermanentNavModel(
        navTargets = setOf(NavTarget),
        savedStateMap = buildContext.savedStateMap,
    ),
    buildContext = buildContext,
    plugins = plugins,
), DependencyInjectionGraphOwner, ChangeRoomMemberRolesEntryPoint.NodeProxy {
    @Parcelize object NavTarget : Parcelable

    data class Inputs(
        val joinedRoom: JoinedRoom,
        val listType: ChangeRoomMemberRolesListType,
    ) : NodeInputs

    private val inputs = inputs<Inputs>()

    override val graph = roomGraphFactory.create(inputs.joinedRoom)

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return createNode<ChangeRolesNode>(
            buildContext = buildContext,
            plugins = listOf(ChangeRolesNode.Inputs(listType = inputs.listType)),
        )
    }

    @Composable
    override fun View(modifier: Modifier) {
        Children(modifier = modifier, navModel = navModel)
    }

    override val roomId: RoomId = inputs.joinedRoom.roomId

    override suspend fun waitForCompletion(): Boolean {
        return waitForChildAttached<ChangeRolesNode>().waitForCompletion()
    }
}
