/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.push.impl.notifications.factories

import android.app.Notification
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.common.truth.Truth.assertThat
import com.zenobia.app.appconfig.NotificationConfig
import com.zenobia.app.features.enterprise.api.EnterpriseService
import com.zenobia.app.features.enterprise.test.FakeEnterpriseService
import com.zenobia.app.libraries.core.meta.BuildMeta
import com.zenobia.app.libraries.matrix.test.AN_EVENT_ID
import com.zenobia.app.libraries.matrix.test.A_COLOR_INT
import com.zenobia.app.libraries.matrix.test.A_ROOM_ID
import com.zenobia.app.libraries.matrix.test.A_SESSION_ID
import com.zenobia.app.libraries.matrix.test.A_THREAD_ID
import com.zenobia.app.libraries.matrix.test.core.aBuildMeta
import com.zenobia.app.libraries.matrix.ui.components.aMatrixUser
import com.zenobia.app.libraries.matrix.ui.media.test.FakeImageLoader
import com.zenobia.app.libraries.matrix.ui.media.test.FakeInitialsAvatarBitmapGenerator
import com.zenobia.app.libraries.push.api.notifications.NotificationBitmapLoader
import com.zenobia.app.libraries.push.impl.notifications.DefaultNotificationBitmapLoader
import com.zenobia.app.libraries.push.impl.notifications.NotificationActionIds
import com.zenobia.app.libraries.push.impl.notifications.RoomEventGroupInfo
import com.zenobia.app.libraries.push.impl.notifications.channels.DefaultNotificationChannels
import com.zenobia.app.libraries.push.impl.notifications.channels.NotificationChannels
import com.zenobia.app.libraries.push.impl.notifications.factories.action.AcceptInvitationActionFactory
import com.zenobia.app.libraries.push.impl.notifications.factories.action.MarkAsReadActionFactory
import com.zenobia.app.libraries.push.impl.notifications.factories.action.QuickReplyActionFactory
import com.zenobia.app.libraries.push.impl.notifications.factories.action.RejectInvitationActionFactory
import com.zenobia.app.libraries.push.impl.notifications.fixtures.aFallbackNotifiableEvent
import com.zenobia.app.libraries.push.impl.notifications.fixtures.aNotifiableMessageEvent
import com.zenobia.app.libraries.push.impl.notifications.model.InviteNotifiableEvent
import com.zenobia.app.libraries.push.impl.notifications.model.SimpleNotifiableEvent
import com.zenobia.app.services.toolbox.test.sdk.FakeBuildVersionSdkIntProvider
import com.zenobia.app.services.toolbox.test.strings.FakeStringProvider
import com.zenobia.app.services.toolbox.test.systemclock.A_FAKE_TIMESTAMP
import com.zenobia.app.services.toolbox.test.systemclock.FakeSystemClock
import com.zenobia.app.tests.testutils.robolectric.RobolectricTest
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.robolectric.RuntimeEnvironment

class DefaultNotificationCreatorTest : RobolectricTest() {
    @Test
    fun `test createDiagnosticNotification`() {
        val sut = createNotificationCreator()
        val result = sut.createDiagnosticNotification(
            color = A_COLOR_INT,
        )
        result.commonAssertions(
            expectedGroup = null,
            expectedCategory = NotificationCompat.CATEGORY_STATUS,
        )
    }

    @Test
    fun `test createUnregistrationNotification`() {
        val sut = createNotificationCreator()
        val matrixUser = aMatrixUser()
        val result = sut.createUnregistrationNotification(
            notificationAccountParams = aNotificationAccountParams(
                user = matrixUser,
            ),
        )
        result.commonAssertions(
            expectedGroup = matrixUser.userId.value,
            expectedCategory = NotificationCompat.CATEGORY_ERROR,
        )
    }

    @Test
    fun `test createFallbackNotification`() {
        val sut = createNotificationCreator()
        val result = sut.createFallbackNotification(
            existingNotification = null,
            notificationAccountParams = aNotificationAccountParams(),
            fallbackNotifiableEvents = listOf(
                aFallbackNotifiableEvent(),
            )
        )
        result.commonAssertions(
            expectedCategory = null,
        )
    }

    @Test
    fun `test createSimpleEventNotification`() {
        val sut = createNotificationCreator()
        val result = sut.createSimpleEventNotification(
            notificationAccountParams = aNotificationAccountParams(),
            SimpleNotifiableEvent(
                sessionId = A_SESSION_ID,
                roomId = A_ROOM_ID,
                eventId = AN_EVENT_ID,
                editedEventId = null,
                noisy = false,
                title = "title",
                description = "description",
                type = null,
                timestamp = A_FAKE_TIMESTAMP,
                soundName = null,
                canBeReplaced = false,
                isRedacted = false,
                isUpdated = false,
            ),
        )
        result.commonAssertions(
            expectedCategory = null,
        )
    }

    @Test
    fun `test createSimpleEventNotification noisy`() {
        val sut = createNotificationCreator(
            enterpriseService = FakeEnterpriseService(
                getNoisyNotificationChannelIdResult = { null },
            ),
        )
        val result = sut.createSimpleEventNotification(
            notificationAccountParams = aNotificationAccountParams(),
            SimpleNotifiableEvent(
                sessionId = A_SESSION_ID,
                roomId = A_ROOM_ID,
                eventId = AN_EVENT_ID,
                editedEventId = null,
                noisy = true,
                title = "title",
                description = "description",
                type = null,
                timestamp = A_FAKE_TIMESTAMP,
                soundName = null,
                canBeReplaced = false,
                isRedacted = false,
                isUpdated = false,
            ),
        )
        result.commonAssertions(
            expectedCategory = null,
        )
    }

    @Test
    fun `test createRoomInvitationNotification`() {
        val sut = createNotificationCreator()
        val result = sut.createRoomInvitationNotification(
            notificationAccountParams = aNotificationAccountParams(),
            InviteNotifiableEvent(
                sessionId = A_SESSION_ID,
                roomId = A_ROOM_ID,
                eventId = AN_EVENT_ID,
                editedEventId = null,
                noisy = false,
                title = "title",
                description = "description",
                type = null,
                timestamp = A_FAKE_TIMESTAMP,
                soundName = null,
                canBeReplaced = false,
                isRedacted = false,
                isUpdated = false,
                roomName = "roomName",
            ),
        )
        result.commonAssertions(
            expectedCategory = null,
        )
        val actionTitles = result.actions?.map { it.title }
        assertThat(actionTitles).isEqualTo(
            listOfNotNull(
                REJECT_INVITATION_ACTION_TITLE.takeIf { NotificationConfig.SHOW_ACCEPT_AND_DECLINE_INVITE_ACTIONS },
                ACCEPT_INVITATION_ACTION_TITLE.takeIf { NotificationConfig.SHOW_ACCEPT_AND_DECLINE_INVITE_ACTIONS },
            )
        )
    }

    @Test
    fun `test createRoomInvitationNotification noisy`() {
        val sut = createNotificationCreator(
            enterpriseService = FakeEnterpriseService(
                getNoisyNotificationChannelIdResult = { null },
            ),
        )
        val result = sut.createRoomInvitationNotification(
            notificationAccountParams = aNotificationAccountParams(),
            InviteNotifiableEvent(
                sessionId = A_SESSION_ID,
                roomId = A_ROOM_ID,
                eventId = AN_EVENT_ID,
                editedEventId = null,
                noisy = true,
                title = "title",
                description = "description",
                type = null,
                timestamp = A_FAKE_TIMESTAMP,
                soundName = null,
                canBeReplaced = false,
                isRedacted = false,
                isUpdated = false,
                roomName = "roomName",
            ),
        )
        result.commonAssertions(
            expectedCategory = null,
        )
    }

    @Test
    fun `test createSummaryListNotification`() {
        val sut = createNotificationCreator()
        val matrixUser = aMatrixUser()
        val result = sut.createSummaryListNotification(
            notificationAccountParams = aNotificationAccountParams(user = matrixUser),
            compatSummary = "compatSummary",
            noisy = false,
            lastMessageTimestamp = 123_456L,
        )
        result.commonAssertions(
            expectedGroup = matrixUser.userId.value,
        )
    }

    @Test
    fun `test createSummaryListNotification noisy`() {
        val sut = createNotificationCreator(
            enterpriseService = FakeEnterpriseService(
                getNoisyNotificationChannelIdResult = { null },
            ),
        )
        val matrixUser = aMatrixUser()
        val result = sut.createSummaryListNotification(
            notificationAccountParams = aNotificationAccountParams(user = matrixUser),
            compatSummary = "compatSummary",
            noisy = true,
            lastMessageTimestamp = 123_456L,
        )
        result.commonAssertions(
            expectedGroup = matrixUser.userId.value,
        )
    }

    @Test
    fun `test createMessagesListNotification`() = runTest {
        val sut = createNotificationCreator()
        val result = sut.createMessagesListNotification(
            notificationAccountParams = aNotificationAccountParams(),
            roomInfo = RoomEventGroupInfo(
                sessionId = A_SESSION_ID,
                roomId = A_ROOM_ID,
                roomDisplayName = "roomDisplayName",
                hasSmartReplyError = false,
                shouldBing = false,
                customSound = null,
                isUpdated = false,
            ),
            threadId = null,
            largeIcon = null,
            lastMessageTimestamp = 123_456L,
            tickerText = "tickerText",
            existingNotification = null,
            imageLoader = FakeImageLoader(),
            events = listOf(aNotifiableMessageEvent()),
        )
        result.commonAssertions()
    }

    @Test
    fun `test createMessagesListNotification should bing and thread`() = runTest {
        val sut = createNotificationCreator(
            enterpriseService = FakeEnterpriseService(
                getNoisyNotificationChannelIdResult = { null },
            ),
        )
        val result = sut.createMessagesListNotification(
            notificationAccountParams = aNotificationAccountParams(),
            roomInfo = RoomEventGroupInfo(
                sessionId = A_SESSION_ID,
                roomId = A_ROOM_ID,
                roomDisplayName = "roomDisplayName",
                hasSmartReplyError = false,
                shouldBing = true,
                customSound = null,
                isUpdated = false,
            ),
            threadId = A_THREAD_ID,
            largeIcon = null,
            lastMessageTimestamp = 123_456L,
            tickerText = "tickerText",
            existingNotification = null,
            imageLoader = FakeImageLoader(),
            events = listOf(aNotifiableMessageEvent()),
        )
        result.commonAssertions()
    }

    private fun Notification.commonAssertions(
        expectedGroup: String? = aMatrixUser().userId.value,
        expectedCategory: String? = NotificationCompat.CATEGORY_MESSAGE,
    ) {
        assertThat(contentIntent).isNotNull()
        assertThat(group).isEqualTo(expectedGroup)
        assertThat(category).isEqualTo(expectedCategory)
    }
}

const val MARK_AS_READ_ACTION_TITLE = "MarkAsReadAction"
const val QUICK_REPLY_ACTION_TITLE = "QuickReplyAction"
const val ACCEPT_INVITATION_ACTION_TITLE = "AcceptInvitationAction"
const val REJECT_INVITATION_ACTION_TITLE = "RejectInvitationAction"

fun createNotificationCreator(
    context: Context = RuntimeEnvironment.getApplication(),
    buildMeta: BuildMeta = aBuildMeta(),
    enterpriseService: EnterpriseService = FakeEnterpriseService(),
    notificationChannels: NotificationChannels = createNotificationChannels(enterpriseService),
    bitmapLoader: NotificationBitmapLoader = DefaultNotificationBitmapLoader(
        context = context,
        sdkIntProvider = FakeBuildVersionSdkIntProvider(Build.VERSION_CODES.R),
        initialsAvatarBitmapGenerator = FakeInitialsAvatarBitmapGenerator(),
    ),
): NotificationCreator {
    return DefaultNotificationCreator(
        context = context,
        notificationChannels = notificationChannels,
        stringProvider = FakeStringProvider("test"),
        buildMeta = buildMeta,
        pendingIntentFactory = PendingIntentFactory(
            context,
            FakeIntentProvider(),
            FakeSystemClock(),
            NotificationActionIds(buildMeta),
        ),
        markAsReadActionFactory = MarkAsReadActionFactory(
            context = context,
            actionIds = NotificationActionIds(buildMeta),
            stringProvider = FakeStringProvider(MARK_AS_READ_ACTION_TITLE),
            clock = FakeSystemClock(),
        ),
        quickReplyActionFactory = QuickReplyActionFactory(
            context = context,
            actionIds = NotificationActionIds(buildMeta),
            stringProvider = FakeStringProvider(QUICK_REPLY_ACTION_TITLE),
            clock = FakeSystemClock(),
        ),
        bitmapLoader = bitmapLoader,
        acceptInvitationActionFactory = AcceptInvitationActionFactory(
            context = context,
            actionIds = NotificationActionIds(buildMeta),
            stringProvider = FakeStringProvider(ACCEPT_INVITATION_ACTION_TITLE),
            clock = FakeSystemClock(),
        ),
        rejectInvitationActionFactory = RejectInvitationActionFactory(
            context = context,
            actionIds = NotificationActionIds(buildMeta),
            stringProvider = FakeStringProvider(REJECT_INVITATION_ACTION_TITLE),
            clock = FakeSystemClock(),
        ),
    )
}

fun createNotificationChannels(
    enterpriseService: EnterpriseService = FakeEnterpriseService(),
): NotificationChannels {
    val context = RuntimeEnvironment.getApplication()
    val notificationManagerCompat = NotificationManagerCompat.from(context)
    return DefaultNotificationChannels(
        notificationManager = notificationManagerCompat,
        stringProvider = FakeStringProvider(""),
        context = context,
        enterpriseService = enterpriseService,
        appPreferencesStore = com.zenobia.app.libraries.preferences.test.InMemoryAppPreferencesStore(),
    )
}
