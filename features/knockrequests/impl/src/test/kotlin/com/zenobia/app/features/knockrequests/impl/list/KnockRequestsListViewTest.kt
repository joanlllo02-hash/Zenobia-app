/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalTestApi::class)

package com.zenobia.app.features.knockrequests.impl.list

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runAndroidComposeUiTest
import com.zenobia.app.features.knockrequests.impl.R
import com.zenobia.app.features.knockrequests.impl.data.aKnockRequestPresentable
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.ui.strings.CommonStrings
import com.zenobia.app.tests.testutils.EnsureNeverCalled
import com.zenobia.app.tests.testutils.EventsRecorder
import com.zenobia.app.tests.testutils.clickOn
import com.zenobia.app.tests.testutils.ensureCalledOnce
import com.zenobia.app.tests.testutils.pressBack
import com.zenobia.app.tests.testutils.robolectric.RobolectricTest
import kotlinx.collections.immutable.persistentListOf
import org.junit.Test

class KnockRequestsListViewTest : RobolectricTest() {
    @Test
    fun `clicking on back invoke the expected callback`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<KnockRequestsListEvents>(expectEvents = false)
        ensureCalledOnce {
            setKnockRequestsListView(
                aKnockRequestsListState(
                    eventSink = eventsRecorder,
                ),
                onBackClick = it
            )
            pressBack()
        }
    }

    @Test
    fun `clicking on accept emit the expected event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<KnockRequestsListEvents>()
        val knockRequest = aKnockRequestPresentable()
        setKnockRequestsListView(
            aKnockRequestsListState(
                knockRequests = AsyncData.Success(persistentListOf(knockRequest)),
                eventSink = eventsRecorder,
            ),
        )
        clickOn(CommonStrings.action_accept)
        eventsRecorder.assertSingle(KnockRequestsListEvents.Accept(knockRequest))
    }

    @Test
    fun `clicking on decline emit the expected event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<KnockRequestsListEvents>()
        val knockRequest = aKnockRequestPresentable()
        setKnockRequestsListView(
            aKnockRequestsListState(
                knockRequests = AsyncData.Success(persistentListOf(knockRequest)),
                eventSink = eventsRecorder,
            ),
        )
        clickOn(CommonStrings.action_decline)
        eventsRecorder.assertSingle(KnockRequestsListEvents.Decline(knockRequest))
    }

    @Test
    fun `clicking on decline and ban emit the expected event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<KnockRequestsListEvents>()
        val knockRequest = aKnockRequestPresentable()
        setKnockRequestsListView(
            aKnockRequestsListState(
                knockRequests = AsyncData.Success(persistentListOf(knockRequest)),
                eventSink = eventsRecorder,
            ),
        )
        clickOn(R.string.screen_knock_requests_list_decline_and_ban_action_title)
        eventsRecorder.assertSingle(KnockRequestsListEvents.DeclineAndBan(knockRequest))
    }

    @Test
    fun `clicking on accept all emit the expected event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<KnockRequestsListEvents>()
        val knockRequests = persistentListOf(aKnockRequestPresentable(), aKnockRequestPresentable())
        setKnockRequestsListView(
            aKnockRequestsListState(
                knockRequests = AsyncData.Success(knockRequests),
                eventSink = eventsRecorder,
            ),
        )
        clickOn(R.string.screen_knock_requests_list_accept_all_button_title)
        eventsRecorder.assertSingle(KnockRequestsListEvents.AcceptAll)
    }

    @Test
    fun `retry on async view retry emit the expected event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<KnockRequestsListEvents>()
        val knockRequests = persistentListOf(aKnockRequestPresentable(), aKnockRequestPresentable())
        setKnockRequestsListView(
            aKnockRequestsListState(
                knockRequests = AsyncData.Success(knockRequests),
                asyncAction = AsyncAction.Failure(RuntimeException("Failed to accept all")),
                currentAction = KnockRequestsAction.AcceptAll,
                eventSink = eventsRecorder,
            ),
        )
        clickOn(CommonStrings.action_retry)
        eventsRecorder.assertSingle(KnockRequestsListEvents.RetryCurrentAction)
    }

    @Test
    fun `canceling async view emit the expected event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<KnockRequestsListEvents>()
        val knockRequests = persistentListOf(aKnockRequestPresentable(), aKnockRequestPresentable())
        setKnockRequestsListView(
            aKnockRequestsListState(
                knockRequests = AsyncData.Success(knockRequests),
                asyncAction = AsyncAction.Failure(RuntimeException("Failed to accept all")),
                currentAction = KnockRequestsAction.AcceptAll,
                eventSink = eventsRecorder,
            ),
        )
        clickOn(CommonStrings.action_cancel)
        eventsRecorder.assertSingle(KnockRequestsListEvents.ResetCurrentAction)
    }

    @Test
    fun `confirming async view emit the expected event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<KnockRequestsListEvents>()
        val knockRequests = persistentListOf(aKnockRequestPresentable(), aKnockRequestPresentable())
        setKnockRequestsListView(
            aKnockRequestsListState(
                knockRequests = AsyncData.Success(knockRequests),
                asyncAction = AsyncAction.ConfirmingNoParams,
                currentAction = KnockRequestsAction.AcceptAll,
                eventSink = eventsRecorder,
            ),
        )
        clickOn(R.string.screen_knock_requests_list_accept_all_alert_confirm_button_title)
        eventsRecorder.assertSingle(KnockRequestsListEvents.ConfirmCurrentAction)
    }
}

private fun AndroidComposeUiTest<ComponentActivity>.setKnockRequestsListView(
    state: KnockRequestsListState,
    onBackClick: () -> Unit = EnsureNeverCalled(),
) {
    setContent {
        KnockRequestsListView(
            state = state,
            onBackClick = onBackClick,
        )
    }
}
