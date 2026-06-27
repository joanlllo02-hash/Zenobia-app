/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.home.impl.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.zenobia.app.appconfig.RoomListConfig
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.features.home.impl.HomeNavigationBarItem
import com.zenobia.app.features.home.impl.R
import com.zenobia.app.features.home.impl.filters.RoomListFiltersState
import com.zenobia.app.features.home.impl.filters.RoomListFiltersView
import com.zenobia.app.features.home.impl.filters.aRoomListFiltersState
import com.zenobia.app.features.home.impl.spacefilters.SpaceFiltersEvent
import com.zenobia.app.features.home.impl.spacefilters.SpaceFiltersState
import com.zenobia.app.features.home.impl.spacefilters.aSelectedSpaceFiltersState
import com.zenobia.app.features.home.impl.spacefilters.anUnselectedSpaceFiltersState
import com.zenobia.app.libraries.designsystem.atomic.atoms.RedIndicatorAtom
import com.zenobia.app.libraries.designsystem.components.TopAppBarScrollBehaviorLayout
import com.zenobia.app.libraries.designsystem.components.avatar.Avatar
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarSize
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarType
import com.zenobia.app.libraries.designsystem.modifiers.backgroundVerticalGradient
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.preview.USER_NAME_ALICE
import com.zenobia.app.libraries.designsystem.theme.aliasScreenTitle
import com.zenobia.app.libraries.designsystem.theme.components.DropdownMenu
import com.zenobia.app.libraries.designsystem.theme.components.DropdownMenuItem
import com.zenobia.app.libraries.designsystem.theme.components.Icon
import com.zenobia.app.libraries.designsystem.theme.components.IconButton
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.designsystem.theme.components.TopAppBar
import com.zenobia.app.libraries.matrix.api.core.SessionId
import com.zenobia.app.libraries.matrix.api.user.MatrixUser
import com.zenobia.app.libraries.matrix.ui.components.aMatrixUser
import com.zenobia.app.libraries.matrix.ui.components.aMatrixUserList
import com.zenobia.app.libraries.matrix.ui.model.getAvatarData
import com.zenobia.app.libraries.testtags.TestTags
import com.zenobia.app.libraries.testtags.testTag
import com.zenobia.app.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    selectedNavigationItem: HomeNavigationBarItem,
    currentUserAndNeighbors: ImmutableList<MatrixUser>,
    showAvatarIndicator: Boolean,
    areSearchResultsDisplayed: Boolean,
    onToggleSearch: () -> Unit,
    onMenuActionClick: (RoomListMenuAction) -> Unit,
    onOpenSettings: () -> Unit,
    onAccountSwitch: (SessionId) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    canReportBug: Boolean,
    displayFilters: Boolean,
    filtersState: RoomListFiltersState,
    spaceFiltersState: SpaceFiltersState,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        TopAppBar(
            modifier = Modifier
                .backgroundVerticalGradient(
                    isVisible = !areSearchResultsDisplayed,
                )
                .statusBarsPadding(),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
            ),
            title = {
                val displayTitle = when (selectedNavigationItem) {
                    HomeNavigationBarItem.Chats -> {
                        when (spaceFiltersState) {
                            is SpaceFiltersState.Selected -> spaceFiltersState.selectedFilter.spaceRoom.displayName
                            else -> stringResource(selectedNavigationItem.labelRes)
                        }
                    }
                    HomeNavigationBarItem.Spaces -> stringResource(selectedNavigationItem.labelRes)
                }
                Text(
                    modifier = Modifier.semantics {
                        heading()
                    },
                    style = ZenobiaTheme.typography.aliasScreenTitle,
                    text = displayTitle,
                )
            },
            navigationIcon = {
                NavigationIcon(
                    currentUserAndNeighbors = currentUserAndNeighbors,
                    showAvatarIndicator = showAvatarIndicator,
                    onAccountSwitch = onAccountSwitch,
                    onClick = onOpenSettings,
                )
            },
            actions = {
                if (selectedNavigationItem == HomeNavigationBarItem.Chats) {
                    RoomListMenuItems(
                        onToggleSearch = onToggleSearch,
                        onMenuActionClick = onMenuActionClick,
                        canReportBug = canReportBug,
                        spaceFiltersState = spaceFiltersState,
                    )
                }
            },
            // We want a 16dp left padding for the navigationIcon :
            // 4dp from default TopAppBarHorizontalPadding
            // 8dp from AccountIcon default padding (because of IconButton)
            // 4dp extra padding using left insets
            windowInsets = WindowInsets(left = 4.dp),
        )
        if (displayFilters) {
            TopAppBarScrollBehaviorLayout(scrollBehavior = scrollBehavior) {
                RoomListFiltersView(
                    state = filtersState,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun RowScope.RoomListMenuItems(
    onToggleSearch: () -> Unit,
    onMenuActionClick: (RoomListMenuAction) -> Unit,
    canReportBug: Boolean,
    spaceFiltersState: SpaceFiltersState,
) {
    IconButton(
        onClick = onToggleSearch,
    ) {
        Icon(
            imageVector = CompoundIcons.Search(),
            contentDescription = stringResource(CommonStrings.action_search),
        )
    }
    SpaceFilterButton(spaceFiltersState = spaceFiltersState)
    if (RoomListConfig.HAS_DROP_DOWN_MENU) {
        var showMenu by remember { mutableStateOf(false) }
        IconButton(
            onClick = { showMenu = !showMenu }
        ) {
            Icon(
                imageVector = CompoundIcons.OverflowVertical(),
                contentDescription = null,
            )
        }
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            if (RoomListConfig.SHOW_INVITE_MENU_ITEM) {
                DropdownMenuItem(
                    onClick = {
                        showMenu = false
                        onMenuActionClick(RoomListMenuAction.InviteFriends)
                    },
                    text = { Text(stringResource(id = CommonStrings.action_invite)) },
                    leadingIcon = {
                        Icon(
                            imageVector = CompoundIcons.ShareAndroid(),
                            tint = ZenobiaTheme.colors.iconSecondary,
                            contentDescription = null,
                        )
                    }
                )
            }
            if (RoomListConfig.SHOW_REPORT_PROBLEM_MENU_ITEM && canReportBug) {
                DropdownMenuItem(
                    onClick = {
                        showMenu = false
                        onMenuActionClick(RoomListMenuAction.ReportBug)
                    },
                    text = { Text(stringResource(id = CommonStrings.common_report_a_problem)) },
                    leadingIcon = {
                        Icon(
                            imageVector = CompoundIcons.ChatProblem(),
                            tint = ZenobiaTheme.colors.iconSecondary,
                            contentDescription = null,
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun SpaceFilterButton(
    spaceFiltersState: SpaceFiltersState,
) {
    if (spaceFiltersState == SpaceFiltersState.Disabled) return

    fun onClick() {
        when (spaceFiltersState) {
            is SpaceFiltersState.Unselected -> spaceFiltersState.eventSink(SpaceFiltersEvent.Unselected.ShowFilters)
            is SpaceFiltersState.Selected -> spaceFiltersState.eventSink(SpaceFiltersEvent.Selected.ClearSelection)
            else -> Unit
        }
    }

    val isSelected = spaceFiltersState is SpaceFiltersState.Selected
    IconButton(
        onClick = ::onClick,
        colors = if (isSelected) {
            IconButtonDefaults.iconButtonColors(
                containerColor = ZenobiaTheme.colors.bgActionPrimaryRest,
                contentColor = ZenobiaTheme.colors.iconOnSolidPrimary,
            )
        } else {
            IconButtonDefaults.iconButtonColors()
        },
    ) {
        Icon(
            imageVector = CompoundIcons.Filter(),
            contentDescription = stringResource(R.string.screen_roomlist_your_spaces),
        )
    }
}

@Composable
private fun NavigationIcon(
    currentUserAndNeighbors: ImmutableList<MatrixUser>,
    showAvatarIndicator: Boolean,
    onAccountSwitch: (SessionId) -> Unit,
    onClick: () -> Unit,
) {
    if (currentUserAndNeighbors.size == 1) {
        AccountIcon(
            matrixUser = currentUserAndNeighbors.single(),
            isCurrentAccount = true,
            showAvatarIndicator = showAvatarIndicator,
            onClick = onClick,
        )
    } else {
        // Render a vertical pager
        val pagerState = rememberPagerState(initialPage = 1) { currentUserAndNeighbors.size }
        // Listen to page changes and switch account if needed
        val latestOnAccountSwitch by rememberUpdatedState(onAccountSwitch)
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.settledPage }.collect { page ->
                latestOnAccountSwitch(SessionId(currentUserAndNeighbors[page].userId.value))
            }
        }
        VerticalPager(
            state = pagerState,
            modifier = Modifier.height(48.dp),
        ) { page ->
            AccountIcon(
                matrixUser = currentUserAndNeighbors[page],
                isCurrentAccount = page == 1,
                showAvatarIndicator = page == 1 && showAvatarIndicator,
                onClick = if (page == 1) {
                    onClick
                } else {
                    {}
                },
            )
        }
    }
}

@Composable
private fun AccountIcon(
    matrixUser: MatrixUser,
    isCurrentAccount: Boolean,
    showAvatarIndicator: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val testTag = if (isCurrentAccount) Modifier.testTag(TestTags.homeScreenSettings) else Modifier
    IconButton(
        modifier = modifier.then(testTag),
        onClick = onClick,
    ) {
        Box {
            val avatarData by remember(matrixUser) {
                derivedStateOf {
                    matrixUser.getAvatarData(size = AvatarSize.CurrentUserTopBar)
                }
            }
            Avatar(
                avatarData = avatarData,
                avatarType = AvatarType.User,
                contentDescription = if (isCurrentAccount) {
                    if (showAvatarIndicator) {
                        stringResource(CommonStrings.a11y_settings_with_required_action)
                    } else {
                        stringResource(CommonStrings.common_settings)
                    }
                } else {
                    null
                },
            )
            if (showAvatarIndicator) {
                RedIndicatorAtom(
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewsDayNight
@Composable
internal fun HomeTopBarPreview() = ZenobiaPreview {
    HomeTopBar(
        selectedNavigationItem = HomeNavigationBarItem.Chats,
        currentUserAndNeighbors = persistentListOf(aMatrixUser(id = "@id:domain", displayName = USER_NAME_ALICE)),
        showAvatarIndicator = false,
        areSearchResultsDisplayed = false,
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
        onOpenSettings = {},
        onAccountSwitch = {},
        onToggleSearch = {},
        canReportBug = true,
        displayFilters = true,
        filtersState = aRoomListFiltersState(),
        spaceFiltersState = anUnselectedSpaceFiltersState(),
        onMenuActionClick = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewsDayNight
@Composable
internal fun HomeTopBarSpaceFiltersSelectedPreview() = ZenobiaPreview {
    HomeTopBar(
        selectedNavigationItem = HomeNavigationBarItem.Chats,
        currentUserAndNeighbors = persistentListOf(aMatrixUser(id = "@id:domain", displayName = USER_NAME_ALICE)),
        showAvatarIndicator = false,
        areSearchResultsDisplayed = false,
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
        onOpenSettings = {},
        onAccountSwitch = {},
        onToggleSearch = {},
        canReportBug = true,
        displayFilters = true,
        filtersState = aRoomListFiltersState(),
        spaceFiltersState = aSelectedSpaceFiltersState(),
        onMenuActionClick = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewsDayNight
@Composable
internal fun HomeTopBarSpacesPreview() = ZenobiaPreview {
    HomeTopBar(
        selectedNavigationItem = HomeNavigationBarItem.Spaces,
        currentUserAndNeighbors = persistentListOf(aMatrixUser(id = "@id:domain", displayName = USER_NAME_ALICE)),
        showAvatarIndicator = false,
        areSearchResultsDisplayed = false,
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
        onOpenSettings = {},
        onAccountSwitch = {},
        onToggleSearch = {},
        canReportBug = true,
        displayFilters = false,
        filtersState = aRoomListFiltersState(),
        spaceFiltersState = anUnselectedSpaceFiltersState(),
        onMenuActionClick = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewsDayNight
@Composable
internal fun HomeTopBarWithIndicatorPreview() = ZenobiaPreview {
    HomeTopBar(
        selectedNavigationItem = HomeNavigationBarItem.Chats,
        currentUserAndNeighbors = persistentListOf(aMatrixUser(id = "@id:domain", displayName = USER_NAME_ALICE)),
        showAvatarIndicator = true,
        areSearchResultsDisplayed = false,
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
        onOpenSettings = {},
        onAccountSwitch = {},
        onToggleSearch = {},
        canReportBug = true,
        displayFilters = true,
        filtersState = aRoomListFiltersState(),
        spaceFiltersState = anUnselectedSpaceFiltersState(),
        onMenuActionClick = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewsDayNight
@Composable
internal fun HomeTopBarMultiAccountPreview() = ZenobiaPreview {
    HomeTopBar(
        selectedNavigationItem = HomeNavigationBarItem.Chats,
        currentUserAndNeighbors = aMatrixUserList().take(3).toImmutableList(),
        showAvatarIndicator = false,
        areSearchResultsDisplayed = false,
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
        onOpenSettings = {},
        onAccountSwitch = {},
        onToggleSearch = {},
        canReportBug = true,
        displayFilters = true,
        filtersState = aRoomListFiltersState(),
        spaceFiltersState = anUnselectedSpaceFiltersState(),
        onMenuActionClick = {},
    )
}
