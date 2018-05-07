package io.github.edufolly.fluttermobilevision.barcode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.util.DisplayMetrics;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.ArrayList;

import io.github.edufolly.fluttermobilevision.ui.CameraSource;
import io.github.edufolly.fluttermobilevision.util.AbstractCaptureActivity;
import io.github.edufolly.fluttermobilevision.util.MobileVisionException;

public final class BarcodeCaptureActivity extends AbstractCaptureActivity<BarcodeGraphic>
        implements BarcodeUpdateListener {

    @SuppressLint("InlinedApi")
    protected void createCameraSource() throws MobileVisionException {
        Context context = getApplicationContext();

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(getIntent().getIntExtra(FORMATS, Barcode.ALL_FORMATS))
                .build();

        BarcodeTrackerFactory barcodeTrackerFactory = new BarcodeTrackerFactory(graphicOverlay,
                this, showText);

        barcodeDetector.setProcessor(
                new MultiProcessor.Builder<>(barcodeTrackerFactory).build());

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
                .setFacing(camera)
                .setRequestedPreviewSize(metrics.heightPixels, metrics.widthPixels)
                .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .setRequestedFps(fps)
                .build();
    }

    protected boolean onTap(float rawX, float rawY) {
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
        data.putExtra(OBJECT, list);
        setResult(CommonStatusCodes.SUCCESS, data);
        finish();
    }
}
