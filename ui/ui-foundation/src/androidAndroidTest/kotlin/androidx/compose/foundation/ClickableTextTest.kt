/*
 * Copyright 2020 The Android Open Source Project
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

package androidx.compose.foundation

import androidx.test.filters.SmallTest
import androidx.ui.core.Modifier
import androidx.ui.core.testTag
import androidx.ui.test.createComposeRule
import androidx.ui.test.performClick
import androidx.ui.test.onNodeWithTag
import androidx.ui.test.runOnIdle
import androidx.compose.ui.text.AnnotatedString
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@SmallTest
@RunWith(JUnit4::class)
class ClickableTextTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun onclick_callback() {
        val onClick: (Int) -> Unit = mock()
        composeTestRule.setContent {
            ClickableText(
                modifier = Modifier.testTag("clickableText"),
                text = AnnotatedString("android"),
                onClick = onClick
            )
        }

        onNodeWithTag("clickableText").performClick()

        runOnIdle {
            verify(onClick, times(1)).invoke(any())
        }
    }
}
