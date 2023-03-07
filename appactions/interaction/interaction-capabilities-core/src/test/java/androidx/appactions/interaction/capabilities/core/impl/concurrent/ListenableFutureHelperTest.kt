/*
 * Copyright 2023 The Android Open Source Project
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

package androidx.appactions.interaction.capabilities.core.impl.concurrent

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.coroutines.cancellation.CancellationException
@RunWith(JUnit4::class)
class ListenableFutureHelperTest {
    val tag = "tag"
    val SHORT_TIMEOUT_MILLIS = 50L

    @Test
    fun suspendToListenableFuture_smokeTest() {
        val stringFuture = ListenableFutureHelper.convertToListenableFuture<String>(tag) { "hello" }
        assertThat(stringFuture.get()).isEqualTo("hello")
    }

    @Test
    fun suspendToListenableFuture_pollingTest() {
        val stringChannel = Channel<String>(1)
        val stringFuture = ListenableFutureHelper.convertToListenableFuture<String>(tag) {
            getNextValueFromChannel(stringChannel)
        }
        assertThat(stringFuture.isDone()).isFalse()

        runBlocking {
            withTimeout(SHORT_TIMEOUT_MILLIS) {
                stringChannel.send("hello")
            }
        }
        assertThat(stringFuture.get()).isEqualTo("hello")
    }

    @Test
    fun suspendToListenableFuture_cancellationTest() {
        val stringChannel = Channel<String>(1)
        val cancellationChannel = Channel<Boolean>(1)
        val stringFuture = ListenableFutureHelper.convertToListenableFuture<String>(tag) {
            getNextValueFromChannel(stringChannel, cancellationChannel)
        }
        stringFuture.cancel(true)
        assertThat(stringFuture.isCancelled()).isTrue()
        runBlocking {
            withTimeout(SHORT_TIMEOUT_MILLIS) {
                assertThat(cancellationChannel.receive()).isTrue()
            }
        }
    }

    private suspend fun <T> getNextValueFromChannel(
        valueChannel: ReceiveChannel<T>,
        cancellationChannel: SendChannel<Boolean>? = null,
    ): T {
        try {
            return valueChannel.receive()
        } catch (e: CancellationException) {
            cancellationChannel?.send(true)
            throw e
        }
    }
}
