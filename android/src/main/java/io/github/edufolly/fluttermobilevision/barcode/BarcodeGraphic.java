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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.google.android.gms.vision.barcode.Barcode;

import io.github.edufolly.fluttermobilevision.ui.GraphicOverlay;


/**
 * Graphic instance for rendering barcode position, size, and ID within an associated graphic
 * overlay view.
 */
public class BarcodeGraphic extends GraphicOverlay.Graphic {

    private static final int COLOR_CHOICES[] = {
            Color.GREEN, Color.BLUE, Color.RED, Color.YELLOW
    };
    private static int currentColorIndex;
    private Paint rectPaint;
    private Paint textPaint;
    private volatile Barcode barcode;

    static {
        currentColorIndex = 0;
    }

    BarcodeGraphic(GraphicOverlay overlay, boolean showText) {
        super(overlay);

        currentColorIndex = (currentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[currentColorIndex];

        rectPaint = new Paint();
        rectPaint.setColor(selectedColor);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(4.0f);

        if (showText) {
            textPaint = new Paint();
            textPaint.setColor(selectedColor);
            textPaint.setTextSize(36.0f);
        }
    }

    public Barcode getBarcode() {
        return barcode;
    }

    /**
     * @return RectF that represents the graphic's bounding box.
     */
    public RectF getBoundingBox() {
        Barcode barcode = this.barcode;
        if (barcode == null) {
            return null;
        }
        RectF rect = new RectF(barcode.getBoundingBox());
        rect = translateRect(rect);
        return rect;
    }

    /**
     * Updates the barcode instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    public void updateItem(Barcode barcode) {
        this.barcode = barcode;
        postInvalidate();
    }

    /**
     * Draws the barcode annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Barcode barcode = this.barcode;
        if (barcode == null) {
            return;
        }

        // Draws the bounding box around the barcode.
        RectF rect = new RectF(barcode.getBoundingBox());
        rect = translateRect(rect);
        canvas.drawRect(rect, rectPaint);

        if (textPaint != null) {
            // Draws a label at the bottom of the barcode indicate the barcode value that was detected.
            canvas.drawText(barcode.rawValue, rect.left, rect.bottom, textPaint);
        }
    }
}
