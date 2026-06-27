/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.login.impl.screens.classic.loginwithclassic

import android.graphics.Bitmap
import androidx.compose.runtime.Stable
import com.zenobia.app.features.login.impl.login.LoginMode
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.matrix.api.core.UserId

@Stable
data class LoginWithClassicState(
    val isElementPro: Boolean,
    val userId: UserId,
    val displayName: String?,
    val avatar: Bitmap?,
    val loginWithClassicAction: AsyncAction<Unit>,
    val loginMode: AsyncData<LoginMode>,
    val eventSink: (LoginWithClassicEvent) -> Unit,
)
