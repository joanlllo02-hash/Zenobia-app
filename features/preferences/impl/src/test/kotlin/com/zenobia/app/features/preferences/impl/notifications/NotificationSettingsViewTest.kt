/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalTestApi::class)

package com.zenobia.app.features.preferences.impl.notifications

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runAndroidComposeUiTest
import com.zenobia.app.features.preferences.impl.R
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.matrix.test.AN_EXCEPTION
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

class NotificationSettingsViewTest : RobolectricTest() {
    @Test
    fun `clicking on back invokes the expected callback`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<NotificationSettingsEvents>()
        ensureCalledOnce {
            setNotificationSettingsView(
                state = aValidNotificationSettingsState(
                    eventSink = eventsRecorder
                ),
                onBackClick = it
            )
            pressBack()
        }
        eventsRecorder.assertSingle(NotificationSettingsEvents.RefreshSystemNotificationsEnabled)
    }

    @Config(qualifiers = "h1280dp")
    @Test
    fun `clicking on troubleshoot notification invokes the expected callback`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<NotificationSettingsEvents>()
        ensureCalledOnce {
            setNotificationSettingsView(
                state = aValidNotificationSettingsState(
                    eventSink = eventsRecorder
                ),
                onTroubleshootNotificationsClick = it
            )
            clickOn(R.string.troubleshoot_notifications_entry_point_title)
        }
        eventsRecorder.assertSingle(NotificationSettingsEvents.RefreshSystemNotificationsEnabled)
    }

    @Config(qualifiers = "h1024dp")
    @Test
    fun `clicking on group chats invokes the expected callback`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<NotificationSettingsEvents>()
        ensureCalledOnceWithParam(false) {
            setNotificationSettingsView(
                state = aValidNotificationSettingsState(
                    eventSink = eventsRecorder
                ),
                onOpenEditDefault = it
            )
            clickOn(R.string.screen_notification_settings_group_chats)
        }
        eventsRecorder.assertSingle(NotificationSettingsEvents.RefreshSystemNotificationsEnabled)
    }

    @Config(qualifiers = "h1024dp")
    @Test
    fun `clicking on direct chats invokes the expected callback`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<NotificationSettingsEvents>()
        ensureCalledOnceWithParam(true) {
            setNotificationSettingsView(
                state = aValidNotificationSettingsState(
                    eventSink = eventsRecorder
                ),
                onOpenEditDefault = it
            )
            clickOn(R.string.screen_notification_settings_direct_chats)
        }
        eventsRecorder.assertSingle(NotificationSettingsEvents.RefreshSystemNotificationsEnabled)
    }

    @Config(qualifiers = "h1024dp")
    @Test
    fun `clicking on disable notifications emits the expected events`() {
        testNotificationToggle(true)
    }

    @Config(qualifiers = "h1024dp")
    @Test
    fun `clicking on enable notifications emits the expected events`() {
        testNotificationToggle(false)
    }

    private fun testNotificationToggle(initialState: Boolean) = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<NotificationSettingsEvents>()
        setNotificationSettingsView(
            state = aValidNotificationSettingsState(
                appNotificationEnabled = initialState,
                eventSink = eventsRecorder
            ),
        )
        clickOn(R.string.screen_notification_settings_enable_notifications)
        eventsRecorder.assertList(
            listOf(
                NotificationSettingsEvents.RefreshSystemNotificationsEnabled,
                NotificationSettingsEvents.SetNotificationsEnabled(!initialState)
            )
        )
    }

    @Config(qualifiers = "h1024dp")
    @Test
    fun `clicking on disable notify me on at room emits the expected events`() {
        testAtRoomToggle(true)
    }

    @Config(qualifiers = "h1024dp")
    @Test
    fun `clicking on enable notify me on at room emits the expected events`() {
        testAtRoomToggle(false)
    }

    private fun testAtRoomToggle(initialState: Boolean) = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<NotificationSettingsEvents>()
        setNotificationSettingsView(
            state = aValidNotificationSettingsState(
                atRoomNotificationsEnabled = initialState,
                eventSink = eventsRecorder
            ),
        )
        clickOn(R.string.screen_notification_settings_room_mention_label)
        eventsRecorder.assertList(
            listOf(
                NotificationSettingsEvents.RefreshSystemNotificationsEnabled,
                NotificationSettingsEvents.SetAtRoomNotificationsEnabled(!initialState)
            )
        )
    }

    @Config(qualifiers = "h1024dp")
    @Test
    fun `clicking on disable notify me on invitation emits the expected events`() {
        testInvitationToggle(true)
    }

    @Config(qualifiers = "h1024dp")
    @Test
    fun `clicking on enable notify me on invitation emits the expected events`() {
        testInvitationToggle(false)
    }

    private fun testInvitationToggle(initialState: Boolean) = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<NotificationSettingsEvents>()
        setNotificationSettingsView(
            state = aValidNotificationSettingsState(
                inviteForMeNotificationsEnabled = initialState,
                eventSink = eventsRecorder
            ),
        )
        clickOn(R.string.screen_notification_settings_invite_for_me_label)
        eventsRecorder.assertList(
            listOf(
                NotificationSettingsEvents.RefreshSystemNotificationsEnabled,
                NotificationSettingsEvents.SetInviteForMeNotificationsEnabled(!initialState)
            )
        )
    }

    @Config(qualifiers = "h1024dp")
    @Test
    fun `with an error configuration, clicking on continue emits the expected events`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<NotificationSettingsEvents>()
        setNotificationSettingsView(
            state = aValidNotificationSettingsState(
                changeNotificationSettingAction = AsyncAction.Failure(AN_EXCEPTION),
                eventSink = eventsRecorder
            ),
        )
        clickOn(CommonStrings.action_ok)
        eventsRecorder.assertList(
            listOf(
                NotificationSettingsEvents.RefreshSystemNotificationsEnabled,
                NotificationSettingsEvents.ClearNotificationChangeError
            )
        )
    }

    @Config(qualifiers = "h1024dp")
    @Test
    fun `with invalid configuration, clicking on continue emits the expected events`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<NotificationSettingsEvents>()
        setNotificationSettingsView(
            state = aInvalidNotificationSettingsState(
                fixFailed = false,
                eventSink = eventsRecorder
            ),
        )
        clickOn(CommonStrings.action_continue)
        eventsRecorder.assertList(
            listOf(
                NotificationSettingsEvents.RefreshSystemNotificationsEnabled,
                NotificationSettingsEvents.FixConfigurationMismatch
            )
        )
    }

    @Config(qualifiers = "h1024dp")
    @Test
    fun `with invalid configuration and error, clicking on OK emits the expected events`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<NotificationSettingsEvents>()
        setNotificationSettingsView(
            state = aInvalidNotificationSettingsState(
                fixFailed = true,
                eventSink = eventsRecorder
            ),
        )
        clickOn(CommonStrings.action_ok)
        eventsRecorder.assertList(
            listOf(
                NotificationSettingsEvents.RefreshSystemNotificationsEnabled,
                NotificationSettingsEvents.ClearConfigurationMismatchError
            )
        )
    }

    @Config(qualifiers = "h1280dp")
    @Test
    fun `clicking on Push notification provider emits the expected event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<NotificationSettingsEvents>()
        setNotificationSettingsView(
            state = aValidNotificationSettingsState(
                eventSink = eventsRecorder
            ),
        )
        clickOn(R.string.screen_advanced_settings_push_provider_android)
        eventsRecorder.assertList(
            listOf(
                NotificationSettingsEvents.RefreshSystemNotificationsEnabled,
                NotificationSettingsEvents.ChangePushProvider,
            )
        )
    }

    @Test
    fun `clicking on a push provider emits the expected event`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<NotificationSettingsEvents>()
        setNotificationSettingsView(
            state = aValidNotificationSettingsState(
                eventSink = eventsRecorder,
                showChangePushProviderDialog = true,
                availablePushDistributors = listOf(aDistributor("P1"), aDistributor("P2"))
            ),
        )
        onNodeWithText("P2").performClick()
        eventsRecorder.assertList(
            listOf(
                NotificationSettingsEvents.RefreshSystemNotificationsEnabled,
                NotificationSettingsEvents.SetPushProvider(1),
            )
        )
    }

    @Config(qualifiers = "h1280dp")
    @Test
    fun `sounds preference category renders rows with current display names`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<NotificationSettingsEvents>()
        setNotificationSettingsView(
            state = aValidNotificationSettingsState(
                eventSink = eventsRecorder,
                messageSoundDisplayName = "Pixel notification",
                callRingtoneDisplayName = "Pixel ringtone",
            ),
        )
        onNodeWithText("Sound").assertIsDisplayed()
        onNodeWithText("Message sound").assertIsDisplayed()
        onNodeWithText("Call ringtone").assertIsDisplayed()
        onNodeWithText("Pixel notification").assertIsDisplayed()
        onNodeWithText("Pixel ringtone").assertIsDisplayed()
    }

    @Config(qualifiers = "h1280dp")
    @Test
    fun `clicking the message sound row opens the preset dialog`() = runAndroidComposeUiTest {
        val eventsRecorder = EventsRecorder<NotificationSettingsEvents>()
        setNotificationSettingsView(
            state = aValidNotificationSettingsState(eventSink = eventsRecorder),
        )
        // The click now opens the in-app preset dialog instead of launching the system picker
        // directly; the picker only fires from the dialog's "Choose another sound..." option
        // (covered by Presenter tests + NotificationSoundPickerTest).
        onNodeWithText("Message sound").performClick()
        eventsRecorder.assertList(
            listOf(
                NotificationSettingsEvents.RefreshSystemNotificationsEnabled,
                NotificationSettingsEvents.ShowMessageSoundDialog,
            )
        )
    }
}

private fun AndroidComposeUiTest<ComponentActivity>.setNotificationSettingsView(
    state: NotificationSettingsState,
    onOpenEditDefault: (isOneToOne: Boolean) -> Unit = EnsureNeverCalledWithParam(),
    onTroubleshootNotificationsClick: () -> Unit = EnsureNeverCalled(),
    onBackClick: () -> Unit = EnsureNeverCalled(),
) {
    setContent {
        NotificationSettingsView(
            state = state,
            onOpenEditDefault = onOpenEditDefault,
            onTroubleshootNotificationsClick = onTroubleshootNotificationsClick,
            onBackClick = onBackClick,
        )
    }
}
