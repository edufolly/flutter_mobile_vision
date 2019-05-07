package io.github.edufolly.fluttermobilevision;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener;

import io.github.edufolly.fluttermobilevision.barcode.BarcodeCaptureActivity;
import io.github.edufolly.fluttermobilevision.face.FaceCaptureActivity;
import io.github.edufolly.fluttermobilevision.face.MyFace;
import io.github.edufolly.fluttermobilevision.ocr.MyTextBlock;
import io.github.edufolly.fluttermobilevision.ocr.OcrCaptureActivity;
import io.github.edufolly.fluttermobilevision.ui.CameraSource;
import io.github.edufolly.fluttermobilevision.util.AbstractCaptureActivity;

/**
 * FlutterMobileVisionPlugin
 */
public class FlutterMobileVisionPlugin implements MethodCallHandler,
        PluginRegistry.ActivityResultListener, RequestPermissionsResultListener {

    private static final int REQUEST_CAMERA_PERMISSIONS = 2;
    private static final int RC_BARCODE_SCAN = 9010;
    private static final int RC_OCR_READ = 8020;
    private static final int RC_FACE_DETECT = 7030;

    private final Registrar registrar;
    private Result pendingResult;

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

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(),
                "flutter_mobile_vision");

        FlutterMobileVisionPlugin instance = new FlutterMobileVisionPlugin(registrar);

        channel.setMethodCallHandler(instance);

        registrar.addActivityResultListener(instance);

        registrar.addRequestPermissionsResultListener(instance);
    }

    /**
     * @param registrar Registrar
     */
    FlutterMobileVisionPlugin(Registrar registrar) {
        this.registrar = registrar;
    }

    /**
     * @param call   Call
     * @param result Result
     */
    @Override
    public void onMethodCall(MethodCall call, Result result) {

        Map<String, Object> arguments = new HashMap<>();

        if (call.arguments() != null) {
            arguments = call.arguments();
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

        Intent intent;
        int res;

        this.pendingResult = result;

        switch (call.method) {
            case "start":
                if (ContextCompat.checkSelfPermission(registrar.activity(),
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(registrar.activity(),
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_CAMERA_PERMISSIONS);

                    return;
                }
                start();
                return;

            case "scan":
                intent = new Intent(registrar.activity(), BarcodeCaptureActivity.class);
                res = RC_BARCODE_SCAN;
                break;

            case "read":
                intent = new Intent(registrar.activity(), OcrCaptureActivity.class);
                res = RC_OCR_READ;
                break;

            case "face":
                intent = new Intent(registrar.activity(), FaceCaptureActivity.class);
                res = RC_FACE_DETECT;
                break;

            default:
                result.notImplemented();
                return;
        }

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
        registrar.activity().startActivityForResult(intent, res);
    }

    /**
     *
     */
    private void start() {
        @SuppressLint("UseSparseArrays")
        Map<Integer, List> map = new HashMap<>();

        int[] cameras = new int[]{CameraSource.CAMERA_FACING_BACK,
                CameraSource.CAMERA_FACING_FRONT};

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

        pendingResult.success(map);
    }

    /**
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param intent      intent
     * @return boolean
     */
    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RC_BARCODE_SCAN) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (intent != null) {
                    ArrayList<Barcode> barcodes = intent
                            .getParcelableArrayListExtra(BarcodeCaptureActivity.OBJECT);
                    if (!barcodes.isEmpty()) {
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
                        pendingResult.success(list);
                        return true;
                    }
                }
                pendingResult.error("No barcode captured, intent data is null", null, null);
            } else if (resultCode == CommonStatusCodes.ERROR) {
                if (intent != null) {
                    Exception e = intent.getParcelableExtra(BarcodeCaptureActivity.ERROR);
                    pendingResult.error(e.getMessage(), null, null);
                } else {
                    pendingResult.error("Intent is null (the camera permission may not be granted)", null, null);
                }
            }
        } else if (requestCode == RC_OCR_READ) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (intent != null) {
                    ArrayList<MyTextBlock> blocks = intent
                            .getParcelableArrayListExtra(OcrCaptureActivity.OBJECT);
                    if (!blocks.isEmpty()) {
                        List<Map<String, Object>> list = new ArrayList<>();
                        for (MyTextBlock block : blocks) {
                            list.add(block.getMap());
                        }
                        pendingResult.success(list);
                        return true;
                    }
                }
                pendingResult.error("No text recognized, intent data is null", null, null);
            } else if (resultCode == CommonStatusCodes.ERROR) {
                if (intent != null) {
                    Exception e = intent.getParcelableExtra(OcrCaptureActivity.ERROR);
                    pendingResult.error(e.getMessage(), null, null);
                } else {
                    pendingResult.error("Intent is null (the camera permission may not be granted)", null, null);
                }
            }
        } else if (requestCode == RC_FACE_DETECT) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (intent != null) {
                    ArrayList<MyFace> faces = intent
                            .getParcelableArrayListExtra(FaceCaptureActivity.OBJECT);
                    if (!faces.isEmpty()) {
                        List<Map<String, Object>> list = new ArrayList<>();
                        for (MyFace face : faces) {
                            list.add(face.getMap());
                        }
                        pendingResult.success(list);
                        return true;
                    }
                }
                pendingResult.error("No face detected, intent data is null", null, null);
            } else if (resultCode == CommonStatusCodes.ERROR) {
                if (intent != null) {
                    Exception e = intent.getParcelableExtra(OcrCaptureActivity.ERROR);
                    pendingResult.error(e.getMessage(), null, null);
                } else {
                    pendingResult.error("Intent is null (the camera permission may not be granted)", null, null);
                }
            }
        }
        return false;
    }

    /**
     * @param requestCode  requestCode
     * @param permissions  permissions
     * @param grantResults grantResults
     * @return boolean
     */
    @Override
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions,
                                              int[] grantResults) {

        if (requestCode == REQUEST_CAMERA_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                start();
            } else {
                pendingResult.error("no_permissions",
                        "this plugin requires camera permissions for scanning", null);
            }
            return true;
        }
        return false;
    }

}
