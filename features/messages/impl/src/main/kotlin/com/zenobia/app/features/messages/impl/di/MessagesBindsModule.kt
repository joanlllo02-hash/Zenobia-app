/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import com.zenobia.app.features.messages.impl.crypto.identity.IdentityChangeState
import com.zenobia.app.features.messages.impl.crypto.identity.IdentityChangeStatePresenter
import com.zenobia.app.features.messages.impl.crypto.sendfailure.resolve.ResolveVerifiedUserSendFailurePresenter
import com.zenobia.app.features.messages.impl.crypto.sendfailure.resolve.ResolveVerifiedUserSendFailureState
import com.zenobia.app.features.messages.impl.link.LinkPresenter
import com.zenobia.app.features.messages.impl.link.LinkState
import com.zenobia.app.features.messages.impl.pinned.banner.PinnedMessagesBannerPresenter
import com.zenobia.app.features.messages.impl.pinned.banner.PinnedMessagesBannerState
import com.zenobia.app.features.messages.impl.timeline.components.customreaction.CustomReactionPresenter
import com.zenobia.app.features.messages.impl.timeline.components.customreaction.CustomReactionState
import com.zenobia.app.features.messages.impl.timeline.components.reactionsummary.ReactionSummaryPresenter
import com.zenobia.app.features.messages.impl.timeline.components.reactionsummary.ReactionSummaryState
import com.zenobia.app.features.messages.impl.timeline.components.receipt.bottomsheet.ReadReceiptBottomSheetPresenter
import com.zenobia.app.features.messages.impl.timeline.components.receipt.bottomsheet.ReadReceiptBottomSheetState
import com.zenobia.app.features.messages.impl.timeline.protection.TimelineProtectionPresenter
import com.zenobia.app.features.messages.impl.timeline.protection.TimelineProtectionState
import com.zenobia.app.features.messages.impl.typing.TypingNotificationPresenter
import com.zenobia.app.features.messages.impl.typing.TypingNotificationState
import com.zenobia.app.libraries.architecture.Presenter
import com.zenobia.app.libraries.di.RoomScope

@ContributesTo(RoomScope::class)
@BindingContainer
interface MessagesBindsModule {
    @Binds
    fun bindPinnedMessagesBannerPresenter(presenter: PinnedMessagesBannerPresenter): Presenter<PinnedMessagesBannerState>

    @Binds
    fun bindResolveVerifiedUserSendFailurePresenter(presenter: ResolveVerifiedUserSendFailurePresenter): Presenter<ResolveVerifiedUserSendFailureState>

    @Binds
    fun bindTypingNotificationPresenter(presenter: TypingNotificationPresenter): Presenter<TypingNotificationState>

    @Binds
    fun bindTimelineProtectionPresenter(presenter: TimelineProtectionPresenter): Presenter<TimelineProtectionState>

    @Binds
    fun bindLinkPresenter(presenter: LinkPresenter): Presenter<LinkState>

    @Binds
    fun bindCustomReactionPresenter(presenter: CustomReactionPresenter): Presenter<CustomReactionState>

    @Binds
    fun bindReactionSummaryPresenter(presenter: ReactionSummaryPresenter): Presenter<ReactionSummaryState>

    @Binds
    fun bindReadReceiptBottomSheetPresenter(presenter: ReadReceiptBottomSheetPresenter): Presenter<ReadReceiptBottomSheetState>

    @Binds
    fun bindIdentityChangeStatePresenter(presenter: IdentityChangeStatePresenter): Presenter<IdentityChangeState>
}
