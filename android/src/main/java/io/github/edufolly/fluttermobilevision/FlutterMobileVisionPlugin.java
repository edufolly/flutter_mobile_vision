package io.github.edufolly.fluttermobilevision;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * FlutterMobileVisionPlugin
 */
public class FlutterMobileVisionPlugin implements MethodCallHandler, PluginRegistry.ActivityResultListener {


    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_mobile_vision");
        FlutterMobileVisionPlugin plugin = new FlutterMobileVisionPlugin(registrar.activity());
        channel.setMethodCallHandler(plugin);
        registrar.addActivityResultListener(plugin);
    }

    private static final int RC_INVOICE_CAPTURE = 9010;

    private Activity activity;
    private Result result;


    public FlutterMobileVisionPlugin(Activity activity) {
        this.activity = activity;
    }

    private String getTag() {
        return "PrincipalActivity";
    }


    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (call.method.equals("scan")) {
            this.result = result;
            Intent intent = new Intent(activity, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AUTO_FOCUS, true);
            intent.putExtra(BarcodeCaptureActivity.USE_FLASH, false);
            intent.putExtra(BarcodeCaptureActivity.FORMATS, Barcode.QR_CODE);
            activity.startActivityForResult(intent, RC_INVOICE_CAPTURE);
        } else {
            result.notImplemented();
        }
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RC_INVOICE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (intent != null) {
                    Barcode barcode = intent.getParcelableExtra(BarcodeCaptureActivity.BARCODE_OBJECT);

                    Log.d(getTag(), "Barcode read: " + barcode.displayValue);

                    result.success(barcode.displayValue);
                } else {
                    Log.e(getTag(), "No barcode captured, intent data is null");
                    result.error("No barcode captured, intent data is null", null, null);
                }
                return true;
            }
        }
        return false;
    }
}
