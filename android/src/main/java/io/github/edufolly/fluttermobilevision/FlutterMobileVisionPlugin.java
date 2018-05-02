package io.github.edufolly.fluttermobilevision;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.github.edufolly.fluttermobilevision.barcode.BarcodeCaptureActivity;
import io.github.edufolly.fluttermobilevision.ocr.MyTextBlock;
import io.github.edufolly.fluttermobilevision.ocr.OcrCaptureActivity;

/**
 * FlutterMobileVisionPlugin
 */
public class FlutterMobileVisionPlugin implements MethodCallHandler,
        PluginRegistry.ActivityResultListener, PluginRegistry.RequestPermissionsResultListener {

    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private static final int RC_BARCODE_SCAN = 9010;
    private static final int RC_OCR_READ = 8020;

    private final Activity activity;
    private Result result;

    private boolean useFlash = false;
    private boolean autoFocus = true;
    private int formats = Barcode.ALL_FORMATS;
    private boolean multiple = false;
    private boolean waitTap = false;

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(),
                "flutter_mobile_vision");

        FlutterMobileVisionPlugin plugin = new FlutterMobileVisionPlugin(registrar.activity());

        channel.setMethodCallHandler(plugin);

        registrar.addActivityResultListener(plugin);
    }

    private FlutterMobileVisionPlugin(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {

        final Map<String, Object> arguments = call.arguments();

        if ("scan".equals(call.method)) {
            this.result = result;

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

            int rc = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
            if (rc != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new
                        String[]{Manifest.permission.CAMERA}, RC_HANDLE_CAMERA_PERM);
            } else {
                scanBarcode();
            }
        } else if ("read".equals(call.method)) {
            this.result = result;

            if (arguments.containsKey("flash")) {
                useFlash = (boolean) arguments.get("flash");
            }

            if (arguments.containsKey("autoFocus")) {
                autoFocus = (boolean) arguments.get("autoFocus");
            }

            if (arguments.containsKey("multiple")) {
                multiple = (boolean) arguments.get("multiple");
            }

            int rc = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
            if (rc != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new
                        String[]{Manifest.permission.CAMERA}, RC_HANDLE_CAMERA_PERM);
            } else {
                ocrRead();
            }

        } else {
            result.notImplemented();
        }
    }

    private void scanBarcode() {
        Intent intent = new Intent(activity, BarcodeCaptureActivity.class);
        intent.putExtra(BarcodeCaptureActivity.AUTO_FOCUS, autoFocus);
        intent.putExtra(BarcodeCaptureActivity.USE_FLASH, useFlash);
        intent.putExtra(BarcodeCaptureActivity.FORMATS, formats);
        intent.putExtra(BarcodeCaptureActivity.MULTIPLE, multiple);
        intent.putExtra(BarcodeCaptureActivity.WAIT_TAP, waitTap);
        activity.startActivityForResult(intent, RC_BARCODE_SCAN);
    }

    private void ocrRead() {
        Intent intent = new Intent(activity, OcrCaptureActivity.class);
        intent.putExtra(OcrCaptureActivity.AUTO_FOCUS, autoFocus);
        intent.putExtra(OcrCaptureActivity.USE_FLASH, useFlash);
        intent.putExtra(OcrCaptureActivity.MULTIPLE, multiple);
        activity.startActivityForResult(intent, RC_OCR_READ);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RC_BARCODE_SCAN) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (intent != null) {
                    ArrayList<Barcode> barcodes = intent
                            .getParcelableArrayListExtra(BarcodeCaptureActivity.BARCODE_OBJECT);
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
                        result.success(list);
                        return true;
                    }
                }
                result.error("No barcode captured, intent data is null", null, null);
            } else if (resultCode == CommonStatusCodes.ERROR) {
                Exception e = intent.getParcelableExtra(BarcodeCaptureActivity.ERROR);
                result.error(e.getMessage(), null, e);
            }
        } else if (requestCode == RC_OCR_READ) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (intent != null) {
                    ArrayList<MyTextBlock> blocks = intent
                            .getParcelableArrayListExtra(OcrCaptureActivity.TEXT_OBJECT);
                    if (!blocks.isEmpty()) {
                        List<Map<String, Object>> list = new ArrayList<>();
                        for (MyTextBlock block : blocks) {
                            list.add(block.getMap());
                        }
                        result.success(list);
                        return true;
                    }
                }
                result.error("No text recognized, intent data is null", null, null);
            } else if (resultCode == CommonStatusCodes.ERROR) {
                Exception e = intent.getParcelableExtra(OcrCaptureActivity.ERROR);
                result.error(e.getMessage(), null, e);
            }
        }
        return false;
    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions,
                                              int[] grantResults) {

        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            result.error("Got unexpected permission result: " + requestCode, null, null);
            return false;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // FIXME: Please!
            // scanBarcode();
            return true;
        }

        result.error("Permission not granted: results len = " + grantResults.length +
                        " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"),
                null, null);

        return false;
    }
}
