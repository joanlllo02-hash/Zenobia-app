/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalTestApi::class)

package com.zenobia.app.features.poll.impl.history

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runAndroidComposeUiTest
import com.zenobia.app.features.poll.api.pollcontent.aPollContentState
import com.zenobia.app.features.poll.impl.R
import com.zenobia.app.features.poll.impl.history.model.PollHistoryFilter
import com.zenobia.app.libraries.matrix.api.core.EventId
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
import org.robolectric.annotation.Config

class PollHistoryViewTest : RobolectricTest() {
    @Test
    fun `clicking on back invokes the expected callback`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<PollHistoryEvents>(expectEvents = false)
        ensureCalledOnce {
            setPollHistoryViewView(
                aPollHistoryState(
                    eventSink = eventsRecorder
                ),
                goBack = it
            )
            pressBack()
        }
    }

    @Config(qualifiers = "h1024dp")
    @Test
    fun `clicking on edit poll invokes the expected callback`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<PollHistoryEvents>(expectEvents = false)
        val eventId = EventId("\$anEventId")
        val state = aPollHistoryState(
            currentItems = listOf(
                aPollHistoryItem(
                    state = aPollContentState(
                        eventId = eventId,
                        isMine = true,
                        isEnded = false,
                    )
                )
            ),
            eventSink = eventsRecorder
        )
        ensureCalledOnceWithParam(eventId) {
            setPollHistoryViewView(
                state = state,
                onEditPoll = it
            )
            clickOn(CommonStrings.action_edit_poll)
        }
    }

    @Config(qualifiers = "h1024dp")
    @Test
    fun `clicking on poll end emits the expected Event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<PollHistoryEvents>()
        val eventId = EventId("\$anEventId")
        val state = aPollHistoryState(
            currentItems = listOf(
                aPollHistoryItem(
                    state = aPollContentState(
                        eventId = eventId,
                        isMine = true,
                        isEnded = false,
                        isPollEditable = false,
                    )
                )
            ),
            eventSink = eventsRecorder
        )
        setPollHistoryViewView(
            state = state,
        )
        clickOn(CommonStrings.action_end_poll)
        // Cancel the dialog
        clickOn(CommonStrings.action_cancel)
        // Do it again, and confirm the dialog
        clickOn(CommonStrings.action_end_poll)
        eventsRecorder.assertEmpty()
        clickOn(CommonStrings.action_ok)
        eventsRecorder.assertSingle(
            PollHistoryEvents.EndPoll(eventId)
        )
    }

    @Config(qualifiers = "h1024dp")
    @Test
    fun `clicking on poll answer emits the expected Event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<PollHistoryEvents>()
        val eventId = EventId("\$anEventId")
        val state = aPollHistoryState(
            currentItems = listOf(
                aPollHistoryItem(
                    state = aPollContentState(
                        eventId = eventId,
                        isMine = true,
                        isEnded = false,
                        isPollEditable = false,
                    )
                )
            ),
            eventSink = eventsRecorder
        )
        val answer = state.pollHistoryItems.ongoing.first().state.answerItems.first().answer
        setPollHistoryViewView(
            state = state,
        )
        onNodeWithText(
            text = answer.text,
            useUnmergedTree = true,
        ).performClick()
        eventsRecorder.assertSingle(
            PollHistoryEvents.SelectPollAnswer(eventId, answer.id)
        )
    }

    @Test
    fun `clicking on past tab emits the expected Event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<PollHistoryEvents>()
        setPollHistoryViewView(
            aPollHistoryState(
                eventSink = eventsRecorder
            ),
        )
        clickOn(R.string.screen_polls_history_filter_past)
        eventsRecorder.assertSingle(
            PollHistoryEvents.SelectFilter(filter = PollHistoryFilter.PAST)
        )
    }

    @Config(qualifiers = "h1024dp")
    @Test
    fun `clicking on load more emits the expected Event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<PollHistoryEvents>()
        setPollHistoryViewView(
            aPollHistoryState(
                hasMoreToLoad = true,
                eventSink = eventsRecorder,
            ),
        )
        clickOn(CommonStrings.action_load_more)
        eventsRecorder.assertSingle(
            PollHistoryEvents.LoadMore
        )
    }
}

private fun AndroidComposeUiTest<ComponentActivity>.setPollHistoryViewView(
    state: PollHistoryState,
    onEditPoll: (EventId) -> Unit = EnsureNeverCalledWithParam(),
    goBack: () -> Unit = EnsureNeverCalled(),
) {
    setContent {
        PollHistoryView(
            state = state,
            onEditPoll = onEditPoll,
            goBack = goBack,
        )
    }
}
