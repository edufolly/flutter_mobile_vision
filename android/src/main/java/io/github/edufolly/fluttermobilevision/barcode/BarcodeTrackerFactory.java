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

import android.content.Context;
import android.util.Log;


import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

import io.github.edufolly.fluttermobilevision.barcode.BarcodeGraphic;
import io.github.edufolly.fluttermobilevision.barcode.BarcodeGraphicTracker;
import io.github.edufolly.fluttermobilevision.ui.GraphicOverlay;


/**
 * Factory for creating a tracker and associated graphic to be associated with a new barcode.  The
 * multi-processor uses this factory to create barcode trackers as needed -- one for each barcode.
 */
public class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;
    private Context mContext;
    private boolean showText;

    public BarcodeTrackerFactory(GraphicOverlay<BarcodeGraphic> mGraphicOverlay,
                                 Context mContext, boolean showText) {

        this.mGraphicOverlay = mGraphicOverlay;
        this.mContext = mContext;
        this.showText = showText;
    }

    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        BarcodeGraphic graphic = new BarcodeGraphic(mGraphicOverlay, showText);
        try {
            return new BarcodeGraphicTracker(mGraphicOverlay, graphic, mContext);
        } catch (Exception ex) {
            Log.d("BarcodeTrackerFactory", ex.getMessage(), ex);
        }
        return null;
    }

}
