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
package androidx.credentials.provider

import android.app.PendingIntent
import android.service.credentials.CallingAppInfo
import androidx.annotation.RequiresApi
import androidx.credentials.CredentialOption
import java.util.stream.Collectors

/**
 * Request received by the provider after the query phase of the get flow is complete i.e. the user
 * was presented with a list of credentials, and the user has now made a selection from the list of
 * [CredentialEntry] presented on the selector UI.
 *
 * This request will be added to the intent extras of the activity invoked by the [PendingIntent]
 * set on the [CredentialEntry] that the user selected. The request
 * must be extracted using the [PendingIntentHandler.retrieveProviderGetCredentialRequest] helper
 * API.
 *
 * @property credentialOptions the list of credential retrieval options containing the
 * required parameters.
 * This list is expected to contain a single [CredentialOption] when this
 * request is retrieved from the [android.app.Activity] invoked by the [android.app.PendingIntent]
 * set on a [PasswordCredentialEntry] or a [PublicKeyCredentialEntry]. This is because these
 * entries are created for a given [BeginGetPasswordOption] or a [BeginGetPublicKeyCredentialOption]
 * respectively, which corresponds to a single [CredentialOption].
 *
 * This list is expected to contain multiple [CredentialOption] when this request is retrieved
 * from the [android.app.Activity] invoked by the [android.app.PendingIntent]
 * set on a [RemoteEntry]. This is because when a remote entry is selected. the entire
 * request, containing multiple options, is sent to a remote device.
 *
 * @property callingAppInfo information pertaining to the calling application
 *
 * Note : Credential providers are not expected to utilize the constructor in this class for any
 * production flow. This constructor must only be used for testing purposes.
 */
@RequiresApi(34)
class ProviderGetCredentialRequest constructor(
    val credentialOptions: List<CredentialOption>,
    val callingAppInfo: CallingAppInfo
) {

    /** @hide */
    companion object {
        internal fun createFrom(request: android.service.credentials.GetCredentialRequest):
            ProviderGetCredentialRequest {
            return ProviderGetCredentialRequest(
                request.credentialOptions.stream()
                    .map { option ->
                        CredentialOption.createFrom(
                            option.type,
                            option.credentialRetrievalData,
                            option.candidateQueryData,
                            option.isSystemProviderRequired,
                            option.allowedProviders,
                        )
                    }
                    .collect(Collectors.toList()),
                request.callingAppInfo)
        }
    }
}
