/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.signedout.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import com.zenobia.app.libraries.architecture.Presenter
import com.zenobia.app.libraries.core.meta.BuildMeta
import com.zenobia.app.libraries.matrix.api.core.SessionId
import com.zenobia.app.libraries.sessionstorage.api.SessionStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AssistedInject
class SignedOutPresenter(
    @Assisted private val sessionId: SessionId,
    private val sessionStore: SessionStore,
    private val buildMeta: BuildMeta,
) : Presenter<SignedOutState> {
    @AssistedFactory
    fun interface Factory {
        fun create(sessionId: SessionId): SignedOutPresenter
    }

    @Composable
    override fun present(): SignedOutState {
        val signedOutSession by remember {
            sessionStore.sessionsFlow().map { sessions ->
                sessions.firstOrNull { it.userId == sessionId.value }
            }
        }.collectAsState(initial = null)
        val coroutineScope = rememberCoroutineScope()

        fun handleEvent(event: SignedOutEvents) {
            when (event) {
                SignedOutEvents.SignInAgain -> coroutineScope.launch {
                    sessionStore.removeSession(sessionId.value)
                }
            }
        }

        return SignedOutState(
            appName = buildMeta.applicationName,
            signedOutSession = signedOutSession,
            eventSink = ::handleEvent,
        )
    }
}
