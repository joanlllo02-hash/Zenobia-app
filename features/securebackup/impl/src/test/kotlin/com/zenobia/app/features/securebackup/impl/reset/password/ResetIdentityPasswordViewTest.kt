/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalTestApi::class)

package com.zenobia.app.features.securebackup.impl.reset.password

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runAndroidComposeUiTest
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.ui.strings.CommonStrings
import com.zenobia.app.tests.testutils.EnsureNeverCalled
import com.zenobia.app.tests.testutils.EventsRecorder
import com.zenobia.app.tests.testutils.clickOn
import com.zenobia.app.tests.testutils.ensureCalledOnce
import com.zenobia.app.tests.testutils.pressBack
import com.zenobia.app.tests.testutils.pressBackKey
import com.zenobia.app.tests.testutils.robolectric.RobolectricTest
import org.junit.Test

class ResetIdentityPasswordViewTest : RobolectricTest() {
    @Test
    fun `pressing the back HW button invokes the expected callback`() = runAndroidComposeUiTest {
        ensureCalledOnce {
            setResetPasswordView(
                ResetIdentityPasswordState(resetAction = AsyncAction.Uninitialized, eventSink = {}),
                onBack = it,
            )
            pressBackKey()
        }
    }

    @Test
    fun `clicking on the back navigation button invokes the expected callback`() = runAndroidComposeUiTest {
        ensureCalledOnce {
            setResetPasswordView(
                ResetIdentityPasswordState(resetAction = AsyncAction.Uninitialized, eventSink = {}),
                onBack = it,
            )
            pressBack()
        }
    }

    @Test
    fun `clicking 'Reset identity' confirms the reset`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<ResetIdentityPasswordEvent>()
        setResetPasswordView(
            ResetIdentityPasswordState(resetAction = AsyncAction.Uninitialized, eventSink = eventsRecorder),
        )
        onNodeWithText("Password").performTextInput("A password")

        clickOn(CommonStrings.action_reset_identity)

        eventsRecorder.assertSingle(ResetIdentityPasswordEvent.Reset("A password"))
    }

    @Test
    fun `modifying the password dismisses the error state`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<ResetIdentityPasswordEvent>()
        setResetPasswordView(
            ResetIdentityPasswordState(resetAction = AsyncAction.Failure(IllegalStateException("A failure")), eventSink = eventsRecorder),
        )
        onNodeWithText("Password").performTextInput("A password")

        eventsRecorder.assertSingle(ResetIdentityPasswordEvent.DismissError)
    }
}

private fun AndroidComposeUiTest<ComponentActivity>.setResetPasswordView(
    state: ResetIdentityPasswordState,
    onBack: () -> Unit = EnsureNeverCalled(),
) {
    setContent {
        ResetIdentityPasswordView(state = state, onBack = onBack)
    }
}
