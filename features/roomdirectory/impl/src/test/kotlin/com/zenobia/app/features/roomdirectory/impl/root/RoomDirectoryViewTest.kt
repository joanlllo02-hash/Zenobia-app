/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalTestApi::class)

package com.zenobia.app.features.roomdirectory.impl.root

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runAndroidComposeUiTest
import com.zenobia.app.features.roomdirectory.api.RoomDescription
import com.zenobia.app.libraries.testtags.TestTags
import com.zenobia.app.tests.testutils.EnsureNeverCalled
import com.zenobia.app.tests.testutils.EnsureNeverCalledWithParam
import com.zenobia.app.tests.testutils.EventsRecorder
import com.zenobia.app.tests.testutils.ensureCalledOnceWithParam
import com.zenobia.app.tests.testutils.robolectric.RobolectricTest
import org.junit.Test

class RoomDirectoryViewTest : RobolectricTest() {
    @Test
    fun `typing text in search field emits the expected Event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<RoomDirectoryEvents>()
        setRoomDirectoryView(
            state = aRoomDirectoryState(
                eventSink = eventsRecorder,
            )
        )
        onNodeWithTag(TestTags.searchTextField.value).performTextInput(
            text = "Test"
        )
        eventsRecorder.assertSingle(RoomDirectoryEvents.Search("Test"))
    }

    @Test
    fun `clicking on room item then onResultClick lambda is called once`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<RoomDirectoryEvents>()
        val state = aRoomDirectoryState(
            roomDescriptions = aRoomDescriptionList(),
            eventSink = eventsRecorder,
        )
        val clickedRoom = state.roomDescriptions.first()
        ensureCalledOnceWithParam(clickedRoom) { callback ->
            setRoomDirectoryView(
                state = state,
                onResultClick = callback,
            )
            onNodeWithText(clickedRoom.computedName).performClick()
        }
    }

    @Test
    fun `composing load more indicator emits expected Event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<RoomDirectoryEvents>()
        val state = aRoomDirectoryState(
            displayLoadMoreIndicator = true,
            eventSink = eventsRecorder,
        )
        setRoomDirectoryView(state = state)
        eventsRecorder.assertSingle(RoomDirectoryEvents.LoadMore)
    }
}

private fun AndroidComposeUiTest<ComponentActivity>.setRoomDirectoryView(
    state: RoomDirectoryState,
    onBackClick: () -> Unit = EnsureNeverCalled(),
    onResultClick: (RoomDescription) -> Unit = EnsureNeverCalledWithParam(),
) {
    setContent {
        RoomDirectoryView(
            state = state,
            onResultClick = onResultClick,
            onBackClick = onBackClick,
        )
    }
}
