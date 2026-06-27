/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.timeline.components.customreaction.picker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.zenobia.app.emojibasebindings.Emoji
import com.zenobia.app.features.messages.impl.timeline.components.customreaction.EmojiItem
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.text.toSp
import com.zenobia.app.libraries.designsystem.theme.components.Icon
import com.zenobia.app.libraries.designsystem.theme.components.IconSource
import com.zenobia.app.libraries.designsystem.theme.components.SearchBar
import com.zenobia.app.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmojiPicker(
    onSelectEmoji: (Emoji) -> Unit,
    state: EmojiPickerState,
    selectedEmojis: ImmutableSet<String>,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { state.categories.size })
    Column(modifier) {
        SearchBar(
            modifier = Modifier.padding(bottom = 10.dp),
            queryState = state.searchQuery,
            resultState = state.searchResults,
            active = state.isSearchActive,
            onActiveChange = { state.eventSink(EmojiPickerEvent.ToggleSearchActive(it)) },
            windowInsets = WindowInsets(0, 0, 0, 0),
            placeHolderTitle = stringResource(CommonStrings.emoji_picker_search_placeholder),
        ) { emojis ->
            EmojiResults(
                emojis = emojis,
                isEmojiSelected = { selectedEmojis.contains(it.unicode) },
                onSelectEmoji = onSelectEmoji,
            )
        }

        if (!state.isSearchActive) {
            SecondaryTabRow(
                selectedTabIndex = pagerState.currentPage,
            ) {
                state.categories.forEachIndexed { index, category ->
                    Tab(
                        icon = {
                            when (category.icon) {
                                is IconSource.Resource -> Icon(
                                    resourceId = category.icon.id,
                                    contentDescription = stringResource(id = category.titleId)
                                )
                                is IconSource.Vector -> Icon(
                                    imageVector = category.icon.vector,
                                    contentDescription = stringResource(id = category.titleId)
                                )
                            }
                        },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
            ) { index ->
                val emojis = state.categories[index].emojis
                EmojiResults(
                    emojis = emojis,
                    isEmojiSelected = { selectedEmojis.contains(it.unicode) },
                    onSelectEmoji = onSelectEmoji,
                )
            }
        }
    }
}

@Composable
private fun EmojiResults(
    emojis: ImmutableList<Emoji>,
    isEmojiSelected: (Emoji) -> Boolean,
    onSelectEmoji: (Emoji) -> Unit,
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(minSize = 48.dp),
        contentPadding = PaddingValues(vertical = 10.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(emojis, key = { it.unicode }) { item ->
            EmojiItem(
                modifier = Modifier.aspectRatio(1f),
                item = item,
                isSelected = isEmojiSelected(item),
                onSelectEmoji = onSelectEmoji,
                emojiSize = 32.dp.toSp(),
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun EmojiPickerPreview(@PreviewParameter(EmojiPickerStateProvider::class) state: EmojiPickerState) = ZenobiaPreview {
    EmojiPicker(
        onSelectEmoji = {},
        state = state,
        selectedEmojis = persistentSetOf("😀", "😄", "😃"),
        modifier = Modifier.fillMaxWidth(),
    )
}
