/*
 * Copyright (c) 2021 New Vector Ltd
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

package org.matrix.android.sdk.internal.crypto.dehydration.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Representation of the response of the claim dehydrated device request
 */
@JsonClass(generateAdapter = true)
data class ClaimDehydratedDeviceResponse (
        /**
         * True if the dehydrated device has been properly claimed. False otherwise.
         */
        @Json(name = "success")
        val success: Boolean
)