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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;

import io.github.edufolly.fluttermobilevision.R;
import io.github.edufolly.fluttermobilevision.ui.CameraSource;
import io.github.edufolly.fluttermobilevision.ui.CameraSourcePreview;
import io.github.edufolly.fluttermobilevision.ui.GraphicOverlay;
import io.github.edufolly.fluttermobilevision.util.MobileVisionException;

public final class BarcodeCaptureActivity extends Activity
        implements BarcodeGraphicTracker.BarcodeUpdateListener {

    public static final String AUTO_FOCUS = "AUTO_FOCUS";
    public static final String USE_FLASH = "USE_FLASH";
    public static final String FORMATS = "FORMATS";
    public static final String MULTIPLE = "MULTIPLE";
    public static final String WAIT_TAP = "WAIT_TAP";
    public static final String SHOW_TEXT = "SHOW_TEXT";

    public static final String BARCODE_OBJECT = "Barcode";
    public static final String ERROR = "Error";

    private CameraSource cameraSource;
    private CameraSourcePreview preview;
    private GraphicOverlay<BarcodeGraphic> graphicOverlay;

    private GestureDetector gestureDetector;

    private boolean autoFocus;
    private boolean useFlash;
    private boolean multiple;
    private boolean waitTap;
    private boolean showText;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.capture);

            preview = findViewById(R.id.preview);
            graphicOverlay = findViewById(R.id.graphic_overlay);

            autoFocus = getIntent().getBooleanExtra(AUTO_FOCUS, false);
            useFlash = getIntent().getBooleanExtra(USE_FLASH, false);
            multiple = getIntent().getBooleanExtra(MULTIPLE, false);
            waitTap = getIntent().getBooleanExtra(WAIT_TAP, false);
            showText = getIntent().getBooleanExtra(SHOW_TEXT, false);

            int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (rc == PackageManager.PERMISSION_GRANTED) {
                createCameraSource();
            } else {
                throw new MobileVisionException("Camera permission is needed.");
            }

            gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
                }
            });
        } catch (Exception e) {
            onError(e);
        }
    }

    @SuppressLint("InlinedApi")
    private void createCameraSource() throws MobileVisionException {
        Context context = getApplicationContext();

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(getIntent().getIntExtra(FORMATS, Barcode.ALL_FORMATS))
                .build();

        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(graphicOverlay,
                this, showText);

        barcodeDetector.setProcessor(
                new MultiProcessor.Builder<>(barcodeFactory).build());

        if (!barcodeDetector.isOperational()) {
            IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;

            if (hasLowStorage) {
                throw new MobileVisionException("Low Storage.");
            }
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        cameraSource = new CameraSource
                .Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(metrics.heightPixels, metrics.widthPixels)
                .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .setRequestedFps(15.0f)
                .build();
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
        if (preview != null) {
            preview.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (preview != null) {
            preview.release();
        }
    }

    @SuppressLint("MissingPermission")
    private void startCameraSource() throws SecurityException, MobileVisionException {

        int code = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(getApplicationContext());

        if (code != ConnectionResult.SUCCESS) {
            throw new MobileVisionException("Google Api Availability Error: " + code);
        }

        if (cameraSource != null) {
            try {
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                cameraSource.release();
                cameraSource = null;
                throw new MobileVisionException("Unable to start camera source.", e);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return gestureDetector.onTouchEvent(e)
                || super.onTouchEvent(e);
    }

    private boolean onTap(float rawX, float rawY) {
        if (!waitTap) {
            return false;
        }

        ArrayList<Barcode> list = new ArrayList<>();

        if (multiple) {
            for (BarcodeGraphic graphic : graphicOverlay.getGraphics()) {
                list.add(graphic.getBarcode());
            }
        } else {
            BarcodeGraphic graphic = graphicOverlay.getBest(rawX, rawY);
            if (graphic != null && graphic.getBarcode() != null) {
                list.add(graphic.getBarcode());
            }
        }

        if (!list.isEmpty()) {
            success(list);
            return true;
        }

        return false;
    }

    @Override
    public void onBarcodeDetected(final Barcode barcode) {
        if (!waitTap) {
            ArrayList<Barcode> list = new ArrayList<>(1);
            list.add(barcode);
            success(list);
        }
    }

    private void success(ArrayList<Barcode> list) {
        Intent data = new Intent();
        data.putExtra(BARCODE_OBJECT, list);
        setResult(CommonStatusCodes.SUCCESS, data);
        finish();
    }
}
