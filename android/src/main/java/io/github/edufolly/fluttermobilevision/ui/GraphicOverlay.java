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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.gms.vision.CameraSource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * A view which renders a series of custom graphics to be overlayed on top of an associated preview
 * (i.e., the camera preview).  The creator can add graphics objects, update the objects, and remove
 * them, triggering the appropriate drawing and invalidation within the view.<p>
 * <p>
 * Supports scaling and mirroring of the graphics relative the camera's preview properties.  The
 * idea is that detection items are expressed in terms of a preview size, but need to be scaled up
 * to the full view size, and also mirrored in the case of the front-facing camera.<p>
 * <p>
 * Associated {@link Graphic} items should use the following methods to convert to view coordinates
 * for the graphics that are drawn:
 * <ol>
 * <li>{@link Graphic#scaleX(float)} and {@link Graphic#scaleY(float)} adjust the size of the
 * supplied value from the preview scale to the view scale.</li>
 * <li>{@link Graphic#translateX(float)} and {@link Graphic#translateY(float)} adjust the coordinate
 * from the preview's coordinate system to the view coordinate system.</li>
 * </ol>
 */
public class GraphicOverlay<T extends GraphicOverlay.Graphic> extends View {
    private final Object lock = new Object();
    private int previewWidth;
    private float widthScaleFactor = 1.0f;
    private int previewHeight;
    private float heightScaleFactor = 1.0f;
    private int facing = CameraSource.CAMERA_FACING_BACK;
    private Set<T> graphics = new HashSet<>();

    /**
     * Base class for a custom graphics object to be rendered within the graphic overlay.  Subclass
     * this and implement the {@link Graphic#draw(Canvas)} method to define the
     * graphics element.  Add instances to the overlay using {@link GraphicOverlay#add(Graphic)}.
     */
    public static abstract class Graphic {
        private int id;
        private GraphicOverlay overlay;

        public Graphic(GraphicOverlay overlay) {
            this.overlay = overlay;
        }

        public abstract void draw(Canvas canvas);

        public abstract RectF getBoundingBox();

        public boolean contains(float x, float y) {
            RectF rect = getBoundingBox();
            return rect != null
                    && (rect.left < x && rect.right > x
                    && rect.top < y && rect.bottom > y);
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public float scaleX(float horizontal) {
            return horizontal * overlay.widthScaleFactor;
        }

        public float scaleY(float vertical) {
            return vertical * overlay.heightScaleFactor;
        }

        public float translateX(float x) {
            if (overlay.facing == CameraSource.CAMERA_FACING_FRONT) {
                return overlay.getWidth() - scaleX(x);
            } else {
                return scaleX(x);
            }
        }

        public float translateY(float y) {
            return scaleY(y);
        }

        public RectF translateRect(RectF inputRect) {
            RectF returnRect = new RectF();

            returnRect.left = translateX(inputRect.left);
            returnRect.top = translateY(inputRect.top);
            returnRect.right = translateX(inputRect.right);
            returnRect.bottom = translateY(inputRect.bottom);

            return returnRect;
        }

        public void postInvalidate() {
            overlay.postInvalidate();
        }
    }

    public GraphicOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Removes all graphics from the overlay.
     */
    public void clear() {
        synchronized (lock) {
            graphics.clear();
        }
        postInvalidate();
    }

    /**
     * Adds a graphic to the overlay.
     */
    public void add(T graphic) {
        synchronized (lock) {
            graphics.add(graphic);
        }
        postInvalidate();
    }

    /**
     * Removes a graphic from the overlay.
     */
    public void remove(T graphic) {
        synchronized (lock) {
            graphics.remove(graphic);
        }
        postInvalidate();
    }

    public T getBest(float rawX, float rawY) {
        synchronized (lock) {
            T best = null;

            int[] location = new int[2];
            getLocationOnScreen(location);
            float x = (rawX - location[0]) / widthScaleFactor;
            float y = (rawY - location[1]) / heightScaleFactor;

            float bestDistance = Float.MAX_VALUE;

            for (T graphic : graphics) {
                if (graphic.contains(x, y)) {
                    best = graphic;
                    break;
                }
                RectF rect = graphic.getBoundingBox();
                if (rect != null) {
                    float dx = x - graphic.getBoundingBox().centerX();
                    float dy = y - graphic.getBoundingBox().centerY();
                    float distance = (dx * dx) + (dy * dy);
                    if (distance < bestDistance) {
                        best = graphic;
                        bestDistance = distance;
                    }
                }
            }

            return best;
        }
    }

    /**
     * Returns a copy (as a list) of the set of all active graphics.
     *
     * @return list of all active graphics.
     */
    public List<T> getGraphics() {
        synchronized (lock) {
            return new Vector(graphics);
        }
    }

    /**
     * Sets the camera attributes for size and facing direction, which informs how to transform
     * image coordinates later.
     */
    public void setCameraInfo(int previewWidth, int previewHeight, int facing) {
        synchronized (lock) {
            this.previewWidth = previewWidth;
            this.previewHeight = previewHeight;
            this.facing = facing;
        }
        postInvalidate();
    }

    /**
     * Draws the overlay with its associated graphic objects.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (lock) {
            if ((previewWidth != 0) && (previewHeight != 0)) {
                widthScaleFactor = (float) canvas.getWidth() / (float) previewWidth;
                heightScaleFactor = (float) canvas.getHeight() / (float) previewHeight;
            }

            for (Graphic graphic : graphics) {
                graphic.draw(canvas);
            }
        }
    }
}