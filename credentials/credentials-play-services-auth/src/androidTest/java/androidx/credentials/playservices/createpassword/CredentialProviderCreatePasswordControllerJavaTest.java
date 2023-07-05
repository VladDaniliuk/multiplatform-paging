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

package androidx.credentials.playservices.createpassword;

import static com.google.common.truth.Truth.assertThat;

import static org.junit.Assert.assertThrows;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import androidx.credentials.CreateCredentialResponse;
import androidx.credentials.CreatePasswordRequest;
import androidx.credentials.CreatePasswordResponse;
import androidx.credentials.playservices.TestCredentialsActivity;
import androidx.credentials.playservices.TestUtils;
import androidx.credentials.playservices.controllers.CreatePassword.CredentialProviderCreatePasswordController;
import androidx.test.core.app.ActivityScenario;
import androidx.test.filters.SmallTest;

import com.google.android.gms.auth.api.identity.SignInPassword;

import kotlin.Unit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
@SmallTest
public class CredentialProviderCreatePasswordControllerJavaTest {

    private final boolean mUseFragmentActivity;

    @Parameterized.Parameters
    public static Object[] data() {
        return new Object[] {true, false};
    }

    public CredentialProviderCreatePasswordControllerJavaTest(final boolean useFragmentActivity)
            throws Throwable {
        mUseFragmentActivity = useFragmentActivity;
    }

    interface TestActivityListener {
        void onActivity(Activity a);
    }

    private void launchTestActivity(TestActivityListener listener) {
        if (mUseFragmentActivity && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            ActivityScenario<androidx.credentials.playservices.TestCredentialsFragmentActivity>
                    activityScenario =
                            ActivityScenario.launch(
                                    androidx.credentials.playservices
                                            .TestCredentialsFragmentActivity.class);
            activityScenario.onActivity(
                    activity -> {
                        listener.onActivity((Activity) activity);
                    });
        } else {
            ActivityScenario<TestCredentialsActivity> activityScenario =
                    ActivityScenario.launch(TestCredentialsActivity.class);
            activityScenario.onActivity(
                    activity -> {
                        listener.onActivity((Activity) activity);
                    });
        }
    }

    @Test
    public void convertResponseToCredentialManager_unitInput_success() {
        String expectedResponseType = new CreatePasswordResponse().getType();
        launchTestActivity(
                activity -> {
                    CreateCredentialResponse actualResponse =
                            CredentialProviderCreatePasswordController.getInstance(activity)
                                    .convertResponseToCredentialManager(Unit.INSTANCE);

                    assertThat(actualResponse.getType()).isEqualTo(expectedResponseType);
                    assertThat(TestUtils.Companion.equals(actualResponse.getData(), Bundle.EMPTY))
                            .isTrue();
                });
    }

    @Test
    public void convertRequestToPlayServices_createPasswordRequest_success() {
        String expectedId = "LM";
        String expectedPassword = "SodaButton";
        launchTestActivity(
                activity -> {
                    SignInPassword actualRequest =
                            CredentialProviderCreatePasswordController.getInstance(activity)
                                    .convertRequestToPlayServices(
                                            new CreatePasswordRequest(expectedId, expectedPassword))
                                    .getSignInPassword();

                    assertThat(actualRequest.getPassword()).isEqualTo(expectedPassword);
                    assertThat(actualRequest.getId()).isEqualTo(expectedId);
                });
    }

    @Test
    public void convertRequestToPlayServices_nullRequest_throws() {
        ActivityScenario<TestCredentialsActivity> activityScenario =
                ActivityScenario.launch(TestCredentialsActivity.class);
        launchTestActivity(
                activity -> {
                    assertThrows(
                            "null create password request must throw exception",
                            NullPointerException.class,
                            () ->
                                    CredentialProviderCreatePasswordController.getInstance(activity)
                                            .convertRequestToPlayServices(null)
                                            .getSignInPassword());
                });
    }

    @Test
    public void convertResponseToCredentialManager_nullRequest_throws() {
        launchTestActivity(
                activity -> {
                    assertThrows(
                            "null unit response must throw exception",
                            NullPointerException.class,
                            () ->
                                    CredentialProviderCreatePasswordController.getInstance(activity)
                                            .convertResponseToCredentialManager(null));
                });
    }

    @Test
    public void duplicateGetInstance_shouldBeEqual() {
        launchTestActivity(
                activity -> {
                    CredentialProviderCreatePasswordController firstInstance =
                            CredentialProviderCreatePasswordController.getInstance(activity);
                    CredentialProviderCreatePasswordController secondInstance =
                            CredentialProviderCreatePasswordController.getInstance(activity);
                    assertThat(firstInstance).isEqualTo(secondInstance);
                });
    }
}
