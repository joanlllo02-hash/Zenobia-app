/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.location.impl.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.features.location.api.Location
import com.zenobia.app.features.location.impl.show.LocationShareItem
import com.zenobia.app.libraries.designsystem.components.avatar.Avatar
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarData
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarSize
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarType
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.preview.USER_NAME_ALICE
import com.zenobia.app.libraries.designsystem.preview.USER_NAME_BOB
import com.zenobia.app.libraries.designsystem.theme.components.Icon
import com.zenobia.app.libraries.designsystem.theme.components.IconButton
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.api.room.location.AssetType
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun LocationShareRow(
    item: LocationShareItem,
    onShareClick: () -> Unit,
    onStopClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(
            avatarData = item.avatarData,
            avatarType = AvatarType.User,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = item.displayName,
                style = ZenobiaTheme.typography.fontBodyLgMedium,
                color = ZenobiaTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (item.isLive) {
                    Icon(
                        imageVector = CompoundIcons.LocationPinSolid(),
                        contentDescription = null,
                        tint = ZenobiaTheme.colors.iconAccentPrimary,
                        modifier = Modifier.size(16.dp),
                    )
                } else {
                    val icon = if (item.assetType == AssetType.PIN) {
                        CompoundIcons.LocationNavigator()
                    } else {
                        CompoundIcons.LocationNavigatorCentred()
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = ZenobiaTheme.colors.iconSecondary,
                        modifier = Modifier.size(16.dp),
                    )
                }
                Text(
                    text = if (item.isLive) stringResource(CommonStrings.screen_room_live_location_banner) else item.formattedTimestamp,
                    style = ZenobiaTheme.typography.fontBodySmRegular,
                    color = if (item.isLive) ZenobiaTheme.colors.textPrimary else ZenobiaTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (item.canStopSharing) {
            IconButton(
                onClick = onStopClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = ZenobiaTheme.colors.bgCriticalPrimary,
                    contentColor = ZenobiaTheme.colors.iconOnSolidPrimary,
                )
            ) {
                Icon(
                    imageVector = CompoundIcons.Stop(),
                    contentDescription = stringResource(CommonStrings.action_stop),
                )
            }
        }
        IconButton(onClick = onShareClick) {
            Icon(
                imageVector = CompoundIcons.ShareAndroid(),
                contentDescription = stringResource(CommonStrings.action_share),
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun LocationShareRowPreview() = ZenobiaPreview {
    Column {
        LocationShareRow(
            item = LocationShareItem(
                userId = UserId("@alice:matrix.org"),
                displayName = USER_NAME_ALICE,
                avatarData = AvatarData(
                    id = "@alice:matrix.org",
                    name = USER_NAME_ALICE,
                    url = null,
                    size = AvatarSize.UserListItem,
                ),
                formattedTimestamp = "Shared 1 min ago",
                isLive = true,
                assetType = AssetType.SENDER,
                location = Location(0.0, 0.0),
                isOwnUser = true,
            ),
            onStopClick = {},
            onShareClick = {},
        )
        LocationShareRow(
            item = LocationShareItem(
                userId = UserId("@bob:matrix.org"),
                displayName = USER_NAME_BOB,
                avatarData = AvatarData(
                    id = "@bob:matrix.org",
                    name = USER_NAME_BOB,
                    url = null,
                    size = AvatarSize.UserListItem,
                ),
                isLive = false,
                assetType = AssetType.PIN,
                formattedTimestamp = "Shared 5 hours ago",
                location = Location(0.0, 0.0),
                isOwnUser = false
            ),
            onStopClick = {},
            onShareClick = {},
        )
    }
}
