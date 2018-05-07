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

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

import io.github.edufolly.fluttermobilevision.ui.GraphicOverlay;

public class BarcodeGraphicTracker extends Tracker<Barcode> {
    private GraphicOverlay<BarcodeGraphic> overlay;
    private BarcodeGraphic graphic;

    private BarcodeUpdateListener barcodeUpdateListener;

    public BarcodeGraphicTracker(GraphicOverlay<BarcodeGraphic> overlay, BarcodeGraphic graphic,
                                 BarcodeUpdateListener barcodeUpdateListener) {

        this.overlay = overlay;
        this.graphic = graphic;
        this.barcodeUpdateListener = barcodeUpdateListener;
    }

    @Override
    public void onNewItem(int id, Barcode barcode) {
        graphic.setId(id);
        if (barcodeUpdateListener != null) {
            barcodeUpdateListener.onBarcodeDetected(barcode);
        }
    }

    @Override
    public void onUpdate(Detector.Detections<Barcode> detectionResults, Barcode barcode) {
        overlay.add(graphic);
        graphic.updateItem(barcode);
    }

    @Override
    public void onMissing(Detector.Detections<Barcode> detectionResults) {
        overlay.remove(graphic);
    }

    @Override
    public void onDone() {
        overlay.remove(graphic);
    }
}