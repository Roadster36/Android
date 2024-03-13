/*
 * Copyright (c) 2023 DuckDuckGo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duckduckgo.subscriptions.impl.services

import com.duckduckgo.anvil.annotations.ContributesNonCachingServiceApi
import com.duckduckgo.di.scopes.AppScope
import com.duckduckgo.subscriptions.impl.repository.Entitlement
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

@ContributesNonCachingServiceApi(AppScope::class)
interface SubscriptionsService {
    @GET("https://subscriptions-dev.duckduckgo.com/api/subscription")
    suspend fun subscription(@Header("Authorization") authorization: String?): SubscriptionResponse

    @GET("https://subscriptions-dev.duckduckgo.com/api/checkout/portal")
    suspend fun portal(@Header("Authorization") authorization: String?): PortalResponse

    @POST("https://subscriptions-dev.duckduckgo.com/api/purchase/confirm/google")
    suspend fun confirm(
        @Header("Authorization") authorization: String?,
        @Body confirmationBody: ConfirmationBody,
    ): ConfirmationResponse
}

data class PortalResponse(val customerPortalUrl: String)

data class SubscriptionResponse(
    val productId: String,
    val startedAt: Long,
    val expiresOrRenewsAt: Long,
    val platform: String,
    val status: String,
)

data class ConfirmationBody(
    val packageName: String,
    val purchaseToken: String,
)

data class ConfirmationResponse(
    val email: String,
    val entitlements: List<ConfirmationEntitlement>,
    val subscription: SubscriptionResponse,
)

data class ConfirmationEntitlement(
    val product: String,
    val name: String,
)

fun List<ConfirmationEntitlement>.toEntitlements(): List<Entitlement> {
    return this.map { Entitlement(it.name, it.product) }
}
