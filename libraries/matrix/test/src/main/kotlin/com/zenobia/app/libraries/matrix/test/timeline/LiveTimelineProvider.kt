/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.test.timeline

import com.zenobia.app.libraries.matrix.api.room.JoinedRoom
import com.zenobia.app.libraries.matrix.api.timeline.Timeline
import com.zenobia.app.libraries.matrix.api.timeline.TimelineProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LiveTimelineProvider(
    private val room: JoinedRoom,
) : TimelineProvider {
    override fun activeTimelineFlow(): StateFlow<Timeline> = MutableStateFlow(room.liveTimeline)
}
