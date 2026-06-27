/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.roomdetails.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import com.zenobia.app.features.roomdetails.impl.members.details.RoomMemberDetailsPresenter
import com.zenobia.app.features.userprofile.api.UserProfilePresenterFactory
import com.zenobia.app.libraries.androidutils.clipboard.ClipboardHelper
import com.zenobia.app.libraries.di.RoomScope
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.api.encryption.EncryptionService
import com.zenobia.app.libraries.matrix.api.room.JoinedRoom

@BindingContainer
@ContributesTo(RoomScope::class)
object RoomMemberModule {
    @Provides
    fun provideRoomMemberDetailsPresenterFactory(
        room: JoinedRoom,
        userProfilePresenterFactory: UserProfilePresenterFactory,
        encryptionService: EncryptionService,
        clipboardHelper: ClipboardHelper,
    ): RoomMemberDetailsPresenter.Factory {
        return object : RoomMemberDetailsPresenter.Factory {
            override fun create(roomMemberId: UserId): RoomMemberDetailsPresenter {
                return RoomMemberDetailsPresenter(
                    roomMemberId = roomMemberId,
                    room = room,
                    userProfilePresenterFactory = userProfilePresenterFactory,
                    encryptionService = encryptionService,
                    clipboardHelper = clipboardHelper,
                )
            }
        }
    }
}
