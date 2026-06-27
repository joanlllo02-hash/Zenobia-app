/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalTestApi::class)

package com.zenobia.app.features.logout.impl.direct

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runAndroidComposeUiTest
import com.zenobia.app.features.logout.api.direct.DirectLogoutEvents
import com.zenobia.app.features.logout.api.direct.DirectLogoutState
import com.zenobia.app.features.logout.api.direct.aDirectLogoutState
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.ui.strings.CommonStrings
import com.zenobia.app.tests.testutils.EventsRecorder
import com.zenobia.app.tests.testutils.clickOn
import com.zenobia.app.tests.testutils.pressBackKey
import com.zenobia.app.tests.testutils.robolectric.RobolectricTest
import org.junit.Ignore
import org.junit.Test

class DefaultDirectLogoutViewTest : RobolectricTest() {
    @Test
    fun `clicking on confirm logout sends expected Event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<DirectLogoutEvents>()
        setDefaultDirectLogoutView(
            state = aDirectLogoutState(
                logoutAction = AsyncAction.ConfirmingNoParams,
                eventSink = eventsRecorder,
            )
        )
        clickOn(CommonStrings.action_signout)
        eventsRecorder.assertSingle(DirectLogoutEvents.Logout(false))
    }

    @Test
    fun `clicking on cancel logout sends expected Event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<DirectLogoutEvents>()
        setDefaultDirectLogoutView(
            state = aDirectLogoutState(
                logoutAction = AsyncAction.ConfirmingNoParams,
                eventSink = eventsRecorder,
            )
        )
        clickOn(CommonStrings.action_cancel)
        eventsRecorder.assertSingle(DirectLogoutEvents.CloseDialogs)
    }

    @Ignore("Pressing back key should dismiss the dialog, and so generate the expected event, but it's not the case.")
    @Test
    fun `clicking on back invoke back callback`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<DirectLogoutEvents>()
        setDefaultDirectLogoutView(
            state = aDirectLogoutState(
                logoutAction = AsyncAction.ConfirmingNoParams,
                eventSink = eventsRecorder,
            )
        )
        pressBackKey()
        eventsRecorder.assertSingle(DirectLogoutEvents.CloseDialogs)
    }

    @Test
    fun `clicking on confirm after error sends expected Event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<DirectLogoutEvents>()
        setDefaultDirectLogoutView(
            state = aDirectLogoutState(
                logoutAction = AsyncAction.Failure(Exception("Error")),
                eventSink = eventsRecorder,
            )
        )
        clickOn(CommonStrings.action_signout_anyway)
        eventsRecorder.assertSingle(DirectLogoutEvents.Logout(true))
    }

    @Test
    fun `clicking on cancel after error sends expected Event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<DirectLogoutEvents>()
        setDefaultDirectLogoutView(
            state = aDirectLogoutState(
                logoutAction = AsyncAction.Failure(Exception("Error")),
                eventSink = eventsRecorder,
            )
        )
        clickOn(CommonStrings.action_cancel)
        eventsRecorder.assertSingle(DirectLogoutEvents.CloseDialogs)
    }
}

private fun AndroidComposeUiTest<ComponentActivity>.setDefaultDirectLogoutView(
    state: DirectLogoutState,
) {
    setContent {
        DefaultDirectLogoutView().Render(state)
    }
}
