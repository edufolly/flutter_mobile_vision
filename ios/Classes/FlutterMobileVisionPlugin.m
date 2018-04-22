#import "FlutterMobileVisionPlugin.h"

@implementation FlutterMobileVisionPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"flutter_mobile_vision"
            binaryMessenger:[registrar messenger]];
  FlutterMobileVisionPlugin* instance = [[FlutterMobileVisionPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    result(FlutterMethodNotImplemented);
}

@end
