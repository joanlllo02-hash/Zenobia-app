/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.preferences.impl.developer.tracing

import com.zenobia.app.libraries.matrix.api.tracing.LogLevel

fun LogLevelItem.toLogLevel(): LogLevel {
    return when (this) {
        LogLevelItem.ERROR -> com.zenobia.app.libraries.matrix.api.tracing.LogLevel.ERROR
        LogLevelItem.WARN -> com.zenobia.app.libraries.matrix.api.tracing.LogLevel.WARN
        LogLevelItem.INFO -> com.zenobia.app.libraries.matrix.api.tracing.LogLevel.INFO
        LogLevelItem.DEBUG -> com.zenobia.app.libraries.matrix.api.tracing.LogLevel.DEBUG
        LogLevelItem.TRACE -> com.zenobia.app.libraries.matrix.api.tracing.LogLevel.TRACE
    }
}

fun LogLevel.toLogLevelItem(): LogLevelItem {
    return when (this) {
        LogLevel.ERROR -> LogLevelItem.ERROR
        LogLevel.WARN -> LogLevelItem.WARN
        LogLevel.INFO -> LogLevelItem.INFO
        LogLevel.DEBUG -> LogLevelItem.DEBUG
        LogLevel.TRACE -> LogLevelItem.TRACE
    }
}
