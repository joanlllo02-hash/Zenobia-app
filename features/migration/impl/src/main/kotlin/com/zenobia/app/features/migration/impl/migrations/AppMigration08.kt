/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.migration.impl.migrations

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import com.zenobia.app.features.announcement.api.Announcement
import com.zenobia.app.features.announcement.api.AnnouncementService

/**
 * Ensure the new notification sound banner is displayed, but only on application upgrade.
 */
@ContributesIntoSet(AppScope::class)
class AppMigration08(
    private val announcementService: AnnouncementService,
) : AppMigration {
    override val order: Int = 8

    override suspend fun migrate(isFreshInstall: Boolean) {
        if (!isFreshInstall) {
            announcementService.showAnnouncement(Announcement.NewNotificationSound)
        }
    }
}
