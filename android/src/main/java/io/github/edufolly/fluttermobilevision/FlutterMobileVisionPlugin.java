package io.github.edufolly.fluttermobilevision;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;


/**
 * FlutterMobileVisionPlugin
 */
public class FlutterMobileVisionPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {

    private static final String CHANNEL = "flutter_mobile_vision";

    private MethodChannel channel;
    private FlutterMobileVisionDelegate delegate;
    private FlutterPluginBinding pluginBinding;
    private ActivityPluginBinding activityBinding;
    private Activity activity;

    @SuppressWarnings("unused")
    public static void registerWith(Registrar registrar) {
        Activity activity = registrar.activity();
        FlutterMobileVisionPlugin plugin = new FlutterMobileVisionPlugin();
        plugin.setup(registrar.messenger(), activity, registrar, null);
    }


    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        pluginBinding = flutterPluginBinding;
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        pluginBinding = null;
    }


    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activityBinding = binding;
        setup(pluginBinding.getBinaryMessenger(),
                activityBinding.getActivity(),
                null,
                activityBinding);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity();
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        onAttachedToActivity(binding);
    }

    @Override
    public void onDetachedFromActivity() {
        tearDown();
    }

    private void setup(
            final BinaryMessenger messenger,

            final Activity activity,
            final PluginRegistry.Registrar registrar,
            final ActivityPluginBinding activityBinding) {
        this.activity = activity;
        this.delegate = new FlutterMobileVisionDelegate(activity);
        channel = new MethodChannel(messenger, CHANNEL);
        channel.setMethodCallHandler(this);
        if (registrar != null) {
            // V1 embedding setup for activity listeners.
            registrar.addActivityResultListener(delegate);
            registrar.addRequestPermissionsResultListener(delegate);
        } else {
            // V2 embedding setup for activity listeners.
            activityBinding.addActivityResultListener(delegate);
            activityBinding.addRequestPermissionsResultListener(delegate);
        }
    }

    private void tearDown() {
        activityBinding.removeActivityResultListener(delegate);
        activityBinding.removeRequestPermissionsResultListener(delegate);
        activityBinding = null;
        delegate = null;
        channel.setMethodCallHandler(null);
        channel = null;
    }

    // MethodChannel.Result wrapper that responds on the platform thread.
    private static class MethodResultWrapper implements MethodChannel.Result {
        private MethodChannel.Result methodResult;
        private Handler handler;

        MethodResultWrapper(MethodChannel.Result result) {
            methodResult = result;
            handler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void success(final Object result) {
            handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            methodResult.success(result);
                        }
                    });
        }

        @Override
        public void error(
                final String errorCode, final String errorMessage, final Object errorDetails) {
            handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            methodResult.error(errorCode, errorMessage, errorDetails);
                        }
                    });
        }

        @Override
        public void notImplemented() {
            handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            methodResult.notImplemented();
                        }
                    });
        }
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result rawResult) {
        if (activity == null) {
            rawResult.error("no_activity",
                    "Flutter Mobile Vision plugin requires a foreground activity.",
                    null);
            return;
        }

        MethodChannel.Result result = new MethodResultWrapper(rawResult);

        switch (call.method) {
            case "start":
                delegate.start(call, result);
                break;

            case "scan":
                delegate.scan(call, result);
                break;

            case "read":
                delegate.read(call, result);
                break;

            case "face":
                delegate.face(call, result);
                break;

            default:
                result.notImplemented();
        }
    }
}
