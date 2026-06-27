/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.roomcall.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import com.zenobia.app.features.roomcall.api.RoomCallState
import com.zenobia.app.features.roomcall.impl.RoomCallStatePresenter
import com.zenobia.app.libraries.architecture.Presenter
import com.zenobia.app.libraries.di.RoomScope

@ContributesTo(RoomScope::class)
@BindingContainer
interface RoomCallModule {
    @Binds
    fun bindRoomCallStatePresenter(presenter: RoomCallStatePresenter): Presenter<RoomCallState>
}
