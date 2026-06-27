/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.zenobia.app.compound.annotations.CoreColorToken
import com.zenobia.app.compound.previews.ColorListPreview
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.SemanticColors
import com.zenobia.app.compound.tokens.generated.internal.DarkColorTokens
import com.zenobia.app.compound.tokens.generated.internal.LightColorTokens
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import kotlinx.collections.immutable.persistentMapOf

/**
 * Room list.
 */
val SemanticColors.roomListRoomName
    get() = textPrimary

val SemanticColors.roomListRoomMessage
    get() = textSecondary

val SemanticColors.roomListRoomMessageDate
    get() = textSecondary

val SemanticColors.unreadIndicator
    get() = iconAccentTertiary

val SemanticColors.placeholderBackground
    get() = bgSubtleSecondary

// This color is not present in Semantic color, so put hard-coded value for now
@OptIn(CoreColorToken::class)
val SemanticColors.messageFromMeBackground
    get() = if (isLight) LightColorTokens.colorGray400 else DarkColorTokens.colorGray500

// This color is not present in Semantic color, so put hard-coded value for now
@OptIn(CoreColorToken::class)
val SemanticColors.messageFromOtherBackground
    get() = if (isLight) LightColorTokens.colorGray300 else DarkColorTokens.colorGray400

// This color is not present in Semantic color, so put hard-coded value for now
@OptIn(CoreColorToken::class)
val SemanticColors.progressIndicatorTrackColor
    get() = if (isLight) LightColorTokens.colorAlphaGray500 else DarkColorTokens.colorAlphaGray500

// This color is not present in Semantic color, so put hard-coded value for now
@OptIn(CoreColorToken::class)
val SemanticColors.bgSubtleTertiary
    get() = if (isLight) LightColorTokens.colorGray100 else DarkColorTokens.colorGray100

// Temporary color, which is not in the token right now
val SemanticColors.temporaryColorBgSpecial
    get() = if (isLight) Color(0xFFE4E8F0) else Color(0xFF3A4048)

// This color is not present in Semantic color, so put hard-coded value for now
@OptIn(CoreColorToken::class)
val SemanticColors.pinDigitBg
    get() = if (isLight) LightColorTokens.colorGray300 else DarkColorTokens.colorGray400

@OptIn(CoreColorToken::class)
val SemanticColors.pinnedMessageBannerIndicator
    get() = if (isLight) LightColorTokens.colorAlphaGray600 else DarkColorTokens.colorAlphaGray600

@OptIn(CoreColorToken::class)
val SemanticColors.pinnedMessageBannerBorder
    get() = if (isLight) LightColorTokens.colorAlphaGray400 else DarkColorTokens.colorAlphaGray400

val SemanticColors.floatingDateBadgeBackground
    get() = if (isLight) bgCanvasDefault else bgSubtlePrimary

@PreviewsDayNight
@Composable
internal fun ColorAliasesPreview() = ZenobiaPreview {
    ColorListPreview(
        backgroundColor = Color.Black,
        foregroundColor = Color.White,
        colors = persistentMapOf(
            "roomListRoomName" to ZenobiaTheme.colors.roomListRoomName,
            "roomListRoomMessage" to ZenobiaTheme.colors.roomListRoomMessage,
            "roomListRoomMessageDate" to ZenobiaTheme.colors.roomListRoomMessageDate,
            "unreadIndicator" to ZenobiaTheme.colors.unreadIndicator,
            "placeholderBackground" to ZenobiaTheme.colors.placeholderBackground,
            "messageFromMeBackground" to ZenobiaTheme.colors.messageFromMeBackground,
            "messageFromOtherBackground" to ZenobiaTheme.colors.messageFromOtherBackground,
            "progressIndicatorTrackColor" to ZenobiaTheme.colors.progressIndicatorTrackColor,
            "temporaryColorBgSpecial" to ZenobiaTheme.colors.temporaryColorBgSpecial,
        )
    )
}
