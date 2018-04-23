package io.github.edufolly.fluttermobilevision;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * FlutterMobileVisionPlugin
 */
public class FlutterMobileVisionPlugin implements MethodCallHandler,
        PluginRegistry.ActivityResultListener, PluginRegistry.RequestPermissionsResultListener {

    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private static final int RC_BARCODE_SCAN = 9010;

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


    private final Activity activity;
    private Result result;
    private boolean useFlash = false;
    private boolean autoFocus = true;
    private int formats = Barcode.ALL_FORMATS;


    private FlutterMobileVisionPlugin(Activity activity) {
        this.activity = activity;
    }

    private String getTag() {
        return "PrincipalActivity";
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {

        final Map<String, Object> arguments = call.arguments();

        if (call.method.equals("scan")) {
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

            int rc = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
            if (rc != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new
                        String[]{Manifest.permission.CAMERA}, RC_HANDLE_CAMERA_PERM);
            } else {
                scanBarcode();
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
        activity.startActivityForResult(intent, RC_BARCODE_SCAN);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RC_BARCODE_SCAN) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (intent != null) {
                    Barcode barcode = intent
                            .getParcelableExtra(BarcodeCaptureActivity.BARCODE_OBJECT);

                    Log.d(getTag(), "Barcode read: " + barcode.displayValue);

                    Map<String, Object> ret = new HashMap<>();

                    ret.put("displayValue", barcode.displayValue);
                    ret.put("rawValue", barcode.rawValue);
                    ret.put("valueFormat", barcode.valueFormat);
                    ret.put("format", barcode.format);

                    result.success(ret);
                } else {
                    result.error("No barcode captured, intent data is null", null, null);
                }
                return true;
            } else if (resultCode == CommonStatusCodes.ERROR) {
                Exception e = intent.getParcelableExtra(BarcodeCaptureActivity.ERROR);
                result.error(e.getMessage(), null, e);
            }
        }
        return false;
    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions,
                                              int[] grantResults) {

        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(getTag(), "Got unexpected permission result: " + requestCode);
            return false;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            scanBarcode();
            return true;
        }

        Log.e(getTag(), "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        return false;
    }
}
