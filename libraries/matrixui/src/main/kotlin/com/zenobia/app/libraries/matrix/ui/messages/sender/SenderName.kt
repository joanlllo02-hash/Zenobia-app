/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.ui.messages.sender

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.api.timeline.item.event.ProfileDetails

// https://www.figma.com/file/Ni6Ii8YKtmXCKYNE90cC67/Timeline-(new)?type=design&node-id=917-80169&mode=design&t=A0CJCBbMqR8NOwUQ-0
@Composable
fun SenderName(
    senderId: UserId,
    senderProfile: ProfileDetails,
    senderNameMode: SenderNameMode,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (senderProfile) {
            is ProfileDetails.Error,
            ProfileDetails.Pending,
            ProfileDetails.Unavailable -> {
                MainText(text = senderId.value, mode = senderNameMode)
            }
            is ProfileDetails.Ready -> {
                val displayName = senderProfile.displayName
                if (displayName.isNullOrEmpty()) {
                    MainText(text = senderId.value, mode = senderNameMode)
                } else {
                    MainText(text = displayName, mode = senderNameMode)
                    if (senderProfile.displayNameAmbiguous) {
                        SecondaryText(text = senderId.value, mode = senderNameMode)
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.MainText(
    text: String,
    mode: SenderNameMode,
) {
    val style = when (mode) {
        is SenderNameMode.Timeline -> ZenobiaTheme.typography.fontBodyMdMedium
        SenderNameMode.ActionList,
        SenderNameMode.Reply -> ZenobiaTheme.typography.fontBodySmMedium
    }
    val modifier = when (mode) {
        is SenderNameMode.Timeline -> Modifier.alignByBaseline()
        SenderNameMode.ActionList,
        SenderNameMode.Reply -> Modifier
    }
    val color = when (mode) {
        is SenderNameMode.Timeline -> mode.mainColor
        SenderNameMode.ActionList,
        SenderNameMode.Reply -> ZenobiaTheme.colors.textPrimary
    }
    Text(
        modifier = modifier.clipToBounds(),
        text = text,
        style = style,
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun RowScope.SecondaryText(
    text: String,
    mode: SenderNameMode,
) {
    val style = when (mode) {
        is SenderNameMode.Timeline -> ZenobiaTheme.typography.fontBodySmRegular
        SenderNameMode.ActionList,
        SenderNameMode.Reply -> ZenobiaTheme.typography.fontBodyXsRegular
    }
    val modifier = when (mode) {
        is SenderNameMode.Timeline -> Modifier.alignByBaseline()
        SenderNameMode.ActionList,
        SenderNameMode.Reply -> Modifier
    }
    Text(
        modifier = modifier.clipToBounds(),
        text = text,
        style = style,
        color = ZenobiaTheme.colors.textSecondary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@PreviewsDayNight
@Composable
internal fun SenderNamePreview(
    @PreviewParameter(SenderNameDataProvider::class) senderNameData: SenderNameData,
) = ZenobiaPreview {
    SenderName(
        senderId = senderNameData.userId,
        senderProfile = senderNameData.profileDetails,
        senderNameMode = senderNameData.senderNameMode,
    )
}
