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
package io.github.edufolly.fluttermobilevision.ocr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

import io.github.edufolly.fluttermobilevision.R;
import io.github.edufolly.fluttermobilevision.ui.CameraSource;
import io.github.edufolly.fluttermobilevision.ui.CameraSourcePreview;
import io.github.edufolly.fluttermobilevision.ui.GraphicOverlay;
import io.github.edufolly.fluttermobilevision.util.BarcodeException;

public final class OcrCaptureActivity extends Activity {
    public static final String AUTO_FOCUS = "AUTO_FOCUS";
    public static final String USE_FLASH = "USE_FLASH";

    public static final String TEXT_OBJECT = "Text";
    public static final String ERROR = "Error";

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<OcrGraphic> mGraphicOverlay;

    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.capture);

            mPreview = findViewById(R.id.preview);
            mGraphicOverlay = findViewById(R.id.graphic_overlay);

            boolean autoFocus = getIntent().getBooleanExtra(AUTO_FOCUS, false);
            boolean useFlash = getIntent().getBooleanExtra(USE_FLASH, false);

            int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (rc == PackageManager.PERMISSION_GRANTED) {
                createCameraSource(autoFocus, useFlash);
            } else {
                throw new BarcodeException("Camera permission is needed.");
            }

            gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
                }
            });

            scaleGestureDetector = new ScaleGestureDetector(this,
                    new ScaleGestureDetector.OnScaleGestureListener() {
                        @Override
                        public boolean onScale(ScaleGestureDetector detector) {
                            return false;
                        }

                        @Override
                        public boolean onScaleBegin(ScaleGestureDetector detector) {
                            return true;
                        }

                        @Override
                        public void onScaleEnd(ScaleGestureDetector detector) {
                            mCameraSource.doZoom(detector.getScaleFactor());
                        }
                    });
        } catch (Exception e) {
            onError(e);
        }
    }

    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) throws BarcodeException {
        Context context = getApplicationContext();

        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        textRecognizer.setProcessor(new OcrDetectorProcessor(mGraphicOverlay));

        if (!textRecognizer.isOperational()) {
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                throw new BarcodeException("Low Storage.");
            }
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        mCameraSource =
                new CameraSource.Builder(getApplicationContext(), textRecognizer)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(metrics.heightPixels, metrics.widthPixels)
                        .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                        .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                        .setRequestedFps(2.0f)
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

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return scaleGestureDetector.onTouchEvent(e)
                || gestureDetector.onTouchEvent(e)
                || super.onTouchEvent(e);
    }

    private boolean onTap(float rawX, float rawY) {
        // FIXME : Problem here!
        OcrGraphic graphic = mGraphicOverlay.getGraphicAtLocation(rawX, rawY);
        TextBlock text;
        if (graphic != null) {
            text = graphic.getTextBlock();
            if (text != null && text.getValue() != null) {
                Intent data = new Intent();
                data.putExtra(TEXT_OBJECT, text.getValue());
                setResult(CommonStatusCodes.SUCCESS, data);
                finish();
                return true;
            } else {
                Log.d("OCR", "text data is null");
                return false;
            }
        } else {
            Log.d("OCR", "no text detected");
            return false;
        }
    }
}