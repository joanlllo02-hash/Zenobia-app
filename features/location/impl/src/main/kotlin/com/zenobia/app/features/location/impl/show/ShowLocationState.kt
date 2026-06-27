/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.location.impl.show

import com.zenobia.app.features.location.api.Location
import com.zenobia.app.features.location.impl.common.ui.LocationConstraintsDialogState
import com.zenobia.app.features.location.impl.common.ui.LocationMarkerData
import com.zenobia.app.features.location.impl.common.userlocation.UserLocationState
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.designsystem.components.PinVariant
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarData
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.api.room.location.AssetType
import kotlinx.collections.immutable.ImmutableList

data class ShowLocationState(
    val customMapStyleUrl: AsyncData<String?>,
    val isLive: Boolean,
    val dialogState: LocationConstraintsDialogState,
    val locationShares: ImmutableList<LocationShareItem>,
    val focusedLocation: LocationShareItem?,
    val isTrackMyLocation: Boolean,
    val userLocationState: UserLocationState,
    val appName: String,
    val hideUserLocationPuck: Boolean,
    val eventSink: (ShowLocationEvent) -> Unit,
) {
    val isSheetDraggable = isLive && locationShares.isNotEmpty()
}

data class LocationShareItem(
    val userId: UserId,
    val displayName: String,
    val avatarData: AvatarData,
    val formattedTimestamp: String,
    val location: Location,
    val isLive: Boolean,
    val assetType: AssetType?,
    val isOwnUser: Boolean
) {
    val canStopSharing = isLive && isOwnUser
}

fun LocationShareItem.toMarkerData(): LocationMarkerData {
    val pinVariant = if (assetType == AssetType.PIN) {
        PinVariant.PinnedLocation
    } else {
        PinVariant.UserLocation(
            avatarData = avatarData,
            isLive = isLive,
        )
    }
    return LocationMarkerData(
        id = userId.value,
        location = location,
        variant = pinVariant,
    )
}
