/*
 * Copyright 2022 The Android Open Source Project
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

package androidx.camera.integration.extensions.camera2extensions

import android.content.Context
import android.hardware.camera2.CameraManager
import androidx.camera.camera2.Camera2Config
import androidx.camera.integration.extensions.util.Camera2ExtensionsTestUtil
import androidx.camera.integration.extensions.util.Camera2ExtensionsTestUtil.assertCanOpenExtensionsSession
import androidx.camera.integration.extensions.util.Camera2ExtensionsTestUtil.findNextSupportedCameraId
import androidx.camera.testing.CameraUtil
import androidx.camera.testing.LabTestRule
import androidx.camera.testing.StressTestRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.LargeTest
import androidx.test.filters.SdkSuppress
import kotlinx.coroutines.runBlocking
import org.junit.Assume.assumeTrue
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@LargeTest
@RunWith(Parameterized::class)
@SdkSuppress(minSdkVersion = 31)
class Camera2ExtensionsSwitchCameraStressTest(
    private val cameraId: String,
    private val extensionMode: Int
) {
    @get:Rule
    val useCamera = CameraUtil.grantCameraPermissionAndPreTest(
        CameraUtil.PreTestCameraIdList(Camera2Config.defaultConfig())
    )

    @get:Rule
    val labTest: LabTestRule = LabTestRule()
    companion object {
        @ClassRule
        @JvmField val stressTest = StressTestRule()

        @Parameterized.Parameters(name = "cameraId = {0}, extensionMode = {1}")
        @JvmStatic
        fun parameters() = Camera2ExtensionsTestUtil.getAllCameraIdExtensionModeCombinations()
    }

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    @LabTestRule.LabTestOnly
    @Test
    fun switchCameras(): Unit = runBlocking {
        val nextCameraId = findNextSupportedCameraId(context, cameraId, extensionMode)
        assumeTrue(nextCameraId != null)
        repeat(Camera2ExtensionsTestUtil.STRESS_TEST_OPERATION_REPEAT_COUNT) {
            assertCanOpenExtensionsSession(cameraManager, cameraId, extensionMode)
            assertCanOpenExtensionsSession(cameraManager, nextCameraId!!, extensionMode)
        }

        // Test if preview frame can update and it can take a picture after the stress test.
        assertCanOpenExtensionsSession(
            cameraManager,
            cameraId,
            extensionMode,
            verifyOutput = true
        )
    }
}