/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.roomselect.impl

import androidx.compose.foundation.text.input.TextFieldState
import com.zenobia.app.libraries.designsystem.theme.components.SearchBarResultState
import com.zenobia.app.libraries.matrix.ui.model.SelectRoomInfo
import com.zenobia.app.libraries.roomselect.api.RoomSelectMode
import kotlinx.collections.immutable.ImmutableList

data class RoomSelectState(
    val mode: RoomSelectMode,
    val maxNumberOfRooms: Int,
    val resultState: SearchBarResultState<ImmutableList<SelectRoomInfo>>,
    val searchQuery: TextFieldState,
    val isSearchActive: Boolean,
    val selectedRooms: ImmutableList<SelectRoomInfo>,
    val eventSink: (RoomSelectEvent) -> Unit,
) {
    val canSelectMoreRooms = selectedRooms.size < maxNumberOfRooms
}
