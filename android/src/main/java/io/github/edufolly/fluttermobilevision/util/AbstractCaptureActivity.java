package io.github.edufolly.fluttermobilevision.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;

import java.io.IOException;

import io.github.edufolly.fluttermobilevision.R;
import io.github.edufolly.fluttermobilevision.ui.CameraSource;
import io.github.edufolly.fluttermobilevision.ui.CameraSourcePreview;
import io.github.edufolly.fluttermobilevision.ui.GraphicOverlay;

public abstract class AbstractCaptureActivity<T extends GraphicOverlay.Graphic>
        extends Activity {

    public static final String AUTO_FOCUS = "AUTO_FOCUS";
    public static final String USE_FLASH = "USE_FLASH";
    public static final String FORMATS = "FORMATS";
    public static final String MULTIPLE = "MULTIPLE";
    public static final String WAIT_TAP = "WAIT_TAP";
    public static final String SHOW_TEXT = "SHOW_TEXT";
    public static final String PREVIEW_WIDTH = "PREVIEW_WIDTH";
    public static final String PREVIEW_HEIGHT = "PREVIEW_HEIGHT";
    public static final String CAMERA = "CAMERA";
    public static final String FPS = "FPS";

    public static final String OBJECT = "Object";
    public static final String ERROR = "Error";

    protected CameraSource cameraSource;
    protected CameraSourcePreview preview;
    protected GraphicOverlay<T> graphicOverlay;

    protected GestureDetector gestureDetector;

    protected boolean autoFocus;
    protected boolean useFlash;
    protected boolean multiple;
    protected boolean waitTap;
    protected boolean showText;
    protected int previewWidth;
    protected int previewHeight;
    protected int camera;
    protected float fps;

    @Override
    protected void onCreate(Bundle icicle) {
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
            previewWidth = getIntent().getIntExtra(PREVIEW_WIDTH, CameraSource.PREVIEW_WIDTH);
            previewHeight = getIntent().getIntExtra(PREVIEW_HEIGHT, CameraSource.PREVIEW_HEIGHT);
            camera = getIntent().getIntExtra(CAMERA, CameraSource.CAMERA_FACING_BACK);
            fps = getIntent().getFloatExtra(FPS, 15.0f);

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
    protected abstract void createCameraSource() throws MobileVisionException;

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

    protected abstract boolean onTap(float rawX, float rawY);
}
