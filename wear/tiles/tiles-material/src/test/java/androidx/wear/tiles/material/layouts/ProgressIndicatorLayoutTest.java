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

package androidx.wear.tiles.material.layouts;

import static com.google.common.truth.Truth.assertThat;

import static java.nio.charset.StandardCharsets.UTF_8;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.wear.tiles.DeviceParametersBuilders.DeviceParameters;
import androidx.wear.tiles.LayoutElementBuilders.Box;
import androidx.wear.tiles.LayoutElementBuilders.Column;
import androidx.wear.tiles.LayoutElementBuilders.LayoutElement;
import androidx.wear.tiles.ModifiersBuilders.ElementMetadata;
import androidx.wear.tiles.ModifiersBuilders.Modifiers;
import androidx.wear.tiles.material.CircularProgressIndicator;
import androidx.wear.tiles.material.Text;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.internal.DoNotInstrument;

@RunWith(AndroidJUnit4.class)
@DoNotInstrument
public class ProgressIndicatorLayoutTest {
    private static final Context CONTEXT = ApplicationProvider.getApplicationContext();
    private static final DeviceParameters DEVICE_PARAMETERS =
            new DeviceParameters.Builder().setScreenWidthDp(192).setScreenHeightDp(192).build();
    private static final Text PRIMARY_LABEL = new Text.Builder(CONTEXT, "Primary label").build();
    private static final Text SECONDARY_LABEL =
            new Text.Builder(CONTEXT, "Secondary label").build();

    @Test
    public void testAll() {
        LayoutElement content = new Box.Builder().build();
        CircularProgressIndicator progressIndicator =
                new CircularProgressIndicator.Builder().build();
        ProgressIndicatorLayout layout =
                new ProgressIndicatorLayout.Builder(DEVICE_PARAMETERS)
                        .setContent(content)
                        .setProgressIndicatorContent(progressIndicator)
                        .setPrimaryLabelTextContent(PRIMARY_LABEL)
                        .setSecondaryLabelTextContent(SECONDARY_LABEL)
                        .build();

        assertLayout(layout, progressIndicator, content, PRIMARY_LABEL, SECONDARY_LABEL);
    }

    @Test
    public void testContentOnly() {
        LayoutElement content = new Box.Builder().build();
        ProgressIndicatorLayout layout =
                new ProgressIndicatorLayout.Builder(DEVICE_PARAMETERS).setContent(content).build();

        assertLayout(layout, null, content, null, null);
    }

    @Test
    public void testIndicatorOnly() {
        CircularProgressIndicator progressIndicator =
                new CircularProgressIndicator.Builder().build();
        ProgressIndicatorLayout layout =
                new ProgressIndicatorLayout.Builder(DEVICE_PARAMETERS)
                        .setProgressIndicatorContent(progressIndicator)
                        .build();

        assertLayout(layout, progressIndicator, null, null, null);
    }

    @Test
    public void testEmpty() {
        ProgressIndicatorLayout layout =
                new ProgressIndicatorLayout.Builder(DEVICE_PARAMETERS).build();

        assertLayout(layout, null, null, null, null);
    }

    @Test
    public void testWrongElement() {
        Column box = new Column.Builder().build();

        assertThat(ProgressIndicatorLayout.fromLayoutElement(box)).isNull();
    }

    @Test
    public void testWrongBox() {
        Box box = new Box.Builder().build();

        assertThat(ProgressIndicatorLayout.fromLayoutElement(box)).isNull();
    }

    @Test
    public void testWrongTag() {
        Box box =
                new Box.Builder()
                    .setModifiers(
                        new Modifiers.Builder()
                            .setMetadata(
                                new ElementMetadata.Builder()
                                    .setTagData("test".getBytes(UTF_8))
                                    .build())
                            .build())
                    .build();

        assertThat(ProgressIndicatorLayout.fromLayoutElement(box)).isNull();
    }

    @Test
    public void testWrongLengthTag() {
        Box box =
                new Box.Builder()
                    .setModifiers(
                        new Modifiers.Builder()
                            .setMetadata(
                                new ElementMetadata.Builder()
                                    .setTagData(
                                        ProgressIndicatorLayout
                                            .METADATA_TAG_PREFIX
                                            .getBytes(UTF_8))
                                    .build())
                            .build())
                    .build();

        assertThat(ProgressIndicatorLayout.fromLayoutElement(box)).isNull();
    }

    private void assertLayout(
            @NonNull ProgressIndicatorLayout actualLayout,
            @Nullable LayoutElement expectedProgressIndicator,
            @Nullable LayoutElement expectedContent,
            @Nullable LayoutElement expectedPrimaryLabel,
            @Nullable LayoutElement expectedSecondaryLabel) {
        assertLayoutIsEqual(
                actualLayout,
                expectedProgressIndicator,
                expectedContent,
                expectedPrimaryLabel,
                expectedSecondaryLabel);

        Box box = new Box.Builder().addContent(actualLayout).build();

        ProgressIndicatorLayout newLayout =
                ProgressIndicatorLayout.fromLayoutElement(box.getContents().get(0));

        assertThat(newLayout).isNotNull();
        assertLayoutIsEqual(
                newLayout,
                expectedProgressIndicator,
                expectedContent,
                expectedPrimaryLabel,
                expectedSecondaryLabel);

        assertThat(ProgressIndicatorLayout.fromLayoutElement(actualLayout)).isEqualTo(actualLayout);
    }

    private void assertLayoutIsEqual(
            @NonNull ProgressIndicatorLayout actualLayout,
            @Nullable LayoutElement expectedProgressIndicator,
            @Nullable LayoutElement expectedContent,
            @Nullable LayoutElement expectedPrimaryLabel,
            @Nullable LayoutElement expectedSecondaryLabel) {
        byte[] expectedMetadata = ProgressIndicatorLayout.METADATA_TAG_BASE.clone();

        if (expectedProgressIndicator == null) {
            assertThat(actualLayout.getProgressIndicatorContent()).isNull();
        } else {
            assertThat(actualLayout.getProgressIndicatorContent().toLayoutElementProto())
                    .isEqualTo(expectedProgressIndicator.toLayoutElementProto());
            expectedMetadata[ProgressIndicatorLayout.FLAG_INDEX] =
                    (byte)
                            (expectedMetadata[ProgressIndicatorLayout.FLAG_INDEX]
                                    | ProgressIndicatorLayout.PROGRESS_INDICATOR_PRESENT);
        }

        if (expectedContent == null) {
            assertThat(actualLayout.getContent()).isNull();
        } else {
            assertThat(actualLayout.getContent().toLayoutElementProto())
                    .isEqualTo(expectedContent.toLayoutElementProto());
            expectedMetadata[ProgressIndicatorLayout.FLAG_INDEX] =
                    (byte)
                            (expectedMetadata[ProgressIndicatorLayout.FLAG_INDEX]
                                    | ProgressIndicatorLayout.CONTENT_PRESENT);
        }

        if (expectedPrimaryLabel == null) {
            assertThat(actualLayout.getPrimaryLabelTextContent()).isNull();
        } else {
            assertThat(actualLayout.getPrimaryLabelTextContent().toLayoutElementProto())
                    .isEqualTo(expectedPrimaryLabel.toLayoutElementProto());
            expectedMetadata[ProgressIndicatorLayout.FLAG_INDEX] =
                    (byte)
                            (expectedMetadata[ProgressIndicatorLayout.FLAG_INDEX]
                                    | ProgressIndicatorLayout.PRIMARY_LABEL_PRESENT);
        }

        if (expectedSecondaryLabel == null) {
            assertThat(actualLayout.getSecondaryLabelTextContent()).isNull();
        } else {
            assertThat(actualLayout.getSecondaryLabelTextContent().toLayoutElementProto())
                    .isEqualTo(expectedSecondaryLabel.toLayoutElementProto());
            expectedMetadata[ProgressIndicatorLayout.FLAG_INDEX] =
                    (byte)
                            (expectedMetadata[ProgressIndicatorLayout.FLAG_INDEX]
                                    | ProgressIndicatorLayout.SECONDARY_LABEL_PRESENT);
        }

        assertThat(actualLayout.getMetadataTag()).isEqualTo(expectedMetadata);
    }
}
