/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.home.impl.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.zenobia.app.features.home.impl.R
import com.zenobia.app.libraries.designsystem.components.Announcement
import com.zenobia.app.libraries.designsystem.components.AnnouncementType
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
internal fun NewNotificationSoundBanner(
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Announcement(
        modifier = modifier.roomListBannerPadding(),
        title = stringResource(R.string.banner_new_sound_title),
        description = stringResource(R.string.banner_new_sound_message),
        type = AnnouncementType.Actionable(
            actionText = stringResource(CommonStrings.action_ok),
            onActionClick = onDismissClick,
            onDismissClick = onDismissClick,
        ),
    )
}

@PreviewsDayNight
@Composable
internal fun NewNotificationSoundBannerPreview() = ZenobiaPreview {
    NewNotificationSoundBanner(
        onDismissClick = {},
    )
}
