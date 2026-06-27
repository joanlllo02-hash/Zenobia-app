/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.push.impl.push

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import com.zenobia.app.libraries.core.coroutine.CoroutineDispatchers
import com.zenobia.app.libraries.featureflag.api.FeatureFlagService
import com.zenobia.app.libraries.featureflag.api.FeatureFlags
import com.zenobia.app.libraries.matrix.api.MatrixClientProvider
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.core.SessionId
import com.zenobia.app.libraries.push.impl.db.PushRequest
import com.zenobia.app.services.appnavstate.api.AppForegroundStateService
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

@ContributesBinding(AppScope::class)
class DefaultSyncOnNotifiableEvent(
    private val matrixClientProvider: MatrixClientProvider,
    private val featureFlagService: FeatureFlagService,
    private val appForegroundStateService: AppForegroundStateService,
    private val dispatchers: CoroutineDispatchers,
) : SyncOnNotifiableEvent {
    override suspend operator fun invoke(requests: List<PushRequest>) = withContext(dispatchers.io) {
        if (!featureFlagService.isFeatureEnabled(FeatureFlags.SyncOnPush)) {
            return@withContext
        }

        try {
            val eventsBySession = requests.groupBy { it.sessionId }

            appForegroundStateService.updateIsSyncingNotificationEvent(true)
            Timber.d("Starting opportunistic room list sync | In foreground: ${appForegroundStateService.isInForeground.value}")

            for ((sessionId, events) in eventsBySession) {
                val client = matrixClientProvider.getOrRestore(SessionId(sessionId)).getOrNull() ?: continue
                val roomIds = events.map { RoomId(it.roomId) }.distinct()

                client.roomListService.subscribeToVisibleRooms(roomIds)

                if (!appForegroundStateService.isInForeground.value) {
                    // Give the sync some time to complete in background
                    delay(10.seconds)
                }
            }
        } finally {
            Timber.d("Finished opportunistic room list sync")
            appForegroundStateService.updateIsSyncingNotificationEvent(false)
        }
    }
}
