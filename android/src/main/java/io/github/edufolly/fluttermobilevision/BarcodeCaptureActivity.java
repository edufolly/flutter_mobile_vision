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
package io.github.edufolly.fluttermobilevision;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import io.github.edufolly.fluttermobilevision.barcode.BarcodeGraphic;
import io.github.edufolly.fluttermobilevision.barcode.BarcodeGraphicTracker;
import io.github.edufolly.fluttermobilevision.barcode.BarcodeTrackerFactory;
import io.github.edufolly.fluttermobilevision.ui.CameraSource;
import io.github.edufolly.fluttermobilevision.ui.CameraSourcePreview;
import io.github.edufolly.fluttermobilevision.ui.GraphicOverlay;
import io.github.edufolly.fluttermobilevision.util.BarcodeException;

public final class BarcodeCaptureActivity extends Activity
        implements BarcodeGraphicTracker.BarcodeUpdateListener {

    public static final String AUTO_FOCUS = "AUTO_FOCUS";
    public static final String USE_FLASH = "USE_FLASH";
    public static final String FORMATS = "FORMATS";

    public static final String BARCODE_OBJECT = "Barcode";
    public static final String ERROR = "Error";

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;

//    helper objects for detecting taps and pinches.
//    private ScaleGestureDetector scaleGestureDetector;
//    private GestureDetector gestureDetector;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.barcode_capture);

            mPreview = findViewById(R.id.barcode_preview);
            mGraphicOverlay = findViewById(R.id.barcode_graphic_overlay);

            // read parameters from the intent used to launch the activity.
            boolean autoFocus = getIntent().getBooleanExtra(AUTO_FOCUS, false);
            boolean useFlash = getIntent().getBooleanExtra(USE_FLASH, false);

            int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (rc == PackageManager.PERMISSION_GRANTED) {
                createCameraSource(autoFocus, useFlash);
            } else {
                throw new BarcodeException("Camera permission is needed.");
            }

//            gestureDetector = new GestureDetector(this, new CaptureGestureListener());
//            scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        } catch (Exception e) {
            onError(e);
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent e) {
//        boolean b = scaleGestureDetector.onTouchEvent(e);
//
//        boolean c = gestureDetector.onTouchEvent(e);
//
//        return b || c || super.onTouchEvent(e);
//    }

    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) throws BarcodeException {
        Context context = getApplicationContext();

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(getIntent().getIntExtra(FORMATS, Barcode.ALL_FORMATS))
                .build();

        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay, this);

        barcodeDetector.setProcessor(
                new MultiProcessor.Builder<>(barcodeFactory).build());

        if (!barcodeDetector.isOperational()) {
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                throw new BarcodeException("Low Storage.");
            }
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        CameraSource.Builder builder = new CameraSource
                .Builder(getApplicationContext(), barcodeDetector)
                .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(metrics.heightPixels, metrics.widthPixels)
                .setRequestedFps(15.0f);

        mCameraSource = builder.build();
    }

    private void onError(Exception e) {
        Intent data = new Intent();
        data.putExtra(ERROR, e);
        setResult(CommonStatusCodes.ERROR);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            startCameraSource();
        } catch (Exception e) {
            onError(e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }

    @SuppressLint("MissingPermission")
    private void startCameraSource() throws SecurityException, BarcodeException {

        int code = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(getApplicationContext());

        if (code != ConnectionResult.SUCCESS) {
            throw new BarcodeException("Google Api Availability Error: " + code);
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                mCameraSource.release();
                mCameraSource = null;
                throw new BarcodeException("Unable to start camera source.", e);
            }
        }
    }

//    /**
//     * onTap returns the tapped barcode result to the calling Activity.
//     *
//     * @param rawX - the raw position of the tap
//     * @param rawY - the raw position of the tap.
//     * @return true if the activity is ending.
//     */
//    private boolean onTap(float rawX, float rawY) {
//        // Find tap point in preview frame coordinates.
//        int[] location = new int[2];
//        mGraphicOverlay.getLocationOnScreen(location);
//        float x = (rawX - location[0]) / mGraphicOverlay.getWidthScaleFactor();
//        float y = (rawY - location[1]) / mGraphicOverlay.getHeightScaleFactor();
//
//        // Find the barcode whose center is closest to the tapped point.
//        Barcode best = null;
//        float bestDistance = Float.MAX_VALUE;
//        for (BarcodeGraphic graphic : mGraphicOverlay.getGraphics()) {
//            Barcode barcode = graphic.getBarcode();
//            if (barcode.getBoundingBox().contains((int) x, (int) y)) {
//                // Exact hit, no need to keep looking.
//                best = barcode;
//                break;
//            }
//            float dx = x - barcode.getBoundingBox().centerX();
//            float dy = y - barcode.getBoundingBox().centerY();
//            float distance = (dx * dx) + (dy * dy);  // actually squared distance
//            if (distance < bestDistance) {
//                best = barcode;
//                bestDistance = distance;
//            }
//        }
//
//        if (best != null) {
//            Intent data = new Intent();
//            data.putExtra(BARCODE_OBJECT, best);
//            setResult(CommonStatusCodes.SUCCESS, data);
//            finish();
//            return true;
//        }
//        return false;
//    }

//    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {
//        @Override
//        public boolean onSingleTapConfirmed(MotionEvent e) {
//            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
//        }
//    }

//    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
//
//        /**
//         * Responds to scaling events for a gesture in progress.
//         * Reported by pointer motion.
//         *
//         * @param detector The detector reporting the event - use this to
//         *                 retrieve extended info about event state.
//         * @return Whether or not the detector should consider this event
//         * as handled. If an event was not handled, the detector
//         * will continue to accumulate movement until an event is
//         * handled. This can be useful if an application, for example,
//         * only wants to update scaling factors if the change is
//         * greater than 0.01.
//         */
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            return false;
//        }
//
//        /**
//         * Responds to the beginning of a scaling gesture. Reported by
//         * new pointers going down.
//         *
//         * @param detector The detector reporting the event - use this to
//         *                 retrieve extended info about event state.
//         * @return Whether or not the detector should continue recognizing
//         * this gesture. For example, if a gesture is beginning
//         * with a focal point outside of a region where it makes
//         * sense, onScaleBegin() may return false to ignore the
//         * rest of the gesture.
//         */
//        @Override
//        public boolean onScaleBegin(ScaleGestureDetector detector) {
//            return true;
//        }
//
//        /**
//         * Responds to the end of a scale gesture. Reported by existing
//         * pointers going up.
//         * <p/>
//         * Once a scale has ended, {@link ScaleGestureDetector#getFocusX()}
//         * and {@link ScaleGestureDetector#getFocusY()} will return focal point
//         * of the pointers remaining on the screen.
//         *
//         * @param detector The detector reporting the event - use this to
//         *                 retrieve extended info about event state.
//         */
//        @Override
//        public void onScaleEnd(ScaleGestureDetector detector) {
//            mCameraSource.doZoom(detector.getScaleFactor());
//        }
//    }

    @Override
    public void onBarcodeDetected(Barcode barcode) {
        Intent data = new Intent();
        data.putExtra(BARCODE_OBJECT, barcode);
        setResult(CommonStatusCodes.SUCCESS, data);
        finish();
    }
}
