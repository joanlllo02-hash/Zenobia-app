/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.home.impl.datasource

import com.zenobia.app.libraries.dateformatter.api.DateFormatter
import com.zenobia.app.libraries.dateformatter.test.FakeDateFormatter
import com.zenobia.app.libraries.eventformatter.api.RoomLatestEventFormatter
import com.zenobia.app.libraries.eventformatter.test.FakeRoomLatestEventFormatter

fun aRoomListRoomSummaryFactory(
    dateFormatter: DateFormatter = FakeDateFormatter { _, _, _ -> "Today" },
    roomLatestEventFormatter: RoomLatestEventFormatter = FakeRoomLatestEventFormatter(),
) = RoomListRoomSummaryFactory(
    dateFormatter = dateFormatter,
    roomLatestEventFormatter = roomLatestEventFormatter,
)
