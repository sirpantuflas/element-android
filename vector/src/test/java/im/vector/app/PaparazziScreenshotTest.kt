/*
 * Copyright (c) 2022 New Vector Ltd
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

package im.vector.app

import android.os.Build
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_3
import app.cash.paparazzi.Paparazzi
import com.airbnb.epoxy.EpoxyController
import com.airbnb.mvrx.Success
import im.vector.app.core.extensions.configureWith
import im.vector.app.core.resources.StringProvider
import im.vector.app.core.utils.DimensionConverter
import im.vector.app.features.home.AvatarRenderer
import im.vector.app.features.home.room.detail.timeline.format.RoomHistoryVisibilityFormatter
import im.vector.app.features.roomprofile.settings.RoomSettingsController
import im.vector.app.features.roomprofile.settings.RoomSettingsViewState
import im.vector.app.test.fakes.FakeVectorPreferences
import io.mockk.coJustRun
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.util.MatrixItem
import java.lang.reflect.Field
import java.lang.reflect.Modifier

class PaparazziScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
            deviceConfig = PIXEL_3,
            theme = "Theme.Vector.Light",
    )

    @Before
    fun setUp() {
        setFinalStaticValue(Build::class.java.getDeclaredField("MANUFACTURER"), "GOOGLE")
    }

    @Test
    fun `room settings`() = runTest {
        // Material components aren't fully supported yet https://github.com/cashapp/paparazzi/issues/223
        val strings = StringProvider(paparazzi.resources)
        val fakeAvatarRender = FakeAvatarRender()
        val fakeDimensionConverter = FakeDimensionConverter()
        val fakeVectorPreferences = FakeVectorPreferences().also { it.givenDeveloperMode(false) }
        val controller = RoomSettingsController(
                strings,
                fakeAvatarRender.instance,
                fakeDimensionConverter.instance,
                RoomHistoryVisibilityFormatter(strings),
                fakeVectorPreferences.instance,
        )

        val view = inflateRecyclerView(controller)

        controller.setData(
                RoomSettingsViewState(
                        roomId = "room-id",
                        roomSummary = Success(
                                RoomSummary(
                                        roomId = "!room-id",
                                        isEncrypted = true,
                                        encryptionEventTs = null,
                                        typingUsers = emptyList(),
                                        displayName = "A room name",
                                        topic = "A room topic",
                                )
                        )
                )
        )

        paparazzi.snapshot(view)
    }

    private fun inflateRecyclerView(controller: EpoxyController): View {
        val view = paparazzi.inflate<FrameLayout>(R.layout.fragment_generic_recycler)
        val recyclerView = view.findViewById<RecyclerView>(R.id.genericRecyclerView)
        recyclerView.configureWith(controller)
        return view
    }
}

fun setFinalStaticValue(field: Field, value: Any) {
    val modifiersField = Field::class.java.getDeclaredField("modifiers")
    modifiersField.isAccessible = true
    modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())
    field.isAccessible = true

    field.set(null, value)
}

class FakeDimensionConverter {

    val instance = mockk<DimensionConverter>().also {
        every { it.dpToPx(any()) }.answers { arg ->
            arg.invocation.args.first() as Int
        }
    }
}

class FakeAvatarRender {

    val instance = mockk<AvatarRenderer>().also {
        coJustRun { it.render(any<MatrixItem>(), any()) }
    }
}
