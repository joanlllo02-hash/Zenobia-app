/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalTestApi::class)

package com.zenobia.app.features.forward.impl

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runAndroidComposeUiTest
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.test.AN_EXCEPTION
import com.zenobia.app.libraries.matrix.test.A_ROOM_ID
import com.zenobia.app.libraries.testtags.TestTags
import com.zenobia.app.tests.testutils.EnsureNeverCalledWithParam
import com.zenobia.app.tests.testutils.EventsRecorder
import com.zenobia.app.tests.testutils.ensureCalledOnceWithParam
import com.zenobia.app.tests.testutils.pressTag
import com.zenobia.app.tests.testutils.robolectric.RobolectricTest
import org.junit.Test

class ForwardMessagesViewTest : RobolectricTest() {
    @Test
    fun `cancel error emits the expected event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<ForwardMessagesEvents>()
        setForwardMessagesView(
            aForwardMessagesState(
                forwardAction = AsyncAction.Failure(AN_EXCEPTION),
                eventSink = eventsRecorder
            ),
        )
        pressTag(TestTags.dialogPositive.value)
        eventsRecorder.assertSingle(ForwardMessagesEvents.ClearError)
    }

    @Test
    fun `success invokes onForwardSuccess`() = runAndroidComposeUiTest {
        val data = listOf(A_ROOM_ID)
        val eventsRecorder = EventsRecorder<ForwardMessagesEvents>(expectEvents = false)
        ensureCalledOnceWithParam<List<RoomId>?>(data) { callback ->
            setForwardMessagesView(
                aForwardMessagesState(
                    forwardAction = AsyncAction.Success(data),
                    eventSink = eventsRecorder
                ),
                onForwardSuccess = callback,
            )
        }
    }
}

private fun AndroidComposeUiTest<ComponentActivity>.setForwardMessagesView(
    state: ForwardMessagesState,
    onForwardSuccess: (List<RoomId>) -> Unit = EnsureNeverCalledWithParam(),
) {
    setContent {
        ForwardMessagesView(
            state = state,
            onForwardSuccess = onForwardSuccess,
        )
    }
}
