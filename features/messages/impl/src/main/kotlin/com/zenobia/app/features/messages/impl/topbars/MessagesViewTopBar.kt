/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.topbars

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.features.messages.impl.MessagesMenuActions
import com.zenobia.app.features.messages.impl.SharedHistoryIcon
import com.zenobia.app.features.roomcall.api.RoomCallState
import com.zenobia.app.features.roomcall.api.aStandByCallState
import com.zenobia.app.features.roomcall.api.anOngoingCallState
import com.zenobia.app.libraries.designsystem.components.avatar.Avatar
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarData
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarSize
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarType
import com.zenobia.app.libraries.designsystem.components.avatar.anAvatarData
import com.zenobia.app.libraries.designsystem.components.button.BackButton
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.preview.ROOM_NAME
import com.zenobia.app.libraries.designsystem.theme.components.HorizontalDivider
import com.zenobia.app.libraries.designsystem.theme.components.Icon
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.designsystem.theme.components.TopAppBar
import com.zenobia.app.libraries.matrix.api.encryption.identity.IdentityState
import com.zenobia.app.libraries.matrix.ui.components.aMatrixUserList
import com.zenobia.app.libraries.matrix.ui.model.getAvatarData
import com.zenobia.app.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MessagesViewTopBar(
    roomName: String?,
    roomAvatar: AvatarData,
    isTombstoned: Boolean,
    heroes: ImmutableList<AvatarData>,
    dmUserIdentityState: IdentityState?,
    sharedHistoryIcon: SharedHistoryIcon,
    onRoomDetailsClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    menuActions: @Composable RowScope.() -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        navigationIcon = {
            BackButton(onClick = onBackClick)
        },
        title = {
            val roundedCornerShape = RoundedCornerShape(8.dp)
            Row(
                modifier = Modifier
                    .clip(roundedCornerShape)
                    .clickable { onRoomDetailsClick() }
                    .semantics { heading() },
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val titleModifier = Modifier.weight(1f, fill = false)
                RoomAvatarAndNameRow(
                    roomName = roomName,
                    roomAvatar = roomAvatar,
                    isTombstoned = isTombstoned,
                    heroes = heroes,
                    modifier = titleModifier
                )

                val iconModifier = Modifier.size(16.dp)

                when (dmUserIdentityState) {
                    IdentityState.Verified -> {
                        Icon(
                            modifier = iconModifier,
                            imageVector = CompoundIcons.Verified(),
                            tint = ZenobiaTheme.colors.iconSuccessPrimary,
                            contentDescription = null,
                        )
                    }
                    IdentityState.VerificationViolation -> {
                        Icon(
                            modifier = iconModifier,
                            imageVector = CompoundIcons.ErrorSolid(),
                            tint = ZenobiaTheme.colors.iconCriticalPrimary,
                            contentDescription = null,
                        )
                    }
                    else -> Unit
                }

                when (sharedHistoryIcon) {
                    SharedHistoryIcon.NONE -> Unit
                    SharedHistoryIcon.SHARED -> Icon(
                        modifier = iconModifier,
                        imageVector = CompoundIcons.History(),
                        tint = ZenobiaTheme.colors.iconInfoPrimary,
                        contentDescription = stringResource(CommonStrings.common_shared_history),
                    )
                    SharedHistoryIcon.WORLD_READABLE -> Icon(
                        modifier = iconModifier,
                        imageVector = CompoundIcons.UserProfileSolid(),
                        tint = ZenobiaTheme.colors.iconInfoPrimary,
                        contentDescription = stringResource(CommonStrings.common_world_readable_history),
                    )
                }
            }
        },
        actions = menuActions,
        windowInsets = WindowInsets(0.dp)
    )
}

@Composable
private fun RoomAvatarAndNameRow(
    roomName: String?,
    roomAvatar: AvatarData,
    heroes: ImmutableList<AvatarData>,
    isTombstoned: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(
            avatarData = roomAvatar,
            avatarType = AvatarType.Room(
                heroes = heroes,
                isTombstoned = isTombstoned,
            ),
        )
        Text(
            modifier = Modifier
                .padding(start = 8.dp),
            text = roomName ?: stringResource(CommonStrings.common_no_room_name),
            style = ZenobiaTheme.typography.fontBodyLgMedium,
            fontStyle = FontStyle.Italic.takeIf { roomName == null },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@PreviewsDayNight
@Composable
internal fun MessagesViewTopBarPreview() = ZenobiaPreview {
    @Composable
    fun AMessagesViewTopBar(
        roomName: String? = ROOM_NAME,
        roomAvatar: AvatarData = anAvatarData(
            name = ROOM_NAME,
            size = AvatarSize.TimelineRoom,
        ),
        isTombstoned: Boolean = false,
        heroes: ImmutableList<AvatarData> = persistentListOf(),
        roomCallState: RoomCallState = RoomCallState.Unavailable,
        dmUserIdentityState: IdentityState? = null,
        sharedHistoryIcon: SharedHistoryIcon = SharedHistoryIcon.NONE,
        displayThreads: Boolean = false,
    ) = MessagesViewTopBar(
        roomName = roomName,
        roomAvatar = roomAvatar,
        isTombstoned = isTombstoned,
        heroes = heroes,
        dmUserIdentityState = dmUserIdentityState,
        sharedHistoryIcon = sharedHistoryIcon,
        onRoomDetailsClick = {},
        onBackClick = {},
        menuActions = {
            MessagesMenuActions(
                roomCallState = roomCallState,
                displayThreads = displayThreads,
                onJoinCallClick = {},
                onThreadsListClick = {},
            )
        }
    )
    Column {
        AMessagesViewTopBar()
        HorizontalDivider()
        AMessagesViewTopBar(
            heroes = aMatrixUserList().map { it.getAvatarData(AvatarSize.TimelineRoom) }.toImmutableList(),
            roomCallState = anOngoingCallState(),
        )
        HorizontalDivider()
        AMessagesViewTopBar(
            roomName = null,
            roomCallState = anOngoingCallState(canJoinCall = false),
        )
        HorizontalDivider()
        AMessagesViewTopBar(
            roomName = "A DM with a very very very long name",
            roomAvatar = anAvatarData(
                size = AvatarSize.TimelineRoom,
                url = "https://some-avatar.jpg"
            ),
            roomCallState = aStandByCallState(canStartCall = false),
            dmUserIdentityState = IdentityState.Verified
        )
        HorizontalDivider()
        AMessagesViewTopBar(
            roomName = "A DM with a very very very long name",
            isTombstoned = true,
            dmUserIdentityState = IdentityState.VerificationViolation
        )
        HorizontalDivider()
        AMessagesViewTopBar(
            roomName = "A DM with shared history",
            dmUserIdentityState = IdentityState.Verified,
            sharedHistoryIcon = SharedHistoryIcon.SHARED,
        )
        HorizontalDivider()
        AMessagesViewTopBar(
            roomName = "A room with world_readable history",
            sharedHistoryIcon = SharedHistoryIcon.WORLD_READABLE,
        )
        HorizontalDivider()
        AMessagesViewTopBar(
            displayThreads = true,
        )
    }
}
