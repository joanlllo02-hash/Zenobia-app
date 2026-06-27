/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.home.impl.model

import androidx.compose.runtime.Immutable
import com.zenobia.app.features.invite.api.InviteData
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarData
import com.zenobia.app.libraries.matrix.api.core.RoomAlias
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.notification.CallIntent
import com.zenobia.app.libraries.matrix.api.room.RoomNotificationMode
import com.zenobia.app.libraries.matrix.ui.model.InviteSender
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class RoomListRoomSummary(
    val id: String,
    val displayType: RoomSummaryDisplayType,
    val roomId: RoomId,
    val name: String?,
    val canonicalAlias: RoomAlias?,
    val numberOfUnreadMessages: Long,
    val numberOfUnreadMentions: Long,
    val numberOfUnreadNotifications: Long,
    val isMarkedUnread: Boolean,
    val timestamp: String?,
    val latestEvent: LatestEvent,
    val avatarData: AvatarData,
    val userDefinedNotificationMode: RoomNotificationMode?,
    val hasRoomCall: Boolean,
    val activeCallIntent: CallIntent?,
    val isDirect: Boolean,
    val isDm: Boolean,
    val isFavorite: Boolean,
    val inviteSender: InviteSender?,
    val isTombstoned: Boolean,
    val heroes: ImmutableList<AvatarData>,
    val isSpace: Boolean,
) {
    val isHighlighted = userDefinedNotificationMode != RoomNotificationMode.MUTE &&
        (numberOfUnreadNotifications > 0 || numberOfUnreadMentions > 0) ||
        isMarkedUnread

    val hasNewContent = numberOfUnreadMessages > 0 ||
        numberOfUnreadMentions > 0 ||
        numberOfUnreadNotifications > 0 ||
        isMarkedUnread

    fun toInviteData() = InviteData(
        roomId = roomId,
        roomName = name ?: roomId.value,
        isDm = isDm,
    )
}
