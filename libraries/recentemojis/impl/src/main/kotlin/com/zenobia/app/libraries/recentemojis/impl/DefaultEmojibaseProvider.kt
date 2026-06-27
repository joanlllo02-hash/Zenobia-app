/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.recentemojis.impl

import android.content.Context
import com.zenobia.app.emojibasebindings.EmojibaseDatasource
import com.zenobia.app.emojibasebindings.EmojibaseStore
import com.zenobia.app.libraries.recentemojis.api.EmojibaseProvider

class DefaultEmojibaseProvider(val context: Context) : EmojibaseProvider {
    override val emojibaseStore: EmojibaseStore by lazy {
        EmojibaseDatasource().load(context)
    }
}
