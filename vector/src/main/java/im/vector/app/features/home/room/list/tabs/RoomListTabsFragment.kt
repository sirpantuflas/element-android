/*
 * Copyright (c) 2020 New Vector Ltd
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

package im.vector.app.features.home.room.list.tabs

import android.os.Bundle
import android.view.View
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.tabs.TabLayoutMediator
import im.vector.app.R
import im.vector.app.core.platform.VectorBaseFragment
import kotlinx.android.synthetic.main.fragment_room_list_tabs.*
import timber.log.Timber
import javax.inject.Inject

class RoomListTabsFragment @Inject constructor(
        private val viewModelFactory: RoomListTabsViewModel.Factory
) : VectorBaseFragment(), RoomListTabsViewModel.Factory by viewModelFactory {

    private val viewModel: RoomListTabsViewModel by fragmentViewModel()
    private lateinit var pagerAdapter: RoomListTabsPagerAdapter

    override fun getLayoutResId() = R.layout.fragment_room_list_tabs

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pagerAdapter = RoomListTabsPagerAdapter(this, requireContext())
        viewPager.adapter = pagerAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val item = RoomListTabsPagerAdapter.TABS[position]
            tab.text = getString(item.titleRes).toLowerCase().capitalize()
        }.attach()
    }

    override fun invalidate() = withState(viewModel) { state ->
        Timber.v("Invalidate state: $state")
    }
}
