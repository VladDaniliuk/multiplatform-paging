/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.ui.samples

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.viewbinding.samples.R
import androidx.compose.ui.viewbinding.samples.databinding.SampleEditTextLayoutBinding
import androidx.compose.ui.viewbinding.samples.databinding.TestFragmentLayoutBinding
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.fragment.app.FragmentActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class FragmentRemoveTest {

    @get:Rule
    val rule = createAndroidComposeRule<EmptyFragmentActivity>()

    @Test
    fun testRemoval() {
        var show by mutableStateOf(true)

        rule.setContent {
            if (show) {
                AndroidViewBinding(TestFragmentLayoutBinding::inflate)
            }
        }

        var fragment = rule.activity.supportFragmentManager
            .findFragmentById(R.id.fragment_container)
        assertWithMessage("Fragment should be present when AndroidViewBinding is in the hierarchy")
            .that(fragment)
            .isNotNull()

        show = false

        rule.waitForIdle()

        fragment = rule.activity.supportFragmentManager
            .findFragmentById(R.id.fragment_container)
        assertWithMessage("Fragment should be removed when the AndroidViewBinding is removed")
            .that(fragment)
            .isNull()
    }

    @Test
    fun testRemovalRemovesState() {
        var show by mutableStateOf(true)

        rule.setContent {
            if (show) {
                AndroidViewBinding(TestFragmentLayoutBinding::inflate)
            }
        }

        var fragment = rule.activity.supportFragmentManager
            .findFragmentById(R.id.fragment_container)
        assertWithMessage("Fragment should be present when AndroidViewBinding is in the hierarchy")
            .that(fragment)
            .isNotNull()

        var binding = SampleEditTextLayoutBinding.bind(fragment!!.requireView())
        assertThat(binding.editText.text.toString()).isEqualTo("Default")

        // Update the state to allow verifying the state is destroyed when the
        // AndroidViewBinding is removed from composition
        rule.runOnUiThread {
            binding.editText.setText("Updated")
        }

        show = false

        rule.waitForIdle()

        fragment = rule.activity.supportFragmentManager
            .findFragmentById(R.id.fragment_container)
        assertWithMessage("Fragment should be removed when the AndroidViewBinding is removed")
            .that(fragment)
            .isNull()

        show = true

        rule.waitForIdle()

        fragment = rule.activity.supportFragmentManager
            .findFragmentById(R.id.fragment_container)
        assertWithMessage("Fragment should be present when AndroidViewBinding is in the hierarchy")
            .that(fragment)
            .isNotNull()
        binding = SampleEditTextLayoutBinding.bind(fragment!!.requireView())

        // State should be reset back to the default
        assertThat(binding.editText.text.toString()).isEqualTo("Default")
    }
}

class EmptyFragmentActivity : FragmentActivity()
