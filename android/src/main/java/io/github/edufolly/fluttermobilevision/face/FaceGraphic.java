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
package io.github.edufolly.fluttermobilevision.face;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.google.android.gms.vision.face.Face;

import io.github.edufolly.fluttermobilevision.ui.GraphicOverlay;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    private static final int COLOR_CHOICES[] = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
            Color.MAGENTA,
            Color.RED,
            Color.WHITE,
            Color.YELLOW
    };
    private static int currentColorIndex;

    static {
        currentColorIndex = 0;
    }

    private Paint facePositionPaint;
    private Paint idPaint;
    private Paint boxPaint;

    private boolean showText;
    private volatile Face face;

    FaceGraphic(GraphicOverlay overlay, boolean showText) {
        super(overlay);

        this.showText = showText;

        currentColorIndex = (currentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[currentColorIndex];

        facePositionPaint = new Paint();
        facePositionPaint.setColor(selectedColor);

        idPaint = new Paint();
        idPaint.setColor(selectedColor);
        idPaint.setTextSize(ID_TEXT_SIZE);

        boxPaint = new Paint();
        boxPaint.setColor(selectedColor);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    }

    public void updateItem(Face face) {
        this.face = face;
        postInvalidate();
    }

    public Face getFace() {
        return face;
    }

    @Override
    public RectF getBoundingBox() {
        Face face = this.face;
        if (face == null) {
            return null;
        }

        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;

        return new RectF(left, top, right, bottom);
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Face face = this.face;
        if (face == null) {
            return;
        }

        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);

        if (showText) {
            canvas.drawCircle(x, y, FACE_POSITION_RADIUS, facePositionPaint);

            canvas.drawText("id: " + getId(), x + ID_X_OFFSET, y + ID_Y_OFFSET, idPaint);

            canvas.drawText("happiness: " + String.format("%.2f", face.getIsSmilingProbability()),
                    x - ID_X_OFFSET, y - ID_Y_OFFSET, idPaint);

            canvas.drawText("right eye: " + String.format("%.2f", face.getIsRightEyeOpenProbability()),
                    x + ID_X_OFFSET * 2, y + ID_Y_OFFSET * 2, idPaint);

            canvas.drawText("left eye: " + String.format("%.2f", face.getIsLeftEyeOpenProbability()),
                    x - ID_X_OFFSET * 2, y - ID_Y_OFFSET * 2, idPaint);
        }

        // Draws a bounding box around the face.
        canvas.drawRect(getBoundingBox(), boxPaint);
    }
}
