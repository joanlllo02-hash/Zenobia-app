/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.rageshake.test.logs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zenobia.app.features.announcement.api.Announcement
import com.zenobia.app.features.announcement.api.AnnouncementService
import com.zenobia.app.tests.testutils.lambda.lambdaError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAnnouncementService(
    initialAnnouncementsToShowFlowValue: List<Announcement> = emptyList(),
    val showAnnouncementResult: (Announcement) -> Unit = { lambdaError() },
    val onAnnouncementDismissedResult: (Announcement) -> Unit = { lambdaError() },
    val renderResult: (Modifier) -> Unit = { lambdaError() },
) : AnnouncementService {
    private val announcementsToShowFlowValue = MutableStateFlow(initialAnnouncementsToShowFlowValue)

    override suspend fun showAnnouncement(announcement: Announcement) {
        showAnnouncementResult(announcement)
    }

    override suspend fun onAnnouncementDismissed(announcement: Announcement) {
        onAnnouncementDismissedResult(announcement)
    }

    override fun announcementsToShowFlow(): Flow<List<Announcement>> {
        return announcementsToShowFlowValue.asStateFlow()
    }

    fun emitAnnouncementsToShow(value: List<Announcement>) {
        announcementsToShowFlowValue.value = value
    }

    @Composable
    override fun Render(modifier: Modifier) {
        renderResult(modifier)
    }
}
