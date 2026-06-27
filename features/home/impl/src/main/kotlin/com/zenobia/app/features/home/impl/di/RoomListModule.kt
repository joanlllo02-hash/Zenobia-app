/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.home.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import com.zenobia.app.features.home.impl.filters.RoomListFiltersPresenter
import com.zenobia.app.features.home.impl.filters.RoomListFiltersState
import com.zenobia.app.features.home.impl.roomlist.RoomListPresenter
import com.zenobia.app.features.home.impl.roomlist.RoomListState
import com.zenobia.app.features.home.impl.search.RoomListSearchPresenter
import com.zenobia.app.features.home.impl.search.RoomListSearchState
import com.zenobia.app.features.home.impl.spacefilters.SpaceFiltersPresenter
import com.zenobia.app.features.home.impl.spacefilters.SpaceFiltersState
import com.zenobia.app.libraries.architecture.Presenter
import com.zenobia.app.libraries.di.SessionScope

@ContributesTo(SessionScope::class)
@BindingContainer
interface RoomListModule {
    @Binds
    fun bindRoomListPresenter(presenter: RoomListPresenter): Presenter<RoomListState>

    @Binds
    fun bindSearchPresenter(presenter: RoomListSearchPresenter): Presenter<RoomListSearchState>

    @Binds
    fun bindFiltersPresenter(presenter: RoomListFiltersPresenter): Presenter<RoomListFiltersState>

    @Binds
    fun bindSpaceFiltersPresenter(presenter: SpaceFiltersPresenter): Presenter<SpaceFiltersState>
}
