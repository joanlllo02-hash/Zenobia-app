/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalTestApi::class)

package com.zenobia.app.features.logout.impl

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runAndroidComposeUiTest
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.testtags.TestTags
import com.zenobia.app.libraries.ui.strings.CommonStrings
import com.zenobia.app.tests.testutils.EnsureNeverCalled
import com.zenobia.app.tests.testutils.EventsRecorder
import com.zenobia.app.tests.testutils.clickOn
import com.zenobia.app.tests.testutils.ensureCalledOnce
import com.zenobia.app.tests.testutils.pressBack
import com.zenobia.app.tests.testutils.pressTag
import com.zenobia.app.tests.testutils.robolectric.RobolectricTest
import org.junit.Test

class LogoutViewTest : RobolectricTest() {
    @Test
    fun `clicking on logout sends a LogoutEvents`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<LogoutEvents>()
        setLogoutView(
            aLogoutState(
                eventSink = eventsRecorder
            ),
        )
        clickOn(CommonStrings.action_signout)
        eventsRecorder.assertSingle(LogoutEvents.Logout(false))
    }

    @Test
    fun `confirming logout sends a LogoutEvents`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<LogoutEvents>()
        setLogoutView(
            aLogoutState(
                logoutAction = AsyncAction.ConfirmingNoParams,
                eventSink = eventsRecorder
            ),
        )
        pressTag(TestTags.dialogPositive.value)
        eventsRecorder.assertSingle(LogoutEvents.Logout(false))
    }

    @Test
    fun `clicking on back invoke back callback`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<LogoutEvents>(expectEvents = false)
        ensureCalledOnce { callback ->
            setLogoutView(
                aLogoutState(
                    eventSink = eventsRecorder
                ),
                onBackClick = callback,
            )
            pressBack()
        }
    }

    @Test
    fun `clicking on confirm after error sends a LogoutEvents`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<LogoutEvents>()
        setLogoutView(
            aLogoutState(
                logoutAction = AsyncAction.Failure(Exception("Failed to logout")),
                eventSink = eventsRecorder
            ),
        )
        clickOn(CommonStrings.action_signout_anyway)
        eventsRecorder.assertSingle(LogoutEvents.Logout(true))
    }

    @Test
    fun `clicking on cancel after error sends a LogoutEvents`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<LogoutEvents>()
        setLogoutView(
            aLogoutState(
                logoutAction = AsyncAction.Failure(Exception("Failed to logout")),
                eventSink = eventsRecorder
            ),
        )
        clickOn(CommonStrings.action_cancel)
        eventsRecorder.assertSingle(LogoutEvents.CloseDialogs)
    }

    @Test
    fun `last session setting button invoke onChangeRecoveryKeyClicked`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<LogoutEvents>(expectEvents = false)
        ensureCalledOnce { callback ->
            setLogoutView(
                aLogoutState(
                    isLastDevice = true,
                    eventSink = eventsRecorder
                ),
                onChangeRecoveryKeyClick = callback,
            )
            clickOn(CommonStrings.common_settings)
        }
    }
}

private fun AndroidComposeUiTest<ComponentActivity>.setLogoutView(
    state: LogoutState,
    onChangeRecoveryKeyClick: () -> Unit = EnsureNeverCalled(),
    onBackClick: () -> Unit = EnsureNeverCalled(),
) {
    setContent {
        LogoutView(
            state = state,
            onChangeRecoveryKeyClick = onChangeRecoveryKeyClick,
            onBackClick = onBackClick,
        )
    }
}
