/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.test.pinned

import com.zenobia.app.features.messages.api.pinned.PinnedEventsTimelineProvider
import com.zenobia.app.libraries.matrix.api.timeline.Timeline
import com.zenobia.app.libraries.matrix.test.timeline.FakeTimelineProvider
import kotlinx.coroutines.flow.StateFlow

class FakePinnedEventsTimelineProvider(
    private val fakeTimelineProvider: FakeTimelineProvider = FakeTimelineProvider(),
) : PinnedEventsTimelineProvider {
    override fun activeTimelineFlow(): StateFlow<Timeline?> = fakeTimelineProvider.activeTimelineFlow()
}
