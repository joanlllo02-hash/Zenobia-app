/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import com.zenobia.app.libraries.di.SessionScope
import com.zenobia.app.libraries.di.annotations.SessionCoroutineScope
import com.zenobia.app.libraries.matrix.api.HomeserverCapabilitiesProvider
import com.zenobia.app.libraries.matrix.api.MatrixClient
import com.zenobia.app.libraries.matrix.api.core.SessionId
import com.zenobia.app.libraries.matrix.api.encryption.EncryptionService
import com.zenobia.app.libraries.matrix.api.media.MatrixMediaLoader
import com.zenobia.app.libraries.matrix.api.media.MediaPreviewService
import com.zenobia.app.libraries.matrix.api.notificationsettings.NotificationSettingsService
import com.zenobia.app.libraries.matrix.api.room.RoomMembershipObserver
import com.zenobia.app.libraries.matrix.api.roomdirectory.RoomDirectoryService
import com.zenobia.app.libraries.matrix.api.roomlist.RoomListService
import com.zenobia.app.libraries.matrix.api.spaces.SpaceService
import com.zenobia.app.libraries.matrix.api.sync.SyncService
import com.zenobia.app.libraries.matrix.api.verification.SessionVerificationService
import kotlinx.coroutines.CoroutineScope

@BindingContainer
@ContributesTo(SessionScope::class)
object SessionMatrixModule {
    @Provides
    fun providesSessionId(matrixClient: MatrixClient): SessionId {
        return matrixClient.sessionId
    }

    @Provides
    fun providesSessionVerificationService(matrixClient: MatrixClient): SessionVerificationService {
        return matrixClient.sessionVerificationService
    }

    @Provides
    fun providesNotificationSettingsService(matrixClient: MatrixClient): NotificationSettingsService {
        return matrixClient.notificationSettingsService
    }

    @Provides
    fun provideRoomMembershipObserver(matrixClient: MatrixClient): RoomMembershipObserver {
        return matrixClient.roomMembershipObserver
    }

    @Provides
    fun providesRoomListService(matrixClient: MatrixClient): RoomListService {
        return matrixClient.roomListService
    }

    @Provides
    fun providesSyncService(matrixClient: MatrixClient): SyncService {
        return matrixClient.syncService
    }

    @Provides
    fun providesEncryptionService(matrixClient: MatrixClient): EncryptionService {
        return matrixClient.encryptionService
    }

    @Provides
    fun providesMatrixMediaLoader(matrixClient: MatrixClient): MatrixMediaLoader {
        return matrixClient.matrixMediaLoader
    }

    @SessionCoroutineScope
    @Provides
    fun providesSessionCoroutineScope(matrixClient: MatrixClient): CoroutineScope {
        return matrixClient.sessionCoroutineScope
    }

    @Provides
    fun providesRoomDirectoryService(matrixClient: MatrixClient): RoomDirectoryService {
        return matrixClient.roomDirectoryService
    }

    @Provides
    fun providesMediaPreviewService(matrixClient: MatrixClient): MediaPreviewService {
        return matrixClient.mediaPreviewService
    }

    @Provides
    fun providesSpaceService(matrixClient: MatrixClient): SpaceService {
        return matrixClient.spaceService
    }

    @Provides
    fun providesHomeserverCapabilitiesProvider(matrixClient: MatrixClient): HomeserverCapabilitiesProvider {
        return matrixClient.homeserverCapabilities()
    }
}
