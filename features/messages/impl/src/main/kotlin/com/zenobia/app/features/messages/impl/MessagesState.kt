/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl

import com.zenobia.app.features.messages.api.timeline.voicemessages.composer.VoiceMessageComposerState
import com.zenobia.app.features.messages.impl.actionlist.ActionListState
import com.zenobia.app.features.messages.impl.crypto.identity.IdentityChangeState
import com.zenobia.app.features.messages.impl.link.LinkState
import com.zenobia.app.features.messages.impl.messagecomposer.MessageComposerState
import com.zenobia.app.features.messages.impl.pinned.banner.PinnedMessagesBannerState
import com.zenobia.app.features.messages.impl.timeline.TimelineState
import com.zenobia.app.features.messages.impl.timeline.components.customreaction.CustomReactionState
import com.zenobia.app.features.messages.impl.timeline.components.reactionsummary.ReactionSummaryState
import com.zenobia.app.features.messages.impl.timeline.components.receipt.bottomsheet.ReadReceiptBottomSheetState
import com.zenobia.app.features.messages.impl.timeline.protection.TimelineProtectionState
import com.zenobia.app.features.roomcall.api.RoomCallState
import com.zenobia.app.features.roommembermoderation.api.RoomMemberModerationState
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarData
import com.zenobia.app.libraries.designsystem.utils.snackbar.SnackbarMessage
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.libraries.matrix.api.encryption.identity.IdentityState
import com.zenobia.app.libraries.matrix.api.room.tombstone.SuccessorRoom
import kotlinx.collections.immutable.ImmutableList

data class MessagesState(
    val roomId: RoomId,
    val roomName: String?,
    val roomAvatar: AvatarData,
    val heroes: ImmutableList<AvatarData>,
    val userEventPermissions: UserEventPermissions,
    val composerState: MessageComposerState,
    val voiceMessageComposerState: VoiceMessageComposerState,
    val timelineState: TimelineState,
    val timelineProtectionState: TimelineProtectionState,
    val identityChangeState: IdentityChangeState,
    val linkState: LinkState,
    val actionListState: ActionListState,
    val customReactionState: CustomReactionState,
    val reactionSummaryState: ReactionSummaryState,
    val readReceiptBottomSheetState: ReadReceiptBottomSheetState,
    val snackbarMessage: SnackbarMessage?,
    val inviteProgress: AsyncData<Unit>,
    val showReinvitePrompt: Boolean,
    val enableTextFormatting: Boolean,
    val roomCallState: RoomCallState,
    val appName: String,
    val pinnedMessagesBannerState: PinnedMessagesBannerState,
    val dmUserVerificationState: IdentityState?,
    val roomMemberModerationState: RoomMemberModerationState,
    /** Type of "shared history" icon to show in the top bar. */
    val topBarSharedHistoryIcon: SharedHistoryIcon,
    val successorRoom: SuccessorRoom?,
    val threads: Threads,
    val showLiveLocationShareBanner: Boolean,
    val eventSink: (MessagesEvent) -> Unit
) {
    val isTombstoned = successorRoom != null

    data class Threads(
        val hasThreads: Boolean,
        val hasUnreadThreads: Boolean,
    )
}

/** Type of "shared history" icon to show in the top bar. */
enum class SharedHistoryIcon {
    /** Show no icon at all. */
    NONE,

    /** history_visibility: shared. */
    SHARED,

    /** history_visibility: world_readable. */
    WORLD_READABLE
}
