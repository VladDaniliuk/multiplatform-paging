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

package androidx.compose.animation.core

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Bounds
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Position
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

// TODO: 1) move Dp.VectorConverter, Position.VectorConverter, etc from animation package to core
// TODO: 2) Make the vals in this class private and remove @PublishedApi annotation
/**
 * A type converter that converts a [Dp] to a [AnimationVector1D], and vice versa.
 */
@PublishedApi
internal val DpToVector: TwoWayConverter<Dp, AnimationVector1D> = TwoWayConverter(
    convertToVector = { AnimationVector1D(it.value) },
    convertFromVector = { Dp(it.value) }
)

/**
 * A type converter that converts a [Position] to a [AnimationVector2D], and vice versa.
 */
@PublishedApi
internal val PositionToVector: TwoWayConverter<Position, AnimationVector2D> =
    TwoWayConverter(
        convertToVector = { AnimationVector2D(it.x.value, it.y.value) },
        convertFromVector = { Position(it.v1.dp, it.v2.dp) }
    )

/**
 * A type converter that converts a [Size] to a [AnimationVector2D], and vice versa.
 */
@PublishedApi
internal val SizeToVector: TwoWayConverter<Size, AnimationVector2D> =
    TwoWayConverter(
        convertToVector = { AnimationVector2D(it.width, it.height) },
        convertFromVector = { Size(it.v1, it.v2) }
    )

/**
 * A type converter that converts a [Bounds] to a [AnimationVector4D], and vice versa.
 */
@PublishedApi
internal val BoundsToVector: TwoWayConverter<Bounds, AnimationVector4D> =
    TwoWayConverter(
        convertToVector = {
            AnimationVector4D(it.left.value, it.top.value, it.right.value, it.bottom.value)
        },
        convertFromVector = { Bounds(it.v1.dp, it.v2.dp, it.v3.dp, it.v4.dp) }
    )

/**
 * A type converter that converts a [Offset] to a [AnimationVector2D], and vice versa.
 */
@PublishedApi
internal val OffsetToVector: TwoWayConverter<Offset, AnimationVector2D> =
    TwoWayConverter(
        convertToVector = { AnimationVector2D(it.x, it.y) },
        convertFromVector = { Offset(it.v1, it.v2) }
    )

/**
 * A type converter that converts a [IntOffset] to a [AnimationVector2D], and vice versa.
 */
@PublishedApi
internal val IntOffsetToVector: TwoWayConverter<IntOffset, AnimationVector2D> =
    TwoWayConverter(
        convertToVector = { AnimationVector2D(it.x.toFloat(), it.y.toFloat()) },
        convertFromVector = { IntOffset(it.v1.roundToInt(), it.v2.roundToInt()) }
    )

/**
 * A type converter that converts a [IntSize] to a [AnimationVector2D], and vice versa.
 */
@PublishedApi
internal val IntSizeToVector: TwoWayConverter<IntSize, AnimationVector2D> =
    TwoWayConverter(
        { AnimationVector2D(it.width.toFloat(), it.height.toFloat()) },
        { IntSize(it.v1.roundToInt(), it.v2.roundToInt()) }
    )

/**
 * A type converter that converts a [Rect] to a [AnimationVector4D], and vice versa.
 */
@PublishedApi
internal val RectToVector: TwoWayConverter<Rect, AnimationVector4D> =
    TwoWayConverter(
        convertToVector = {
            AnimationVector4D(it.left, it.top, it.right, it.bottom)
        },
        convertFromVector = {
            Rect(it.v1, it.v2, it.v3, it.v4)
        }
    )