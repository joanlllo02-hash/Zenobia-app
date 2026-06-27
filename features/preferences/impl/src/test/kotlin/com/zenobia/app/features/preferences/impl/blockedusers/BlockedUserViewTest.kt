/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalTestApi::class)

package com.zenobia.app.features.preferences.impl.blockedusers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runAndroidComposeUiTest
import com.zenobia.app.features.preferences.impl.R
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.matrix.ui.components.aMatrixUserList
import com.zenobia.app.libraries.ui.strings.CommonStrings
import com.zenobia.app.tests.testutils.EnsureNeverCalled
import com.zenobia.app.tests.testutils.EventsRecorder
import com.zenobia.app.tests.testutils.clickOn
import com.zenobia.app.tests.testutils.ensureCalledOnce
import com.zenobia.app.tests.testutils.pressBack
import com.zenobia.app.tests.testutils.robolectric.RobolectricTest
import org.junit.Test

class BlockedUserViewTest : RobolectricTest() {
    @Test
    fun `clicking on back invokes back callback`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<BlockedUsersEvents>(expectEvents = false)
        ensureCalledOnce { callback ->
            setBlockedUsersView(
                aBlockedUsersState(
                    eventSink = eventsRecorder
                ),
                onBackClick = callback,
            )
            pressBack()
        }
    }

    @Test
    fun `clicking on a user emits the expected Event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<BlockedUsersEvents>()
        val userList = aMatrixUserList()
        setBlockedUsersView(
            aBlockedUsersState(
                blockedUsers = userList,
                eventSink = eventsRecorder
            ),
        )
        onNodeWithText(userList.first().displayName.orEmpty()).performClick()
        eventsRecorder.assertSingle(BlockedUsersEvents.Unblock(userList.first().userId))
    }

    @Test
    fun `clicking on cancel sends a BlockedUsersEvents`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<BlockedUsersEvents>()
        setBlockedUsersView(
            aBlockedUsersState(
                unblockUserAction = AsyncAction.ConfirmingNoParams,
                eventSink = eventsRecorder
            ),
        )
        clickOn(CommonStrings.action_cancel)
        eventsRecorder.assertSingle(BlockedUsersEvents.Cancel)
    }

    @Test
    fun `clicking on confirm sends a BlockedUsersEvents`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<BlockedUsersEvents>()
        setBlockedUsersView(
            aBlockedUsersState(
                unblockUserAction = AsyncAction.ConfirmingNoParams,
                eventSink = eventsRecorder
            ),
        )
        clickOn(R.string.screen_blocked_users_unblock_alert_action)
        eventsRecorder.assertSingle(BlockedUsersEvents.ConfirmUnblock)
    }
}

private fun AndroidComposeUiTest<ComponentActivity>.setBlockedUsersView(
    state: BlockedUsersState,
    onBackClick: () -> Unit = EnsureNeverCalled(),
) {
    setContent {
        BlockedUsersView(
            state = state,
            onBackClick = onBackClick,
        )
    }
}
