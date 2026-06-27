/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.zenobia.app.features.messages.api.timeline.voicemessages.composer.VoiceMessageComposerState
import com.zenobia.app.features.messages.api.timeline.voicemessages.composer.aVoiceMessageComposerState
import com.zenobia.app.features.messages.api.timeline.voicemessages.composer.aVoiceMessagePreviewState
import com.zenobia.app.features.messages.impl.actionlist.ActionListState
import com.zenobia.app.features.messages.impl.actionlist.anActionListState
import com.zenobia.app.features.messages.impl.crypto.identity.IdentityChangeState
import com.zenobia.app.features.messages.impl.crypto.identity.aRoomMemberIdentityStateChange
import com.zenobia.app.features.messages.impl.crypto.identity.anIdentityChangeState
import com.zenobia.app.features.messages.impl.link.LinkState
import com.zenobia.app.features.messages.impl.link.aLinkState
import com.zenobia.app.features.messages.impl.messagecomposer.MessageComposerState
import com.zenobia.app.features.messages.impl.messagecomposer.aMessageComposerState
import com.zenobia.app.features.messages.impl.pinned.banner.PinnedMessagesBannerState
import com.zenobia.app.features.messages.impl.pinned.banner.aLoadedPinnedMessagesBannerState
import com.zenobia.app.features.messages.impl.timeline.TimelineState
import com.zenobia.app.features.messages.impl.timeline.aTimelineItemList
import com.zenobia.app.features.messages.impl.timeline.aTimelineState
import com.zenobia.app.features.messages.impl.timeline.components.customreaction.CustomReactionEvent
import com.zenobia.app.features.messages.impl.timeline.components.customreaction.CustomReactionState
import com.zenobia.app.features.messages.impl.timeline.components.reactionsummary.ReactionSummaryEvent
import com.zenobia.app.features.messages.impl.timeline.components.reactionsummary.ReactionSummaryState
import com.zenobia.app.features.messages.impl.timeline.components.receipt.bottomsheet.ReadReceiptBottomSheetEvent
import com.zenobia.app.features.messages.impl.timeline.components.receipt.bottomsheet.ReadReceiptBottomSheetState
import com.zenobia.app.features.messages.impl.timeline.model.TimelineItem
import com.zenobia.app.features.messages.impl.timeline.model.event.aTimelineItemTextContent
import com.zenobia.app.features.messages.impl.timeline.protection.TimelineProtectionState
import com.zenobia.app.features.messages.impl.timeline.protection.aTimelineProtectionState
import com.zenobia.app.features.roomcall.api.RoomCallState
import com.zenobia.app.features.roomcall.api.aStandByCallState
import com.zenobia.app.features.roommembermoderation.api.RoomMemberModerationEvents
import com.zenobia.app.features.roommembermoderation.api.RoomMemberModerationPermissions
import com.zenobia.app.features.roommembermoderation.api.RoomMemberModerationState
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarData
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarSize
import com.zenobia.app.libraries.designsystem.preview.ROOM_NAME
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.core.ThreadId
import com.zenobia.app.libraries.matrix.api.encryption.identity.IdentityState
import com.zenobia.app.libraries.matrix.api.room.tombstone.SuccessorRoom
import com.zenobia.app.libraries.matrix.api.timeline.Timeline
import com.zenobia.app.libraries.textcomposer.model.MessageComposerMode
import com.zenobia.app.libraries.textcomposer.model.aTextEditorStateMarkdown
import com.zenobia.app.libraries.textcomposer.model.aTextEditorStateRich
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

open class MessagesStateProvider : PreviewParameterProvider<MessagesState> {
    override val values: Sequence<MessagesState>
        get() = sequenceOf(
            aMessagesState(),
            aMessagesState(composerState = aMessageComposerState(showAttachmentSourcePicker = true)),
            aMessagesState(userEventPermissions = aUserEventPermissions(canSendMessage = false)),
            aMessagesState(showReinvitePrompt = true),
            aMessagesState(composerState = aMessageComposerState(showTextFormatting = true)),
            aMessagesState(
                voiceMessageComposerState = aVoiceMessageComposerState(showPermissionRationaleDialog = true),
            ),
            aMessagesState(
                voiceMessageComposerState = aVoiceMessageComposerState(
                    voiceMessageState = aVoiceMessagePreviewState(),
                    showSendFailureDialog = true
                ),
            ),
            aMessagesState(
                pinnedMessagesBannerState = aLoadedPinnedMessagesBannerState(
                    knownPinnedMessagesCount = 4,
                    currentPinnedMessageIndex = 0,
                ),
            ),
            aMessagesState(isCurrentlySharingLiveLocationInRoom = true),
            aMessagesState(successorRoom = SuccessorRoom(RoomId("!id:domain"), null)),
            aMessagesState(
                timelineState = aTimelineState(
                    timelineMode = Timeline.Mode.Thread(threadRootId = ThreadId("\$a-thread-id")),
                    timelineItems = aTimelineItemList(aTimelineItemTextContent()),
                )
            ),
            aMessagesState(
                composerState = aMessageComposerState(textEditorState = aTextEditorStateMarkdown()),
                identityChangeState = anIdentityChangeState(listOf(aRoomMemberIdentityStateChange()))
            ),
        )
}

fun aMessagesState(
    roomName: String? = ROOM_NAME,
    roomAvatar: AvatarData = AvatarData("!id:domain", ROOM_NAME, size = AvatarSize.TimelineRoom),
    userEventPermissions: UserEventPermissions = aUserEventPermissions(),
    composerState: MessageComposerState = aMessageComposerState(
        textEditorState = aTextEditorStateRich(initialText = "Hello", initialFocus = true),
        isFullScreen = false,
        mode = MessageComposerMode.Normal,
    ),
    voiceMessageComposerState: VoiceMessageComposerState = aVoiceMessageComposerState(),
    timelineState: TimelineState = aTimelineState(
        timelineItems = aTimelineItemList(aTimelineItemTextContent()),
        // Render a focused event for an event with sender information displayed
        focusedEventIndex = 2,
    ),
    timelineProtectionState: TimelineProtectionState = aTimelineProtectionState(),
    identityChangeState: IdentityChangeState = anIdentityChangeState(),
    linkState: LinkState = aLinkState(),
    readReceiptBottomSheetState: ReadReceiptBottomSheetState = aReadReceiptBottomSheetState(),
    actionListState: ActionListState = anActionListState(),
    customReactionState: CustomReactionState = aCustomReactionState(),
    reactionSummaryState: ReactionSummaryState = aReactionSummaryState(),
    showReinvitePrompt: Boolean = false,
    roomCallState: RoomCallState = aStandByCallState(),
    pinnedMessagesBannerState: PinnedMessagesBannerState = aLoadedPinnedMessagesBannerState(),
    dmUserVerificationState: IdentityState? = null,
    roomMemberModerationState: RoomMemberModerationState = aRoomMemberModerationState(),
    topBarSharedHistoryIcon: SharedHistoryIcon = SharedHistoryIcon.NONE,
    successorRoom: SuccessorRoom? = null,
    threads: MessagesState.Threads = MessagesState.Threads(
        hasThreads = false,
        hasUnreadThreads = false,
    ),
    isCurrentlySharingLiveLocationInRoom: Boolean = false,
    eventSink: (MessagesEvent) -> Unit = {},
) = MessagesState(
    roomId = RoomId("!id:domain"),
    roomName = roomName,
    roomAvatar = roomAvatar,
    heroes = persistentListOf(),
    userEventPermissions = userEventPermissions,
    composerState = composerState,
    voiceMessageComposerState = voiceMessageComposerState,
    timelineProtectionState = timelineProtectionState,
    identityChangeState = identityChangeState,
    linkState = linkState,
    timelineState = timelineState,
    readReceiptBottomSheetState = readReceiptBottomSheetState,
    actionListState = actionListState,
    customReactionState = customReactionState,
    reactionSummaryState = reactionSummaryState,
    snackbarMessage = null,
    inviteProgress = AsyncData.Uninitialized,
    showReinvitePrompt = showReinvitePrompt,
    enableTextFormatting = true,
    roomCallState = roomCallState,
    appName = "Zenobia",
    pinnedMessagesBannerState = pinnedMessagesBannerState,
    dmUserVerificationState = dmUserVerificationState,
    roomMemberModerationState = roomMemberModerationState,
    topBarSharedHistoryIcon = topBarSharedHistoryIcon,
    successorRoom = successorRoom,
    threads = threads,
    showLiveLocationShareBanner = isCurrentlySharingLiveLocationInRoom,
    eventSink = eventSink,
)

fun aRoomMemberModerationState(
    permissions: RoomMemberModerationPermissions = RoomMemberModerationPermissions.DEFAULT,
) = object : RoomMemberModerationState {
    override val permissions: RoomMemberModerationPermissions = permissions
    override val eventSink: (RoomMemberModerationEvents) -> Unit = {}
}

fun aUserEventPermissions(
    canRedactOwn: Boolean = false,
    canRedactOther: Boolean = false,
    canSendMessage: Boolean = true,
    canSendReaction: Boolean = true,
    canPinUnpin: Boolean = false,
) = UserEventPermissions(
    canRedactOwn = canRedactOwn,
    canRedactOther = canRedactOther,
    canSendMessage = canSendMessage,
    canSendReaction = canSendReaction,
    canPinUnpin = canPinUnpin,
)

fun aReactionSummaryState(
    target: ReactionSummaryState.Summary? = null,
    eventSink: (ReactionSummaryEvent) -> Unit = {}
) = ReactionSummaryState(
    target = target,
    eventSink = eventSink,
)

fun aCustomReactionState(
    target: CustomReactionState.Target = CustomReactionState.Target.None,
    recentEmojis: ImmutableList<String> = persistentListOf(),
    eventSink: (CustomReactionEvent) -> Unit = {},
) = CustomReactionState(
    target = target,
    recentEmojis = recentEmojis,
    selectedEmoji = persistentSetOf(),
    eventSink = eventSink,
)

fun aReadReceiptBottomSheetState(
    selectedEvent: TimelineItem.Event? = null,
    eventSink: (ReadReceiptBottomSheetEvent) -> Unit = {},
) = ReadReceiptBottomSheetState(
    selectedEvent = selectedEvent,
    eventSink = eventSink,
)
