/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.logout.impl.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.zenobia.app.features.deactivation.impl.R
import com.zenobia.app.libraries.designsystem.components.dialogs.ConfirmationDialog
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun AccountDeactivationConfirmationDialog(
    onSubmitClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    ConfirmationDialog(
        title = stringResource(id = R.string.screen_deactivate_account_title),
        content = stringResource(R.string.screen_deactivate_account_confirmation_dialog_content),
        submitText = stringResource(id = CommonStrings.action_delete),
        onSubmitClick = onSubmitClick,
        onDismiss = onDismiss,
        destructiveSubmit = true,
    )
}
