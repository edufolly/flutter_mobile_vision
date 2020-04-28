package io.github.edufolly.fluttermobilevision;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.barcode.Barcode;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry.ActivityResultListener;
import io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener;
import io.github.edufolly.fluttermobilevision.barcode.BarcodeCaptureActivity;
import io.github.edufolly.fluttermobilevision.face.FaceCaptureActivity;
import io.github.edufolly.fluttermobilevision.face.MyFace;
import io.github.edufolly.fluttermobilevision.ocr.MyTextBlock;
import io.github.edufolly.fluttermobilevision.ocr.OcrCaptureActivity;
import io.github.edufolly.fluttermobilevision.ui.CameraSource;
import io.github.edufolly.fluttermobilevision.util.AbstractCaptureActivity;

public class FlutterMobileVisionDelegate
        implements ActivityResultListener, RequestPermissionsResultListener {

    private static final int RC_BARCODE_SCAN = 9010;
    private static final int RC_OCR_READ = 8020;
    private static final int RC_FACE_DETECT = 7030;
    private static final int RC_START = 6040;

    private static final int REQUEST_CAMERA_PERMISSION = 2345;

    private int callerId;
    private MethodChannel.Result pendingResult;
    private MethodCall methodCall;

    private boolean useFlash = false;
    private boolean autoFocus = true;
    private int formats = Barcode.ALL_FORMATS;
    private boolean multiple = false;
    private boolean waitTap = false;
    private boolean showText = false;
    private int previewWidth = 640;
    private int previewHeight = 480;
    private int camera = CameraSource.CAMERA_FACING_BACK;
    private float fps = 15.0f;


    private final Activity activity;


    FlutterMobileVisionDelegate(final Activity activity) {
        this.activity = activity;


    }

    private boolean isCameraPermissionGranted() {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void askForCameraPermission() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA},
                REQUEST_CAMERA_PERMISSION);
    }

    private boolean needRequestCameraPermission() {
        return FlutterMobileVisionUtils.needRequestCameraPermission(activity);
    }

    void start(MethodCall methodCall, MethodChannel.Result result) {
        if (prepare(methodCall, result, RC_START)) {
            launchStart();
        }
    }

    private void launchStart() {
        Map<Integer, List> map = new HashMap<>();

        int[] cameras = new int[]{
                CameraSource.CAMERA_FACING_BACK,
                CameraSource.CAMERA_FACING_FRONT
        };

        for (int facing : cameras) {

            List<Size> sizeList = CameraSource.getSizesForCameraFacing(facing);

            List<Map<String, Object>> list = new ArrayList<>();
            for (Size size : sizeList) {
                Map<String, Object> ret = new HashMap<>();
                ret.put("width", size.getWidth());
                ret.put("height", size.getHeight());
                list.add(ret);
            }

            map.put(facing, list);
        }

        finishWithSuccess(map);
    }

    void scan(MethodCall methodCall, MethodChannel.Result result) {
        if (prepare(methodCall, result, RC_BARCODE_SCAN)) {
            launchIntent();
        }
    }

    void read(MethodCall methodCall, MethodChannel.Result result) {
        if (prepare(methodCall, result, RC_OCR_READ)) {
            launchIntent();
        }
    }

    void face(MethodCall methodCall, MethodChannel.Result result) {
        if (prepare(methodCall, result, RC_FACE_DETECT)) {
            launchIntent();
        }
    }


    private boolean prepare(MethodCall methodCall, MethodChannel.Result result, int callerId) {
        if (!setPendingMethodCallAndResult(methodCall, result, callerId)) {
            finishWithAlreadyActiveError(result);
            return false;
        }

        if (needRequestCameraPermission() && !isCameraPermissionGranted()) {
            askForCameraPermission();
            return false;
        }

        return true;
    }

    private void launchIntent() {

        Map<String, Object> arguments = new HashMap<>();

        if (methodCall.arguments() != null) {
            arguments = methodCall.arguments();
        }

        if (arguments.containsKey("flash")) {
            useFlash = (boolean) arguments.get("flash");
        }

        if (arguments.containsKey("autoFocus")) {
            autoFocus = (boolean) arguments.get("autoFocus");
        }

        if (arguments.containsKey("formats")) {
            formats = (int) arguments.get("formats");
        }

        if (arguments.containsKey("multiple")) {
            multiple = (boolean) arguments.get("multiple");
        }

        if (arguments.containsKey("waitTap")) {
            waitTap = (boolean) arguments.get("waitTap");
        }

        if (multiple) {
            waitTap = true;
        }

        if (arguments.containsKey("showText")) {
            showText = (boolean) arguments.get("showText");
        }

        if (arguments.containsKey("previewWidth")) {
            previewWidth = (int) arguments.get("previewWidth");
        }

        if (arguments.containsKey("previewHeight")) {
            previewHeight = (int) arguments.get("previewHeight");
        }

        if (arguments.containsKey("camera")) {
            camera = (int) arguments.get("camera");
        }

        if (arguments.containsKey("fps")) {
            double tfps = (double) arguments.get("fps");
            fps = (float) tfps;
        }

        Class clazz;

        switch (callerId) {
            case RC_BARCODE_SCAN:
                clazz = BarcodeCaptureActivity.class;
                break;
            case RC_FACE_DETECT:
                clazz = FaceCaptureActivity.class;
                break;
            case RC_OCR_READ:
                clazz = OcrCaptureActivity.class;
                break;
            default:
                finishWithError("method_not_found",
                        "Method not found.");
                return;
        }


        Intent intent = new Intent(activity, clazz);

        intent.putExtra(AbstractCaptureActivity.AUTO_FOCUS, autoFocus);
        intent.putExtra(AbstractCaptureActivity.USE_FLASH, useFlash);
        intent.putExtra(AbstractCaptureActivity.FORMATS, formats);
        intent.putExtra(AbstractCaptureActivity.MULTIPLE, multiple);
        intent.putExtra(AbstractCaptureActivity.WAIT_TAP, waitTap);
        intent.putExtra(AbstractCaptureActivity.SHOW_TEXT, showText);
        intent.putExtra(AbstractCaptureActivity.PREVIEW_WIDTH, previewWidth);
        intent.putExtra(AbstractCaptureActivity.PREVIEW_HEIGHT, previewHeight);
        intent.putExtra(AbstractCaptureActivity.CAMERA, camera);
        intent.putExtra(AbstractCaptureActivity.FPS, fps);

        activity.startActivityForResult(intent, callerId);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RC_BARCODE_SCAN) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (intent != null) {
                    ArrayList<Barcode> barcodes = intent
                            .getParcelableArrayListExtra(BarcodeCaptureActivity.OBJECT);
                    if (barcodes != null && !barcodes.isEmpty()) {
                        List<Map<String, Object>> list = new ArrayList<>();
                        for (Barcode barcode : barcodes) {
                            Rect rect = barcode.getBoundingBox();
                            Map<String, Object> ret = new HashMap<>();
                            ret.put("displayValue", barcode.displayValue);
                            ret.put("rawValue", barcode.rawValue);
                            ret.put("valueFormat", barcode.valueFormat);
                            ret.put("format", barcode.format);
                            ret.put("top", rect.top);
                            ret.put("bottom", rect.bottom);
                            ret.put("left", rect.left);
                            ret.put("right", rect.right);
                            list.add(ret);
                        }
                        finishWithSuccess(list);
                        return true;
                    }
                }
                finishWithError("No barcode captured, intent data is null", null);
            } else if (resultCode == CommonStatusCodes.ERROR) {
                if (intent != null) {
                    Exception e = intent.getParcelableExtra(BarcodeCaptureActivity.ERROR);
                    finishWithError(e.getMessage(), null);
                } else {
                    finishWithError("Intent is null (the camera permission may not be granted)", null);
                }
            }
        } else if (requestCode == RC_OCR_READ) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (intent != null) {
                    ArrayList<MyTextBlock> blocks = intent
                            .getParcelableArrayListExtra(OcrCaptureActivity.OBJECT);
                    if (blocks != null && !blocks.isEmpty()) {
                        List<Map<String, Object>> list = new ArrayList<>();
                        for (MyTextBlock block : blocks) {
                            list.add(block.getMap());
                        }
                        finishWithSuccess(list);
                        return true;
                    }
                }
                finishWithError("No text recognized, intent data is null", null);
            } else if (resultCode == CommonStatusCodes.ERROR) {
                if (intent != null) {
                    Exception e = intent.getParcelableExtra(OcrCaptureActivity.ERROR);
                    finishWithError(e.getMessage(), null);
                } else {
                    finishWithError("Intent is null (the camera permission may not be granted)", null);
                }
            }
        } else if (requestCode == RC_FACE_DETECT) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (intent != null) {
                    ArrayList<MyFace> faces = intent
                            .getParcelableArrayListExtra(FaceCaptureActivity.OBJECT);
                    if (faces != null && !faces.isEmpty()) {
                        List<Map<String, Object>> list = new ArrayList<>();
                        for (MyFace face : faces) {
                            list.add(face.getMap());
                        }
                        finishWithSuccess(list);
                        return true;
                    }
                }
                finishWithError("No face detected, intent data is null", null);
            } else if (resultCode == CommonStatusCodes.ERROR) {
                if (intent != null) {
                    Exception e = intent.getParcelableExtra(OcrCaptureActivity.ERROR);
                    finishWithError(e.getMessage(), null);
                } else {
                    finishWithError("Intent is null (the camera permission may not be granted)", null);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean permissionGranted = grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED;

        if (permissionGranted) {
            switch (callerId) {
                case RC_START:
                    launchStart();
                    break;
                case RC_BARCODE_SCAN:
                case RC_OCR_READ:
                case RC_FACE_DETECT:
                    launchIntent();
                    break;
                default:
                    finishWithError("permission_not_found",
                            "Permission not found.");
            }
            return true;
        }

        finishWithError("camera_access_denied", "The user did not allow camera access.");
        return false;
    }

    private boolean setPendingMethodCallAndResult(MethodCall methodCall,
                                                  MethodChannel.Result result, int callerId) {
        if (pendingResult != null) {
            return false;
        }

        this.callerId = callerId;
        this.methodCall = methodCall;
        pendingResult = result;

        return true;
    }

    private void finishWithAlreadyActiveError(MethodChannel.Result result) {
        result.error("already_active",
                "Flutter Mobile Vision is already active.",
                null);
    }

    private void finishWithSuccess(Object object) {
        if (pendingResult == null) {
            // TODO - Return an error.
            return;
        }

        pendingResult.success(object);
        clearMethodCallAndResult();
    }

    private void finishWithError(String errorCode, String errorMessage) {
        if (pendingResult == null) {
            // TODO - Return an error.
            return;
        }

        pendingResult.error(errorCode, errorMessage, null);
        clearMethodCallAndResult();
    }

    private void clearMethodCallAndResult() {
        callerId = 0;
        methodCall = null;
        pendingResult = null;
    }
}
