/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.eventformatter.impl

import androidx.annotation.StringRes
import androidx.compose.ui.text.AnnotatedString
import dev.zacsweers.metro.ContributesBinding
import com.zenobia.app.libraries.di.SessionScope
import com.zenobia.app.libraries.eventformatter.api.PinnedMessagesBannerFormatter
import com.zenobia.app.libraries.matrix.api.permalink.PermalinkParser
import com.zenobia.app.libraries.matrix.api.timeline.item.event.AudioMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.EmoteMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.EventTimelineItem
import com.zenobia.app.libraries.matrix.api.timeline.item.event.FileMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.ImageMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.LocationMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.MessageContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.MessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.NoticeMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.OtherMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.PollContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.RedactedContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.StickerContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.StickerMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.TextMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.UnableToDecryptContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.VideoMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.VoiceMessageType
import com.zenobia.app.libraries.matrix.api.timeline.item.event.getDisambiguatedDisplayName
import com.zenobia.app.libraries.matrix.ui.messages.toPlainText
import com.zenobia.app.libraries.ui.strings.CommonStrings
import com.zenobia.app.services.toolbox.api.strings.StringProvider

@ContributesBinding(SessionScope::class)
class DefaultPinnedMessagesBannerFormatter(
    private val sp: StringProvider,
    private val permalinkParser: PermalinkParser,
) : PinnedMessagesBannerFormatter {
    override fun format(event: EventTimelineItem): CharSequence {
        return when (val content = event.content) {
            is MessageContent -> processMessageContents(event, content)
            is StickerContent -> {
                val text = content.body ?: content.filename
                text.prefixWith(CommonStrings.common_sticker)
            }
            is UnableToDecryptContent -> {
                sp.getString(CommonStrings.common_waiting_for_decryption_key)
            }
            is PollContent -> {
                content.question.prefixWith(CommonStrings.a11y_poll)
            }
            RedactedContent -> {
                sp.getString(CommonStrings.common_message_removed)
            }
            else -> {
                sp.getString(CommonStrings.common_unsupported_event)
            }
        }
    }

    private fun processMessageContents(
        event: EventTimelineItem,
        messageContent: MessageContent,
    ): CharSequence {
        return when (val messageType: MessageType = messageContent.type) {
            is EmoteMessageType -> {
                val senderDisambiguatedDisplayName = event.senderProfile.getDisambiguatedDisplayName(event.sender)
                "* $senderDisambiguatedDisplayName ${messageType.body}"
            }
            is TextMessageType -> {
                messageType.toPlainText(permalinkParser)
            }
            is VideoMessageType -> {
                messageType.toPlainText(permalinkParser).prefixWith(CommonStrings.common_video)
            }
            is ImageMessageType -> {
                messageType.toPlainText(permalinkParser).prefixWith(CommonStrings.common_image)
            }
            is StickerMessageType -> {
                messageType.toPlainText(permalinkParser).prefixWith(CommonStrings.common_sticker)
            }
            is LocationMessageType -> {
                messageType.body.prefixWith(CommonStrings.common_shared_location)
            }
            is FileMessageType -> {
                messageType.toPlainText(permalinkParser).prefixWith(CommonStrings.common_file)
            }
            is AudioMessageType -> {
                messageType.toPlainText(permalinkParser).prefixWith(CommonStrings.common_audio)
            }
            is VoiceMessageType -> {
                messageType
                    .toPlainText(permalinkParser, "")
                    .takeIf { it.isNotEmpty() }
                    ?.prefixWith(sp.getString(CommonStrings.common_voice_message))
                    ?: sp.getString(CommonStrings.common_voice_message)
            }
            is OtherMessageType -> {
                messageType.body
            }
            is NoticeMessageType -> {
                messageType.body
            }
        }
    }

    private fun CharSequence.prefixWith(@StringRes res: Int): AnnotatedString {
        val prefix = sp.getString(res)
        return prefixWith(prefix)
    }
}
