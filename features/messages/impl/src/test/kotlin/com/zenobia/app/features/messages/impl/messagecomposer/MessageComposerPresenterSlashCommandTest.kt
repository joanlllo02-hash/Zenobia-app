/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.zenobia.app.features.messages.impl.messagecomposer

import android.net.Uri
import app.cash.turbine.ReceiveTurbine
import com.google.common.truth.Truth.assertThat
import com.zenobia.app.features.location.api.LocationService
import com.zenobia.app.features.location.test.FakeLocationService
import com.zenobia.app.features.messages.impl.FakeMessagesNavigator
import com.zenobia.app.features.messages.impl.MessagesNavigator
import com.zenobia.app.features.messages.impl.draft.ComposerDraftService
import com.zenobia.app.features.messages.impl.draft.FakeComposerDraftService
import com.zenobia.app.features.messages.impl.messagecomposer.suggestions.SuggestionsProcessor
import com.zenobia.app.features.messages.impl.timeline.TimelineController
import com.zenobia.app.features.messages.impl.utils.FakeMentionSpanFormatter
import com.zenobia.app.features.messages.impl.utils.FakeTextPillificationHelper
import com.zenobia.app.features.messages.impl.utils.TextPillificationHelper
import com.zenobia.app.libraries.designsystem.utils.snackbar.SnackbarDispatcher
import com.zenobia.app.libraries.matrix.api.core.ThreadId
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.api.permalink.PermalinkBuilder
import com.zenobia.app.libraries.matrix.api.permalink.PermalinkParser
import com.zenobia.app.libraries.matrix.api.room.JoinedRoom
import com.zenobia.app.libraries.matrix.api.timeline.Timeline
import com.zenobia.app.libraries.matrix.test.A_FAILURE_REASON
import com.zenobia.app.libraries.matrix.test.A_MESSAGE
import com.zenobia.app.libraries.matrix.test.A_USER_ID
import com.zenobia.app.libraries.matrix.test.permalink.FakePermalinkBuilder
import com.zenobia.app.libraries.matrix.test.permalink.FakePermalinkParser
import com.zenobia.app.libraries.matrix.test.room.FakeJoinedRoom
import com.zenobia.app.libraries.mediapickers.api.PickerProvider
import com.zenobia.app.libraries.mediapickers.test.FakePickerProvider
import com.zenobia.app.libraries.mediaupload.api.MediaOptimizationConfig
import com.zenobia.app.libraries.mediaupload.api.MediaPreProcessor
import com.zenobia.app.libraries.mediaupload.api.MediaSenderFactory
import com.zenobia.app.libraries.mediaupload.impl.DefaultMediaSender
import com.zenobia.app.libraries.mediaupload.test.FakeMediaOptimizationConfigProvider
import com.zenobia.app.libraries.mediaupload.test.FakeMediaPreProcessor
import com.zenobia.app.libraries.mediaviewer.test.FakeLocalMediaFactory
import com.zenobia.app.libraries.permissions.api.PermissionsPresenter
import com.zenobia.app.libraries.permissions.test.FakePermissionsPresenter
import com.zenobia.app.libraries.permissions.test.FakePermissionsPresenterFactory
import com.zenobia.app.libraries.preferences.api.store.SessionPreferencesStore
import com.zenobia.app.libraries.preferences.api.store.VideoCompressionPreset
import com.zenobia.app.libraries.preferences.test.InMemorySessionPreferencesStore
import com.zenobia.app.libraries.push.test.notifications.conversations.FakeNotificationConversationService
import com.zenobia.app.libraries.slashcommands.api.SlashCommand
import com.zenobia.app.libraries.slashcommands.api.SlashCommandService
import com.zenobia.app.libraries.slashcommands.test.FakeSlashCommandService
import com.zenobia.app.libraries.textcomposer.mentions.MentionSpanProvider
import com.zenobia.app.libraries.textcomposer.mentions.MentionSpanTheme
import com.zenobia.app.libraries.textcomposer.model.MessageComposerMode
import com.zenobia.app.services.analytics.test.FakeAnalyticsService
import com.zenobia.app.tests.testutils.WarmUpRule
import com.zenobia.app.tests.testutils.lambda.lambdaRecorder
import com.zenobia.app.tests.testutils.lambda.value
import com.zenobia.app.tests.testutils.test
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class MessageComposerPresenterSlashCommandTest {
    @get:Rule
    val warmUpRule = WarmUpRule()

    private val pickerProvider = FakePickerProvider().apply {
        givenResult(mockk()) // Uri is not available in JVM, so the only way to have a non-null Uri is using Mockk
    }
    private val mediaPreProcessor = FakeMediaPreProcessor()
    private val snackbarDispatcher = SnackbarDispatcher()
    private val mockMediaUrl: Uri = mockk("localMediaUri")
    private val localMediaFactory = FakeLocalMediaFactory(mockMediaUrl)
    private val analyticsService = FakeAnalyticsService()
    private val notificationConversationService = FakeNotificationConversationService()

    @Test
    fun `present - initial state`() = runTest {
        val presenter = createPresenter()
        presenter.test {
            val initialState = awaitFirstItem()
            assertThat(initialState.isFullScreen).isFalse()
            assertThat(initialState.textEditorState.messageHtml()).isEmpty()
            assertThat(initialState.mode).isEqualTo(MessageComposerMode.Normal)
            assertThat(initialState.showAttachmentSourcePicker).isFalse()
            assertThat(initialState.canShareLocation).isTrue()
        }
    }

    @Test
    fun `present - slash command error sets failure`() = runTest {
        val presenter = createPresenter(
            slashCommandService = FakeSlashCommandService(
                parseResult = { _, _, _ -> SlashCommand.ErrorUnknownSlashCommand(A_FAILURE_REASON) }
            )
        )
        presenter.test {
            val initialState = awaitFirstItem()
            initialState.textEditorState.setHtml(A_MESSAGE)
            initialState.eventSink(MessageComposerEvent.SendMessage)
            val errorState = awaitItem()
            assertThat(errorState.slashCommandAction.isFailure()).isTrue()
            assertThat(errorState.slashCommandAction.errorOrNull()?.message).isEqualTo(A_FAILURE_REASON)
            // Composer should not be reset when command is an error
            assertThat(errorState.textEditorState.messageHtml()).isEqualTo(A_MESSAGE)
            // Close the error
            errorState.eventSink(MessageComposerEvent.ClearSlashError)
            val finalState = awaitItem()
            assertThat(finalState.slashCommandAction.isUninitialized()).isTrue()
        }
    }

    @Test
    fun `present - slash command navigation ShowUser navigates to member and resets composer`() = runTest {
        val navigateToMember = lambdaRecorder<UserId, Unit> {}
        val navigator = FakeMessagesNavigator(navigateToMemberLambda = navigateToMember)
        val presenter = createPresenter(
            navigator = navigator,
            slashCommandService = FakeSlashCommandService(
                parseResult = { _, _, _ -> SlashCommand.ShowUser(A_USER_ID) }
            )
        )
        presenter.test {
            val initialState = awaitFirstItem()
            initialState.textEditorState.setHtml(A_MESSAGE)
            initialState.eventSink(MessageComposerEvent.SendMessage)
            advanceUntilIdle()
            // navigation should be invoked and composer reset
            navigateToMember.assertions().isCalledOnce().with(value(A_USER_ID))
            assertThat(initialState.textEditorState.messageHtml()).isEmpty()
        }
    }

    @Test
    fun `present - slash command navigation DevTools navigates to developer settings and resets composer`() = runTest {
        val navigateToDev = lambdaRecorder<Unit> { }
        val navigator = FakeMessagesNavigator(navigateToDeveloperSettingsLambda = navigateToDev)
        val presenter = createPresenter(
            navigator = navigator,
            slashCommandService = FakeSlashCommandService(
                parseResult = { _, _, _ -> SlashCommand.DevTools }
            )
        )
        presenter.test {
            val initialState = awaitFirstItem()
            initialState.textEditorState.setHtml(A_MESSAGE)
            initialState.eventSink(MessageComposerEvent.SendMessage)
            advanceUntilIdle()
            navigateToDev.assertions().isCalledOnce()
            assertThat(initialState.textEditorState.messageHtml()).isEmpty()
        }
    }

    @Test
    fun `present - slash command send message proceeds and resets composer`() = runTest {
        val presenter = createPresenter(
            slashCommandService = FakeSlashCommandService(
                parseResult = { _, _, _ -> SlashCommand.SendPlainText(A_MESSAGE) },
                proceedSendMessageResult = { _, _ -> Result.success(Unit) }
            )
        )
        presenter.test {
            val initialState = awaitFirstItem()
            initialState.textEditorState.setHtml(A_MESSAGE)
            initialState.eventSink(MessageComposerEvent.SendMessage)
            advanceUntilIdle()
            // Composer reset after successful slash send
            assertThat(initialState.textEditorState.messageHtml()).isEmpty()
            // Ensure no failure
            assertThat(initialState.slashCommandAction.isFailure()).isFalse()
        }
    }

    @Test
    fun `present - slash command send message failure sets failure state`() = runTest {
        val presenter = createPresenter(
            slashCommandService = FakeSlashCommandService(
                parseResult = { _, _, _ -> SlashCommand.SendPlainText("A_MESSAGE") },
                proceedSendMessageResult = { _, _ -> Result.failure(Exception(A_FAILURE_REASON)) }
            )
        )
        presenter.test {
            val initialState = awaitFirstItem()
            initialState.textEditorState.setHtml(A_MESSAGE)
            initialState.eventSink(MessageComposerEvent.SendMessage)
            val failureState = awaitItem()
            assertThat(failureState.slashCommandAction.isFailure()).isTrue()
            assertThat(failureState.slashCommandAction.errorOrNull()?.message).isEqualTo(A_FAILURE_REASON)
            // Clear the error
            failureState.eventSink(MessageComposerEvent.ClearSlashError)
            val finalState = awaitItem()
            assertThat(finalState.slashCommandAction.isUninitialized()).isTrue()
        }
    }

    @Test
    fun `present - slash command admin proceeds and resets state on success`() = runTest {
        val presenter = createPresenter(
            slashCommandService = FakeSlashCommandService(
                parseResult = { _, _, _ -> SlashCommand.BanUser(A_USER_ID, null) },
                proceedAdminResult = { _ -> Result.success(Unit) }
            )
        )
        presenter.test {
            val initialState = awaitFirstItem()
            initialState.textEditorState.setHtml(A_MESSAGE)
            initialState.eventSink(MessageComposerEvent.SendMessage)
            val loadingState = awaitItem()
            assertThat(loadingState.slashCommandAction.isLoading()).isTrue()
            val successState = awaitItem()
            // After success, state should be Uninitialized
            assertThat(successState.slashCommandAction.isUninitialized()).isTrue()
            assertThat(successState.textEditorState.messageHtml()).isEmpty()
        }
    }

    @Test
    fun `present - slash command admin proceeds and emit failure on error`() = runTest {
        val presenter = createPresenter(
            slashCommandService = FakeSlashCommandService(
                parseResult = { _, _, _ -> SlashCommand.BanUser(A_USER_ID, null) },
                proceedAdminResult = { _ -> Result.failure(Exception(A_FAILURE_REASON)) }
            )
        )
        presenter.test {
            val initialState = awaitFirstItem()
            initialState.textEditorState.setHtml(A_MESSAGE)
            initialState.eventSink(MessageComposerEvent.SendMessage)
            val loadingState = awaitItem()
            assertThat(loadingState.slashCommandAction.isLoading()).isTrue()
            val failureState = awaitItem()
            assertThat(failureState.slashCommandAction.isFailure()).isTrue()
            assertThat(failureState.slashCommandAction.errorOrNull()?.message).isEqualTo(A_FAILURE_REASON)
            // Clear error
            failureState.eventSink(MessageComposerEvent.ClearSlashError)
            val finalState = awaitItem()
            assertThat(finalState.slashCommandAction.isUninitialized()).isTrue()
        }
    }

    private fun TestScope.createPresenter(
        room: JoinedRoom = FakeJoinedRoom(
            typingNoticeResult = { Result.success(Unit) }
        ),
        timeline: Timeline = room.liveTimeline,
        navigator: MessagesNavigator = FakeMessagesNavigator(),
        pickerProvider: PickerProvider = this@MessageComposerPresenterSlashCommandTest.pickerProvider,
        locationService: LocationService = FakeLocationService(true),
        sessionPreferencesStore: SessionPreferencesStore = InMemorySessionPreferencesStore(),
        mediaPreProcessor: MediaPreProcessor = this@MessageComposerPresenterSlashCommandTest.mediaPreProcessor,
        snackbarDispatcher: SnackbarDispatcher = this@MessageComposerPresenterSlashCommandTest.snackbarDispatcher,
        permissionPresenter: PermissionsPresenter = FakePermissionsPresenter(),
        permalinkBuilder: PermalinkBuilder = FakePermalinkBuilder(),
        permalinkParser: PermalinkParser = FakePermalinkParser(),
        mentionSpanProvider: MentionSpanProvider = MentionSpanProvider(
            permalinkParser = permalinkParser,
            mentionSpanFormatter = FakeMentionSpanFormatter(),
            mentionSpanTheme = MentionSpanTheme(A_USER_ID)
        ),
        textPillificationHelper: TextPillificationHelper = FakeTextPillificationHelper(),
        isRichTextEditorEnabled: Boolean = true,
        draftService: ComposerDraftService = FakeComposerDraftService(),
        mediaOptimizationConfigProvider: FakeMediaOptimizationConfigProvider = FakeMediaOptimizationConfigProvider(),
        threadRoot: ThreadId? = null,
        slashCommandService: SlashCommandService = FakeSlashCommandService(),
    ) = MessageComposerPresenter(
        navigator = navigator,
        sessionCoroutineScope = this,
        threadRoot = threadRoot,
        room = room,
        mediaPickerProvider = pickerProvider,
        sessionPreferencesStore = sessionPreferencesStore,
        localMediaFactory = localMediaFactory,
        mediaSenderFactory = MediaSenderFactory { timelineMode ->
            DefaultMediaSender(
                preProcessor = mediaPreProcessor,
                room = room,
                timelineMode = timelineMode,
                mediaOptimizationConfigProvider = {
                    MediaOptimizationConfig(
                        compressImages = true,
                        videoCompressionPreset = VideoCompressionPreset.STANDARD
                    )
                }
            )
        },
        snackbarDispatcher = snackbarDispatcher,
        analyticsService = analyticsService,
        locationService = locationService,
        messageComposerContext = DefaultMessageComposerContext(),
        richTextEditorStateFactory = TestRichTextEditorStateFactory(),
        roomAliasSuggestionsDataSource = FakeRoomAliasSuggestionsDataSource(),
        permissionsPresenterFactory = FakePermissionsPresenterFactory(permissionPresenter),
        permalinkParser = permalinkParser,
        permalinkBuilder = permalinkBuilder,
        timelineController = TimelineController(room, timeline),
        draftService = draftService,
        mentionSpanProvider = mentionSpanProvider,
        pillificationHelper = textPillificationHelper,
        suggestionsProcessor = SuggestionsProcessor(slashCommandService = slashCommandService),
        mediaOptimizationConfigProvider = mediaOptimizationConfigProvider,
        notificationConversationService = notificationConversationService,
        slashCommandService = slashCommandService,
    ).apply {
        isTesting = true
        showTextFormatting = isRichTextEditorEnabled
    }

    private suspend fun <T> ReceiveTurbine<T>.awaitFirstItem(): T {
        skipItems(1)
        return awaitItem()
    }
}
