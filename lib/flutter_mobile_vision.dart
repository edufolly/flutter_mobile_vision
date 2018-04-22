import 'dart:async';

import 'package:flutter/services.dart';

class FlutterMobileVision {
  static const MethodChannel _channel =
      const MethodChannel('flutter_mobile_vision');

  static final int ALL_FORMATS = 0;
  static final int CODE_128 = 1;
  static final int CODE_39 = 2;
  static final int CODE_93 = 4;
  static final int CODABAR = 8;
  static final int DATA_MATRIX = 16;
  static final int EAN_13 = 32;
  static final int EAN_8 = 64;
  static final int ITF = 128;
  static final int QR_CODE = 256;
  static final int UPC_A = 512;
  static final int UPC_E = 1024;
  static final int PDF417 = 2048;
  static final int AZTEC = 4096;
  static final int CONTACT_INFO = 1;
  static final int EMAIL = 2;
  static final int ISBN = 3;
  static final int PHONE = 4;
  static final int PRODUCT = 5;
  static final int SMS = 6;
  static final int TEXT = 7;
  static final int URL = 8;
  static final int WIFI = 9;
  static final int GEO = 10;
  static final int CALENDAR_EVENT = 11;
  static final int DRIVER_LICENSE = 12;

  static Future<String> scan({bool flash: false}) async {
    Map<String, dynamic> arguments = {
      'flash': flash,
    };
    final String barcode = await _channel.invokeMethod('scan', arguments);
    return barcode;
  }
}
