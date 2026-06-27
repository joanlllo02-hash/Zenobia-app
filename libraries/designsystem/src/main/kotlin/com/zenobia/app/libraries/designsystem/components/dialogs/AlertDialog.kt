/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.designsystem.components.dialogs

import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.ZenobiaThemedPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewGroup
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.DialogPreview
import com.zenobia.app.libraries.designsystem.theme.components.SimpleAlertDialogContent
import com.zenobia.app.libraries.ui.strings.CommonStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialog(
    content: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    submitText: String = AlertDialogDefaults.submitText,
) {
    BasicAlertDialog(modifier = modifier, onDismissRequest = onDismiss) {
        AlertDialogContent(
            title = title,
            content = content,
            submitText = submitText,
            onSubmitClick = onDismiss,
        )
    }
}

@Composable
private fun AlertDialogContent(
    content: String,
    onSubmitClick: () -> Unit,
    title: String? = AlertDialogDefaults.title,
    submitText: String = AlertDialogDefaults.submitText,
) {
    SimpleAlertDialogContent(
        title = title,
        content = content,
        submitText = submitText,
        onSubmitClick = onSubmitClick,
    )
}

object AlertDialogDefaults {
    val title: String? @Composable get() = null
    val submitText: String @Composable get() = stringResource(id = CommonStrings.action_ok)
}

@Preview(group = PreviewGroup.Dialogs)
@Composable
internal fun AlertDialogContentPreview() {
    ZenobiaThemedPreview(showBackground = false) {
        DialogPreview {
            AlertDialogContent(
                content = "Content",
                onSubmitClick = {},
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun AlertDialogPreview() = ZenobiaPreview {
    AlertDialog(
        content = "Content",
        onDismiss = {},
    )
}
