/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalTestApi::class)

package com.zenobia.app.features.userprofile.shared.blockuser

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runAndroidComposeUiTest
import com.zenobia.app.features.userprofile.api.UserProfileEvents
import com.zenobia.app.features.userprofile.api.UserProfileState
import com.zenobia.app.features.userprofile.shared.R
import com.zenobia.app.features.userprofile.shared.aUserProfileState
import com.zenobia.app.libraries.ui.strings.CommonStrings
import com.zenobia.app.tests.testutils.EventsRecorder
import com.zenobia.app.tests.testutils.clickOn
import com.zenobia.app.tests.testutils.robolectric.RobolectricTest
import org.junit.Test

class BlockUserDialogsTest : RobolectricTest() {
    @Test
    fun `confirm block user emit expected Event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<UserProfileEvents>()
        setContent {
            BlockUserDialogs(
                state = aUserProfileState(
                    displayConfirmationDialog = UserProfileState.ConfirmationDialog.Block,
                    eventSink = eventsRecorder,
                )
            )
        }
        clickOn(R.string.screen_dm_details_block_alert_action)
        eventsRecorder.assertSingle(UserProfileEvents.BlockUser(false))
    }

    @Test
    fun `cancel block user emit expected Event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<UserProfileEvents>()
        setContent {
            BlockUserDialogs(
                state = aUserProfileState(
                    displayConfirmationDialog = UserProfileState.ConfirmationDialog.Block,
                    eventSink = eventsRecorder,
                )
            )
        }
        clickOn(CommonStrings.action_cancel)
        eventsRecorder.assertSingle(UserProfileEvents.ClearConfirmationDialog)
    }

    @Test
    fun `confirm unblock user emit expected Event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<UserProfileEvents>()
        setContent {
            BlockUserDialogs(
                state = aUserProfileState(
                    displayConfirmationDialog = UserProfileState.ConfirmationDialog.Unblock,
                    eventSink = eventsRecorder,
                )
            )
        }
        clickOn(R.string.screen_dm_details_unblock_alert_action)
        eventsRecorder.assertSingle(UserProfileEvents.UnblockUser(false))
    }

    @Test
    fun `cancel unblock user emit expected Event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<UserProfileEvents>()
        setContent {
            BlockUserDialogs(
                state = aUserProfileState(
                    displayConfirmationDialog = UserProfileState.ConfirmationDialog.Unblock,
                    eventSink = eventsRecorder,
                )
            )
        }
        clickOn(CommonStrings.action_cancel)
        eventsRecorder.assertSingle(UserProfileEvents.ClearConfirmationDialog)
    }
}
