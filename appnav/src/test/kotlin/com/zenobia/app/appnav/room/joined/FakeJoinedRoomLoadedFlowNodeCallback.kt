/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.appnav.room.joined

import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.permalink.PermalinkData
import com.zenobia.app.tests.testutils.lambda.lambdaError

class FakeJoinedRoomLoadedFlowNodeCallback : JoinedRoomLoadedFlowNode.Callback {
    override fun onDone() = lambdaError()
    override fun navigateToRoom(roomId: RoomId, serverNames: List<String>, clearBackStack: Boolean) = lambdaError()
    override fun handlePermalinkClick(data: PermalinkData, pushToBackstack: Boolean) = lambdaError()
    override fun navigateToGlobalNotificationSettings() = lambdaError()
    override fun navigateToDeveloperSettings() = lambdaError()
}
