/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.roomaliasresolver.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.architecture.Presenter
import com.zenobia.app.libraries.architecture.runCatchingUpdatingState
import com.zenobia.app.libraries.matrix.api.MatrixClient
import com.zenobia.app.libraries.matrix.api.core.RoomAlias
import com.zenobia.app.libraries.matrix.api.room.alias.ResolvedRoomAlias
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.jvm.optionals.getOrElse

@AssistedInject
class RoomAliasResolverPresenter(
    @Assisted private val roomAlias: RoomAlias,
    private val matrixClient: MatrixClient,
) : Presenter<RoomAliasResolverState> {
    fun interface Factory {
        fun create(
            roomAlias: RoomAlias,
        ): RoomAliasResolverPresenter
    }

    @Composable
    override fun present(): RoomAliasResolverState {
        val coroutineScope = rememberCoroutineScope()
        val resolveState: MutableState<AsyncData<ResolvedRoomAlias>> = remember { mutableStateOf(AsyncData.Uninitialized) }
        LaunchedEffect(Unit) {
            resolveAlias(resolveState)
        }

        fun handleEvent(event: RoomAliasResolverEvents) {
            when (event) {
                RoomAliasResolverEvents.Retry -> coroutineScope.resolveAlias(resolveState)
                RoomAliasResolverEvents.DismissError -> resolveState.value = AsyncData.Uninitialized
            }
        }

        return RoomAliasResolverState(
            roomAlias = roomAlias,
            resolveState = resolveState.value,
            eventSink = ::handleEvent,
        )
    }

    private fun CoroutineScope.resolveAlias(resolveState: MutableState<AsyncData<ResolvedRoomAlias>>) = launch {
        suspend {
            matrixClient.resolveRoomAlias(roomAlias)
                .getOrThrow()
                .getOrElse { throw RoomAliasResolverFailures.UnknownAlias }
        }.runCatchingUpdatingState(resolveState)
    }
}
