import 'dart:async';

import 'package:flutter/services.dart';

class FlutterMobileVision {
  static const MethodChannel _channel =
      const MethodChannel('flutter_mobile_vision');

  static Future<String> scan() async {
    final String barcode = await _channel.invokeMethod('scan');
    return barcode;
  }
}
