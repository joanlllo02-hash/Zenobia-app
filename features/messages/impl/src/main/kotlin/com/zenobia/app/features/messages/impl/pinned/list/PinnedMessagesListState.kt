/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.pinned.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.zenobia.app.features.messages.impl.UserEventPermissions
import com.zenobia.app.features.messages.impl.actionlist.ActionListState
import com.zenobia.app.features.messages.impl.link.LinkState
import com.zenobia.app.features.messages.impl.timeline.TimelineRoomInfo
import com.zenobia.app.features.messages.impl.timeline.model.TimelineItem
import com.zenobia.app.features.messages.impl.timeline.protection.TimelineProtectionState
import com.zenobia.app.libraries.ui.strings.CommonPlurals
import com.zenobia.app.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed interface PinnedMessagesListState {
    data object Failed : PinnedMessagesListState
    data object Loading : PinnedMessagesListState
    data object Empty : PinnedMessagesListState
    data class Filled(
        val timelineRoomInfo: TimelineRoomInfo,
        val timelineProtectionState: TimelineProtectionState,
        val userEventPermissions: UserEventPermissions,
        val timelineItems: ImmutableList<TimelineItem>,
        val actionListState: ActionListState,
        val linkState: LinkState,
        val displayThreadSummaries: Boolean,
        val eventSink: (PinnedMessagesListEvent) -> Unit,
    ) : PinnedMessagesListState {
        val loadedPinnedMessagesCount = timelineItems.count { timelineItem -> timelineItem is TimelineItem.Event }
    }

    @Composable
    fun title(): String {
        return when (this) {
            is Filled -> {
                pluralStringResource(id = CommonPlurals.screen_pinned_timeline_screen_title, loadedPinnedMessagesCount, loadedPinnedMessagesCount)
            }
            else -> stringResource(id = CommonStrings.screen_pinned_timeline_screen_title_empty)
        }
    }
}
