/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalTestApi::class)

package com.zenobia.app.features.reportroom.impl

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runAndroidComposeUiTest
import com.zenobia.app.libraries.ui.strings.CommonStrings
import com.zenobia.app.tests.testutils.EnsureNeverCalled
import com.zenobia.app.tests.testutils.EventsRecorder
import com.zenobia.app.tests.testutils.clickOn
import com.zenobia.app.tests.testutils.ensureCalledOnce
import com.zenobia.app.tests.testutils.pressBack
import com.zenobia.app.tests.testutils.robolectric.RobolectricTest
import org.junit.Test

class ReportRoomViewTest : RobolectricTest() {
    @Test
    fun `clicking on back invoke the expected callback`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<ReportRoomEvents>(expectEvents = false)
        ensureCalledOnce {
            setReportRoomView(
                aReportRoomState(
                    eventSink = eventsRecorder,
                ),
                onBackClick = it
            )
            pressBack()
        }
    }

    @Test
    fun `clicking on report when enabled emits the expected event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<ReportRoomEvents>()
        setReportRoomView(
            aReportRoomState(
                reason = "Spam",
                eventSink = eventsRecorder,
            ),
        )
        clickOn(CommonStrings.action_report)
        eventsRecorder.assertSingle(ReportRoomEvents.Report)
    }

    @Test
    fun `clicking on decline when disabled does not emit event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<ReportRoomEvents>(expectEvents = false)
        setReportRoomView(
            aReportRoomState(eventSink = eventsRecorder),
        )
        clickOn(CommonStrings.action_report)
    }

    @Test
    fun `clicking on leave room option emits the expected event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<ReportRoomEvents>()
        setReportRoomView(
            aReportRoomState(eventSink = eventsRecorder),
        )
        clickOn(CommonStrings.action_leave_room)
        eventsRecorder.assertSingle(ReportRoomEvents.ToggleLeaveRoom)
    }

    @Test
    fun `typing text in the reason field emits the expected Event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<ReportRoomEvents>()
        setReportRoomView(
            aReportRoomState(
                eventSink = eventsRecorder,
                reason = ""
            ),
        )
        onNodeWithText("").performTextInput("Spam!")
        eventsRecorder.assertSingle(ReportRoomEvents.UpdateReason("Spam!"))
    }
}

private fun AndroidComposeUiTest<ComponentActivity>.setReportRoomView(
    state: ReportRoomState,
    onBackClick: () -> Unit = EnsureNeverCalled(),
) {
    setContent {
        ReportRoomView(
            state = state,
            onBackClick = onBackClick,
        )
    }
}
