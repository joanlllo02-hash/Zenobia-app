/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.knockrequests.impl.list

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.features.knockrequests.impl.R
import com.zenobia.app.features.knockrequests.impl.data.KnockRequestPresentable
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.designsystem.atomic.molecules.IconTitleSubtitleMolecule
import com.zenobia.app.libraries.designsystem.components.BigIcon
import com.zenobia.app.libraries.designsystem.components.ProgressDialog
import com.zenobia.app.libraries.designsystem.components.async.AsyncActionView
import com.zenobia.app.libraries.designsystem.components.avatar.Avatar
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarSize
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarType
import com.zenobia.app.libraries.designsystem.components.button.BackButton
import com.zenobia.app.libraries.designsystem.components.dialogs.ConfirmationDialog
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.text.toDp
import com.zenobia.app.libraries.designsystem.theme.components.Button
import com.zenobia.app.libraries.designsystem.theme.components.ButtonSize
import com.zenobia.app.libraries.designsystem.theme.components.CircularProgressIndicator
import com.zenobia.app.libraries.designsystem.theme.components.HorizontalDivider
import com.zenobia.app.libraries.designsystem.theme.components.Icon
import com.zenobia.app.libraries.designsystem.theme.components.OutlinedButton
import com.zenobia.app.libraries.designsystem.theme.components.Scaffold
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.designsystem.theme.components.TextButton
import com.zenobia.app.libraries.designsystem.theme.components.TopAppBar
import com.zenobia.app.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.ImmutableList

@Composable
fun KnockRequestsListView(
    state: KnockRequestsListState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            KnockRequestsListTopBar(onBackClick = onBackClick)
        },
        content = { padding ->
            KnockRequestsListContent(
                state = state,
                modifier = Modifier
                    .padding(padding)
                    .consumeWindowInsets(padding),
            )
        }
    )
}

@Composable
private fun KnockRequestsListContent(
    state: KnockRequestsListState,
    modifier: Modifier = Modifier,
) {
    fun onAcceptClick(knockRequest: KnockRequestPresentable) {
        state.eventSink(KnockRequestsListEvents.Accept(knockRequest))
    }

    fun onDeclineClick(knockRequest: KnockRequestPresentable) {
        state.eventSink(KnockRequestsListEvents.Decline(knockRequest))
    }

    fun onBanClick(knockRequest: KnockRequestPresentable) {
        state.eventSink(KnockRequestsListEvents.DeclineAndBan(knockRequest))
    }

    var bottomPaddingInPixels by remember { mutableIntStateOf(0) }

    Box(modifier.fillMaxSize()) {
        when (state.knockRequests) {
            is AsyncData.Success -> {
                val knockRequests = state.knockRequests.data
                if (knockRequests.isEmpty()) {
                    KnockRequestsEmptyList()
                } else {
                    KnockRequestsList(
                        knockRequests = knockRequests,
                        canAccept = state.permissions.canAccept,
                        canDecline = state.permissions.canDecline,
                        canBan = state.permissions.canBan,
                        onAcceptClick = ::onAcceptClick,
                        onDeclineClick = ::onDeclineClick,
                        onBanClick = ::onBanClick,
                        contentPadding = PaddingValues(bottom = bottomPaddingInPixels.toDp()),
                    )
                }
            }
            is AsyncData.Loading -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = spacedBy(16.dp),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    CircularProgressIndicator(color = ZenobiaTheme.colors.iconPrimary)
                    Text(
                        text = stringResource(R.string.screen_knock_requests_list_initial_loading_title),
                        style = ZenobiaTheme.typography.fontBodyLgRegular,
                        color = ZenobiaTheme.colors.textPrimary,
                    )
                }
            }
            else -> Unit
        }
        KnockRequestsActionsView(
            currentAction = state.currentAction,
            asyncAction = state.asyncAction,
            onConfirm = {
                state.eventSink(KnockRequestsListEvents.ConfirmCurrentAction)
            },
            onRetry = {
                state.eventSink(KnockRequestsListEvents.RetryCurrentAction)
            },
            onDismiss = {
                state.eventSink(KnockRequestsListEvents.ResetCurrentAction)
            },
        )
        if (state.canAcceptAll) {
            KnockRequestsAcceptAll(
                onClick = {
                    state.eventSink(KnockRequestsListEvents.AcceptAll)
                },
                onHeightChange = { height ->
                    bottomPaddingInPixels = height
                },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun KnockRequestsActionsView(
    currentAction: KnockRequestsAction,
    asyncAction: AsyncAction<Unit>,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        AsyncActionView(
            async = asyncAction,
            onSuccess = { onDismiss() },
            onErrorDismiss = onDismiss,
            confirmationDialog = {
                KnockRequestActionConfirmation(
                    currentAction = currentAction,
                    onSubmit = onConfirm,
                    onDismiss = onDismiss,
                )
            },
            progressDialog = {
                KnockRequestActionProgress(target = currentAction)
            },
            errorMessage = {
                when (currentAction) {
                    is KnockRequestsAction.Accept -> stringResource(R.string.screen_knock_requests_list_accept_failed_alert_description)
                    is KnockRequestsAction.Decline -> stringResource(R.string.screen_knock_requests_list_decline_failed_alert_description)
                    is KnockRequestsAction.DeclineAndBan -> stringResource(R.string.screen_knock_requests_list_decline_failed_alert_description)
                    KnockRequestsAction.AcceptAll -> stringResource(R.string.screen_knock_requests_list_accept_all_failed_alert_description)
                    else -> ""
                }
            },
            onRetry = onRetry,
        )
    }
}

@Composable
private fun KnockRequestActionConfirmation(
    currentAction: KnockRequestsAction,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (title, content, submitText) = when (currentAction) {
        KnockRequestsAction.AcceptAll -> Triple(
            stringResource(R.string.screen_knock_requests_list_accept_all_alert_title),
            stringResource(R.string.screen_knock_requests_list_accept_all_alert_description),
            stringResource(R.string.screen_knock_requests_list_accept_all_alert_confirm_button_title),
        )
        is KnockRequestsAction.Decline -> Triple(
            stringResource(R.string.screen_knock_requests_list_decline_alert_title),
            stringResource(R.string.screen_knock_requests_list_decline_alert_description, currentAction.knockRequest.getBestName()),
            stringResource(R.string.screen_knock_requests_list_decline_alert_confirm_button_title),
        )
        is KnockRequestsAction.DeclineAndBan -> Triple(
            stringResource(R.string.screen_knock_requests_list_ban_alert_title),
            stringResource(R.string.screen_knock_requests_list_ban_alert_description, currentAction.knockRequest.getBestName()),
            stringResource(R.string.screen_knock_requests_list_ban_alert_confirm_button_title),
        )
        else -> return
    }
    ConfirmationDialog(
        title = title,
        content = content,
        submitText = submitText,
        onSubmitClick = onSubmit,
        onDismiss = onDismiss,
        modifier = modifier,
    )
}

@Composable
private fun KnockRequestActionProgress(
    target: KnockRequestsAction,
    modifier: Modifier = Modifier,
) {
    val progressText = when (target) {
        is KnockRequestsAction.Accept -> stringResource(R.string.screen_knock_requests_list_accept_loading_title)
        is KnockRequestsAction.Decline -> stringResource(R.string.screen_knock_requests_list_decline_loading_title)
        is KnockRequestsAction.DeclineAndBan -> stringResource(R.string.screen_knock_requests_list_ban_loading_title)
        KnockRequestsAction.AcceptAll -> stringResource(R.string.screen_knock_requests_list_accept_all_loading_title)
        else -> return
    }
    ProgressDialog(
        text = progressText,
        modifier = modifier,
    )
}

@Composable
private fun KnockRequestsList(
    knockRequests: ImmutableList<KnockRequestPresentable>,
    canAccept: Boolean,
    canDecline: Boolean,
    canBan: Boolean,
    onAcceptClick: (KnockRequestPresentable) -> Unit,
    onDeclineClick: (KnockRequestPresentable) -> Unit,
    onBanClick: (KnockRequestPresentable) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
    ) {
        itemsIndexed(knockRequests) { index, knockRequest ->
            KnockRequestItem(
                knockRequest = knockRequest,
                onAcceptClick = onAcceptClick,
                canBan = canBan,
                canDecline = canDecline,
                canAccept = canAccept,
                onDeclineClick = onDeclineClick,
                onBanClick = onBanClick,
            )
            if (index != knockRequests.size - 1) {
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun KnockRequestItem(
    knockRequest: KnockRequestPresentable,
    canAccept: Boolean,
    canDecline: Boolean,
    canBan: Boolean,
    onAcceptClick: (KnockRequestPresentable) -> Unit,
    onDeclineClick: (KnockRequestPresentable) -> Unit,
    onBanClick: (KnockRequestPresentable) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Avatar(
            avatarData = knockRequest.getAvatarData(AvatarSize.KnockRequestItem),
            avatarType = AvatarType.User,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            // Name and date
            Row {
                Text(
                    modifier = Modifier
                        .clipToBounds()
                        .weight(1f),
                    text = knockRequest.getBestName(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = ZenobiaTheme.colors.textPrimary,
                    style = ZenobiaTheme.typography.fontBodyLgMedium,
                )
                val formattedDate = knockRequest.formattedDate
                if (!formattedDate.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formattedDate,
                        color = ZenobiaTheme.colors.textSecondary,
                        style = ZenobiaTheme.typography.fontBodySmRegular,
                    )
                }
            }
            // UserId
            if (!knockRequest.displayName.isNullOrEmpty()) {
                Text(
                    text = knockRequest.userId.value,
                    color = ZenobiaTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = ZenobiaTheme.typography.fontBodyMdRegular,
                )
            }
            // Reason
            val reason = knockRequest.reason
            if (!reason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                var isExpanded by rememberSaveable(knockRequest.userId) { mutableStateOf(false) }
                var isExpandable by rememberSaveable(knockRequest.userId) { mutableStateOf(false) }
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .animateContentSize()
                        .clickable(enabled = isExpandable) { isExpanded = !isExpanded }
                ) {
                    Text(
                        text = reason,
                        style = ZenobiaTheme.typography.fontBodyMdRegular,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                        onTextLayout = { result ->
                            if (!isExpanded && result.hasVisualOverflow) {
                                isExpandable = true
                            }
                        },
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Box(modifier = Modifier.size(24.dp)) {
                        if (isExpandable) {
                            Icon(
                                imageVector = if (isExpanded) CompoundIcons.ChevronUp() else CompoundIcons.ChevronDown(),
                                contentDescription = null,
                                tint = ZenobiaTheme.colors.iconTertiary,
                            )
                        }
                    }
                }
            }
            // Actions
            if (canDecline || canAccept) {
                Spacer(modifier = Modifier.height(12.dp))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (canDecline) {
                    OutlinedButton(
                        text = stringResource(CommonStrings.action_decline),
                        onClick = {
                            onDeclineClick(knockRequest)
                        },
                        size = ButtonSize.MediumLowPadding,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (canAccept) {
                    Button(
                        text = stringResource(CommonStrings.action_accept),
                        onClick = {
                            onAcceptClick(knockRequest)
                        },
                        size = ButtonSize.MediumLowPadding,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            if (canBan) {
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    text = stringResource(R.string.screen_knock_requests_list_decline_and_ban_action_title),
                    onClick = {
                        onBanClick(knockRequest)
                    },
                    destructive = true,
                    size = ButtonSize.Small,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun KnockRequestsAcceptAll(
    onClick: () -> Unit,
    onHeightChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(elevation = 24.dp, spotColor = Color.Transparent)
            .background(color = ZenobiaTheme.colors.bgCanvasDefault)
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .onSizeChanged { onHeightChange(it.height) }
    ) {
        OutlinedButton(
            text = stringResource(R.string.screen_knock_requests_list_accept_all_button_title),
            onClick = onClick,
            size = ButtonSize.Medium,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun KnockRequestsEmptyList(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.padding(
            horizontal = 32.dp,
            vertical = 48.dp,
        ),
        contentAlignment = Alignment.Center,
    ) {
        IconTitleSubtitleMolecule(
            title = stringResource(R.string.screen_knock_requests_list_empty_state_title),
            subTitle = stringResource(R.string.screen_knock_requests_list_empty_state_description),
            iconStyle = BigIcon.Style.Default(CompoundIcons.AskToJoin()),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KnockRequestsListTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        titleStr = stringResource(R.string.screen_knock_requests_list_title),
        navigationIcon = { BackButton(onClick = onBackClick) },
    )
}

@PreviewsDayNight
@Composable
internal fun KnockRequestsListViewPreview(
    @PreviewParameter(KnockRequestsListStateProvider::class) state: KnockRequestsListState
) = ZenobiaPreview {
    KnockRequestsListView(
        state = state,
        onBackClick = {},
    )
}
