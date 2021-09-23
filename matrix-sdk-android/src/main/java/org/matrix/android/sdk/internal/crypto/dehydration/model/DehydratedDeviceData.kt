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
 * Class representing the `device_data` part of the dehydrated device JSON object
 */
@JsonClass(generateAdapter = true)
data class DehydratedDeviceData (
        /**
         * Type of the algorithm used for pickling the account data. Should be "m.dehydration.v1.olm"
         */
        @Json(name = "algorithm")
        val algorithm: String,

        /**
         * Pickled account data of the device
         */
        @Json(name = "account")
        val account: String,

        /**
         * Optionally the passphrase used to pickle the data
         */
        @Json(name = "passphrase")
        val passphrase: String? = null
)