/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.timeline.components.event

import android.text.SpannedString
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.features.messages.impl.timeline.components.layout.ContentAvoidingLayout
import com.zenobia.app.features.messages.impl.timeline.components.layout.ContentAvoidingLayoutData
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemTextBasedContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemTextBasedContentProvider
import com.zenobia.app.features.messages.impl.timeline.model.event.aTimelineItemTextContent
import com.zenobia.app.features.messages.impl.utils.containsOnlyEmojis
import com.zenobia.app.libraries.androidutils.text.LinkifyHelper
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.textcomposer.ElementRichTextEditorStyle
import com.zenobia.app.libraries.textcomposer.mentions.LocalMentionSpanUpdater
import com.zenobia.app.wysiwyg.compose.EditorStyledText
import com.zenobia.app.wysiwyg.link.Link

@Composable
fun TimelineItemTextView(
    content: TimelineItemTextBasedContent,
    onLinkClick: (Link) -> Unit,
    onLinkLongClick: (Link) -> Unit,
    modifier: Modifier = Modifier,
    onContentLayoutChange: (ContentAvoidingLayoutData) -> Unit = {},
) {
    val emojiOnly = content.formattedBody.toString() == content.body &&
        content.body.replace(" ", "").containsOnlyEmojis()
    val textStyle = when {
        emojiOnly -> ZenobiaTheme.typography.fontHeadingXlRegular
        else -> ZenobiaTheme.typography.fontBodyLgRegular
    }
    CompositionLocalProvider(
        LocalContentColor provides ZenobiaTheme.colors.textPrimary,
        LocalTextStyle provides textStyle
    ) {
        val text = getTextWithResolvedMentions(content)
        Box(modifier.semantics { contentDescription = content.plainText }) {
            EditorStyledText(
                text = text,
                onLinkClickedListener = onLinkClick,
                onLinkLongClickedListener = onLinkLongClick,
                style = ElementRichTextEditorStyle.textStyle(),
                onTextLayout = ContentAvoidingLayout.measureLegacyLastTextLine(onContentLayoutChange = onContentLayoutChange),
                releaseOnDetach = false,
            )
        }
    }
}

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
@Composable
internal fun getTextWithResolvedMentions(content: TimelineItemTextBasedContent): CharSequence {
    val mentionSpanUpdater = LocalMentionSpanUpdater.current
    val bodyWithResolvedMentions = mentionSpanUpdater.rememberMentionSpans(content.formattedBody)
    return SpannedString.valueOf(bodyWithResolvedMentions)
}

@PreviewsDayNight
@Composable
internal fun TimelineItemTextViewPreview(
    @PreviewParameter(TimelineItemTextBasedContentProvider::class) content: TimelineItemTextBasedContent
) = ZenobiaPreview {
    TimelineItemTextView(
        content = content,
        onLinkClick = {},
        onLinkLongClick = {},
    )
}

@Preview
@Composable
internal fun TimelineItemTextViewWithLinkifiedUrlPreview() = ZenobiaPreview {
    val content = aTimelineItemTextContent(
        formattedBody = LinkifyHelper.linkify("The link should end after the first '?' (url: github.com/element-hq/element-x-android/README?)?.")
    )
    TimelineItemTextView(
        content = content,
        onLinkClick = {},
        onLinkLongClick = {},
    )
}

@Preview
@Composable
internal fun TimelineItemTextViewWithLinkifiedUrlAndNestedParenthesisPreview() = ZenobiaPreview {
    val content = aTimelineItemTextContent(
        formattedBody = LinkifyHelper.linkify("The link should end after the '(ME)' ((url: github.com/element-hq/element-x-android/READ(ME)))!")
    )
    TimelineItemTextView(
        content = content,
        onLinkClick = {},
        onLinkLongClick = {},
    )
}
