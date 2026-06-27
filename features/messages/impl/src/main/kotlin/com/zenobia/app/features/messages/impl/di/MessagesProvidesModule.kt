/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import com.zenobia.app.features.messages.impl.timeline.di.LiveTimeline
import com.zenobia.app.libraries.di.RoomScope
import com.zenobia.app.libraries.matrix.api.room.JoinedRoom
import com.zenobia.app.libraries.matrix.api.timeline.Timeline

@ContributesTo(RoomScope::class)
@BindingContainer
object MessagesProvidesModule {
    @Provides
    @LiveTimeline
    fun provideLiveTimeline(joinedRoom: JoinedRoom): Timeline = joinedRoom.liveTimeline
}
