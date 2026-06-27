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
import com.zenobia.app.libraries.push.api.battery.BatteryOptimizationEvents
import com.zenobia.app.libraries.push.api.battery.BatteryOptimizationState
import com.zenobia.app.libraries.push.api.battery.aBatteryOptimizationState

@Composable
internal fun BatteryOptimizationBanner(
    state: BatteryOptimizationState,
    modifier: Modifier = Modifier,
) {
    Announcement(
        modifier = modifier.roomListBannerPadding(),
        title = stringResource(R.string.banner_battery_optimization_title_android),
        description = stringResource(R.string.banner_battery_optimization_content_android),
        type = AnnouncementType.Actionable(
            actionText = stringResource(R.string.banner_battery_optimization_submit_android),
            onActionClick = { state.eventSink(BatteryOptimizationEvents.RequestDisableOptimizations) },
            onDismissClick = { state.eventSink(BatteryOptimizationEvents.Dismiss) },
        ),
    )
}

@PreviewsDayNight
@Composable
internal fun BatteryOptimizationBannerPreview() = ZenobiaPreview {
    BatteryOptimizationBanner(
        state = aBatteryOptimizationState(),
    )
}
