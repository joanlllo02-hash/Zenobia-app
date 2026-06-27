/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.appnav.di

import com.zenobia.app.features.messages.api.pinned.PinnedEventsTimelineProvider
import com.zenobia.app.libraries.matrix.api.timeline.TimelineProvider
import com.zenobia.app.services.analytics.api.watchers.AnalyticsSendMessageWatcher

interface TimelineBindings {
    val timelineProvider: TimelineProvider
    val pinnedEventsTimelineProvider: PinnedEventsTimelineProvider
    val analyticsSendMessageWatcher: AnalyticsSendMessageWatcher
}
