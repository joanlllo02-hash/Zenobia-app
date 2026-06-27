/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
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
import com.zenobia.app.libraries.fullscreenintent.api.FullScreenIntentPermissionsEvents
import com.zenobia.app.libraries.fullscreenintent.api.FullScreenIntentPermissionsState
import com.zenobia.app.libraries.fullscreenintent.api.aFullScreenIntentPermissionsState
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun FullScreenIntentPermissionBanner(
    state: FullScreenIntentPermissionsState,
    modifier: Modifier = Modifier
) {
    Announcement(
        title = stringResource(R.string.full_screen_intent_banner_title),
        description = stringResource(R.string.full_screen_intent_banner_message),
        type = AnnouncementType.Actionable(
            actionText = stringResource(CommonStrings.action_continue),
            onDismissClick = { state.eventSink(FullScreenIntentPermissionsEvents.Dismiss) },
            onActionClick = { state.eventSink(FullScreenIntentPermissionsEvents.OpenSettings) },
        ),
        modifier = modifier.roomListBannerPadding(),
    )
}

@PreviewsDayNight
@Composable
internal fun FullScreenIntentPermissionBannerPreview() {
    ZenobiaPreview {
        FullScreenIntentPermissionBanner(aFullScreenIntentPermissionsState())
    }
}
