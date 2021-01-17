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
package io.github.edufolly.fluttermobilevision.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Point;


import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.util.List;

import io.github.edufolly.fluttermobilevision.ui.GraphicOverlay;

/**
 * Graphic instance 
 */
public class ScanAreaGraphic extends GraphicOverlay.Graphic {

    private static final int TEXT_COLOR = Color.WHITE;
    private static final Paint rectPaint = new Paint();
    private static final Paint textPaint = new Paint();

    static {
        rectPaint.setColor(TEXT_COLOR);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(4.0f);

        textPaint.setColor(TEXT_COLOR);
        textPaint.setTextSize(50.0f);
    }

    private int scanAreaHeight;
    private int scanAreaWidth;


    public ScanAreaGraphic(GraphicOverlay overlay, int scanAreaHeight, int scanAreaWidth) {
        super(overlay);

        this.scanAreaHeight = scanAreaHeight;
        this.scanAreaWidth = scanAreaWidth;

        postInvalidate();
    }

    // Unused
    public RectF getBoundingBox() {
        return new RectF();
    }

    /**
     * Draws the RectF outlining the scan area
     */
    @Override
    public void draw(Canvas canvas) {
        int canvasW = canvas.getWidth();
        int canvasH = canvas.getHeight();
        Point centerOfCanvas = new Point(canvasW / 2, canvasH / 2);
        int rectH = this.scanAreaHeight;
        int rectW = this.scanAreaWidth;
        int left = centerOfCanvas.x - (rectW / 2);
        int top = centerOfCanvas.y - (rectH / 2);
        int right = centerOfCanvas.x + (rectW / 2);
        int bottom = centerOfCanvas.y + (rectH / 2);
        RectF rect = new RectF(left, top, right, bottom);
        canvas.drawRect(rect, rectPaint);
    }
}