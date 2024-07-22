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

package com.duckduckgo.performancemetrics.impl

import com.duckduckgo.di.scopes.AppScope
import com.duckduckgo.privacy.config.api.PrivacyFeaturePlugin
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesMultibinding(AppScope::class)
class PerformanceMetricsFeaturePlugin @Inject constructor(
    private val performanceMetricsRepository: PerformanceMetricsRepository,
) : PrivacyFeaturePlugin {

    override fun store(featureName: String, jsonString: String): Boolean {
        val performanceMetricsFeatureName = performanceMetricsFeatureValueOf(featureName) ?: return false
        if (performanceMetricsFeatureName.value == this.featureName) {
            val entity = PerformanceMetricsEntity(json = jsonString)
            performanceMetricsRepository.updateAll(performanceMetricsEntity = entity)
            return true
        }
        return false
    }

    override val featureName: String = PerformanceMetricsFeatureName.PerformanceMetrics.value
}
