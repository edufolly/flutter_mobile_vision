/*
 * Copyright (C) The Android Open Source Project
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
package io.github.edufolly.fluttermobilevision.barcode;

import android.util.Log;


import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

import io.github.edufolly.fluttermobilevision.ui.GraphicOverlay;

public class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {
    private GraphicOverlay<BarcodeGraphic> graphicOverlay;
    private BarcodeUpdateListener barcodeUpdateListener;
    private boolean showText;

    public BarcodeTrackerFactory(GraphicOverlay<BarcodeGraphic> graphicOverlay,
                                 BarcodeUpdateListener barcodeUpdateListener, boolean showText) {

        this.graphicOverlay = graphicOverlay;
        this.barcodeUpdateListener = barcodeUpdateListener;
        this.showText = showText;
    }

    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        BarcodeGraphic graphic = new BarcodeGraphic(graphicOverlay, showText);
        try {
            return new BarcodeGraphicTracker(graphicOverlay, graphic, barcodeUpdateListener);
        } catch (Exception ex) {
            Log.d("BarcodeTrackerFactory", ex.getMessage(), ex);
        }
        return null;
    }

}
