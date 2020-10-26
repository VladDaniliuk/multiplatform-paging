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

package androidx.compose.ui.window

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.node.Owner
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.enforce
import androidx.compose.ui.unit.hasFixedHeight
import androidx.compose.ui.unit.hasFixedWidth
import androidx.test.espresso.Espresso
import androidx.test.espresso.Root
import androidx.test.espresso.assertion.ViewAssertions
import androidx.ui.test.ComposeTestRule
import org.hamcrest.CoreMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import kotlin.math.max

// TODO(b/139861182): Remove all of this and provide helpers on rule
internal fun ComposeTestRule.popupMatches(popupTestTag: String, viewMatcher: Matcher<in View>) {
    // Make sure that current measurement/drawing is finished
    runOnIdle { }
    Espresso.onView(CoreMatchers.instanceOf(Owner::class.java))
        .inRoot(PopupLayoutMatcher(popupTestTag))
        .check(ViewAssertions.matches(viewMatcher))
}

internal class PopupLayoutMatcher(val testTag: String) : TypeSafeMatcher<Root>() {

    var lastSeenWindowParams: WindowManager.LayoutParams? = null

    override fun describeTo(description: Description?) {
        description?.appendText("PopupLayoutMatcher")
    }

    // TODO(b/141101446): Find a way to match the window used by the popup
    override fun matchesSafely(item: Root?): Boolean {
        val matches = item != null && isPopupLayout(item.decorView, testTag)
        if (matches) {
            lastSeenWindowParams = item!!.windowLayoutParams.get()
        }
        return matches
    }
}

internal class ActivityWithFlagSecure : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }
}

/**
 * A Container Box implementation used for selection children and handle layout
 */
@Composable
internal fun SimpleContainer(
    modifier: Modifier = Modifier,
    width: Dp? = null,
    height: Dp? = null,
    children: @Composable () -> Unit
) {
    Layout(children, modifier) { measurables, incomingConstraints ->
        val containerConstraints = Constraints()
            .copy(
                width?.toIntPx() ?: 0,
                width?.toIntPx() ?: Constraints.Infinity,
                height?.toIntPx() ?: 0,
                height?.toIntPx() ?: Constraints.Infinity
            )
            .enforce(incomingConstraints)
        val childConstraints = containerConstraints.copy(minWidth = 0, minHeight = 0)
        var placeable: Placeable? = null
        val containerWidth = if (
            containerConstraints.hasFixedWidth
        ) {
            containerConstraints.maxWidth
        } else {
            placeable = measurables.firstOrNull()?.measure(childConstraints)
            max((placeable?.width ?: 0), containerConstraints.minWidth)
        }
        val containerHeight = if (
            containerConstraints.hasFixedHeight
        ) {
            containerConstraints.maxHeight
        } else {
            if (placeable == null) {
                placeable = measurables.firstOrNull()?.measure(childConstraints)
            }
            max((placeable?.height ?: 0), containerConstraints.minHeight)
        }
        layout(containerWidth, containerHeight) {
            val p = placeable ?: measurables.firstOrNull()?.measure(childConstraints)
            p?.let {
                val position = Alignment.Center.align(
                    IntSize(
                        containerWidth - it.width,
                        containerHeight - it.height
                    )
                )
                it.placeRelative(
                    position.x,
                    position.y
                )
            }
        }
    }
}
