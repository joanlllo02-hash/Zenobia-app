/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.share.test

import android.content.Intent
import com.zenobia.app.features.share.api.ShareIntentData
import com.zenobia.app.features.share.api.ShareIntentHandler

class FakeShareIntentHandler(
    private val onIncomingShareIntent: (Intent) -> ShareIntentData? = { null },
) : ShareIntentHandler {
    override fun handleIncomingShareIntent(
        intent: Intent,
    ): ShareIntentData? {
        return onIncomingShareIntent(intent)
    }
}
