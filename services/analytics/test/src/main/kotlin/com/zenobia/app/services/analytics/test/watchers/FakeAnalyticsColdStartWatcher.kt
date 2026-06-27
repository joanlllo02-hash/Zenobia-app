/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.services.analytics.test.watchers

import com.zenobia.app.services.analytics.api.watchers.AnalyticsColdStartWatcher

class FakeAnalyticsColdStartWatcher : AnalyticsColdStartWatcher {
    override fun start() {}
    override fun whenLoggingIn() {}
    override fun onRoomListVisible() {}
}
