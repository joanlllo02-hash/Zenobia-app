/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.test.timeline

import androidx.compose.runtime.Composable
import com.zenobia.app.features.messages.api.timeline.HtmlConverterProvider
import com.zenobia.app.wysiwyg.utils.HtmlConverter
import org.jsoup.nodes.Document

class FakeHtmlConverterProvider(
    private val transform: (String) -> CharSequence = { it },
    private val transformDom: (Document) -> CharSequence = { it.html() },
) : HtmlConverterProvider {
    @Composable
    override fun Update() = Unit

    override fun provide(): HtmlConverter {
        return object : HtmlConverter {
            override fun fromHtmlToSpans(html: String): CharSequence {
                return transform(html)
            }

            override fun fromDocumentToSpans(dom: Document): CharSequence {
                return transformDom(dom)
            }
        }
    }
}
