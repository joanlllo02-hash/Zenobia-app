/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.knockrequests.impl.list

import com.zenobia.app.features.knockrequests.impl.data.KnockRequestPresentable

sealed interface KnockRequestsListEvents {
    data class Accept(val knockRequest: KnockRequestPresentable) : KnockRequestsListEvents
    data class Decline(val knockRequest: KnockRequestPresentable) : KnockRequestsListEvents
    data class DeclineAndBan(val knockRequest: KnockRequestPresentable) : KnockRequestsListEvents
    data object AcceptAll : KnockRequestsListEvents
    data object ResetCurrentAction : KnockRequestsListEvents
    data object RetryCurrentAction : KnockRequestsListEvents
    data object ConfirmCurrentAction : KnockRequestsListEvents
}
