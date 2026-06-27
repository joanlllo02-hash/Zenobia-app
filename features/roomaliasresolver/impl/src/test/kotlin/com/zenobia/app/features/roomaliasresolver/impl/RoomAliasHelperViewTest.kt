/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalTestApi::class)

package com.zenobia.app.features.roomaliasresolver.impl

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runAndroidComposeUiTest
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.matrix.api.room.alias.ResolvedRoomAlias
import com.zenobia.app.libraries.ui.strings.CommonStrings
import com.zenobia.app.tests.testutils.EnsureNeverCalled
import com.zenobia.app.tests.testutils.EnsureNeverCalledWithParam
import com.zenobia.app.tests.testutils.EventsRecorder
import com.zenobia.app.tests.testutils.clickOn
import com.zenobia.app.tests.testutils.ensureCalledOnce
import com.zenobia.app.tests.testutils.ensureCalledOnceWithParam
import com.zenobia.app.tests.testutils.pressBack
import com.zenobia.app.tests.testutils.robolectric.RobolectricTest
import org.junit.Test

class RoomAliasHelperViewTest : RobolectricTest() {
    @Test
    fun `clicking on back invokes the expected callback`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<RoomAliasResolverEvents>(expectEvents = false)
        ensureCalledOnce {
            setRoomAliasResolverView(
                aRoomAliasResolverState(
                    eventSink = eventsRecorder,
                ),
                onBackClick = it
            )
            pressBack()
        }
    }

    @Test
    fun `clicking on Retry emits the expected Event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<RoomAliasResolverEvents>()
        setRoomAliasResolverView(
            aRoomAliasResolverState(
                resolveState = AsyncData.Failure(Exception("Error")),
                eventSink = eventsRecorder,
            ),
        )
        clickOn(CommonStrings.action_retry)
        eventsRecorder.assertSingle(RoomAliasResolverEvents.Retry)
    }

    @Test
    fun `success state invokes the expected Callback`() = runAndroidComposeUiTest {
        val result = aResolvedRoomAlias()
        val eventsRecorder = EventsRecorder<RoomAliasResolverEvents>(expectEvents = false)
        ensureCalledOnceWithParam(result) {
            setRoomAliasResolverView(
                aRoomAliasResolverState(
                    resolveState = AsyncData.Success(result),
                    eventSink = eventsRecorder,
                ),
                onAliasResolved = it,
            )
        }
    }
}

private fun AndroidComposeUiTest<ComponentActivity>.setRoomAliasResolverView(
    state: RoomAliasResolverState,
    onBackClick: () -> Unit = EnsureNeverCalled(),
    onAliasResolved: (ResolvedRoomAlias) -> Unit = EnsureNeverCalledWithParam(),
) {
    setContent {
        RoomAliasResolverView(
            state = state,
            onBackClick = onBackClick,
            onSuccess = onAliasResolved,
        )
    }
}
