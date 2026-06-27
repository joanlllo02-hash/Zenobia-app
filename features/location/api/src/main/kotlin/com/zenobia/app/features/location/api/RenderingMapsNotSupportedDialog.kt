/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.location.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.zenobia.app.libraries.designsystem.components.dialogs.ErrorDialog
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun RenderingMapsNotSupportedDialog(onSubmit: () -> Unit) {
    ErrorDialog(
        title = stringResource(CommonStrings.vulkan_not_supported_dialog_title_android),
        content = stringResource(CommonStrings.vulkan_not_supported_dialog_content_android),
        onSubmit = onSubmit,
    )
}

@PreviewsDayNight
@Composable
internal fun RenderingMapsNotSupportedDialogPreview() = ZenobiaPreview {
    RenderingMapsNotSupportedDialog(onSubmit = {})
}
