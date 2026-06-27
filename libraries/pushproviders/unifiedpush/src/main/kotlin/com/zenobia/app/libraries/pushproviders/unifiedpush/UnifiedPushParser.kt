/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.pushproviders.unifiedpush

import dev.zacsweers.metro.Inject
import com.zenobia.app.libraries.androidutils.json.JsonProvider
import com.zenobia.app.libraries.core.data.tryOrNull
import com.zenobia.app.libraries.pushproviders.api.PushData

@Inject
class UnifiedPushParser(
    private val json: JsonProvider,
) {
    fun parse(message: ByteArray, clientSecret: String): PushData? {
        return tryOrNull { json().decodeFromString<PushDataUnifiedPush>(String(message)) }?.toPushData(clientSecret)
    }
}
