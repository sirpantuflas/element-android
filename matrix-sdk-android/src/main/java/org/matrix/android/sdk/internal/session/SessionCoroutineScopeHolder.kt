/*
 * Copyright (c) 2021 The Matrix.org Foundation C.I.C.
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

package org.matrix.android.sdk.internal.session

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.newCoroutineContext
import kotlinx.coroutines.withContext
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.SessionLifecycleObserver
import org.matrix.android.sdk.internal.di.MatrixCoroutineScope
import javax.inject.Inject

@SessionScope
internal class SessionCoroutineScopeHolder @Inject constructor(
        @MatrixCoroutineScope private val parentScope: CoroutineScope,
) : SessionLifecycleObserver {

    val scope: CoroutineScope = CoroutineScope(parentScope.coroutineContext + SupervisorJob() + CoroutineName("Session"))

    override fun onSessionStopped(session: Session) {
        scope.cancelChildren()
    }

    override fun onClearCache(session: Session) {
        scope.cancelChildren()
    }

    private fun CoroutineScope.cancelChildren() {
        coroutineContext.cancelChildren(CancellationException("Closing session"))
    }
}
