import 'dart:async';

import 'package:flutter/services.dart';

class FlutterMobileVision {
  static const MethodChannel _channel =
      const MethodChannel('flutter_mobile_vision');

  static Future<Barcode> scan({
    bool flash: false,
    bool autoFocus: true,
    int formats: Barcode.ALL_FORMATS,
  }) async {
    Map<String, dynamic> arguments = {
      'flash': flash,
      'autoFocus': autoFocus,
      'formats': formats,
    };

    final Map<dynamic, dynamic> map =
        await _channel.invokeMethod('scan', arguments);

    return Barcode.fromMap(map);
  }
}

class Barcode {
  static const int ALL_FORMATS = 0;
  static const int CODE_128 = 1;
  static const int CODE_39 = 2;
  static const int CODE_93 = 4;
  static const int CODABAR = 8;
  static const int DATA_MATRIX = 16;
  static const int EAN_13 = 32;
  static const int EAN_8 = 64;
  static const int ITF = 128;
  static const int QR_CODE = 256;
  static const int UPC_A = 512;
  static const int UPC_E = 1024;
  static const int PDF417 = 2048;
  static const int AZTEC = 4096;
  static const int CONTACT_INFO = 1;
  static const int EMAIL = 2;
  static const int ISBN = 3;
  static const int PHONE = 4;
  static const int PRODUCT = 5;
  static const int SMS = 6;
  static const int TEXT = 7;
  static const int URL = 8;
  static const int WIFI = 9;
  static const int GEO = 10;
  static const int CALENDAR_EVENT = 11;
  static const int DRIVER_LICENSE = 12;

  final String displayValue;
  final String rawValue;
  final int valueFormat;
  final int format;

  Barcode.fromMap(Map map)
      : displayValue = map['displayValue'],
        rawValue = map['rawValue'],
        valueFormat = map['valueFormat'],
        format = map['format'];

  Map<String, dynamic> toMap() {
    return {
      'displayValue': displayValue,
      'rawValue': rawValue,
      'valueFormat': valueFormat,
      'format': format,
    };
  }

  String getFormatString() {
    switch (format) {
      case CODE_128:
        return "CODE 128";
      case CODE_39:
        return 'CODE 39';
      case CODE_93:
        return 'CODE 93';
      case CODABAR:
        return 'CODABAR';
      case DATA_MATRIX:
        return 'DATA MATRIX';
      case EAN_13:
        return 'EAN 13';
      case EAN_8:
        return 'EAN 8';
      case ITF:
        return 'ITF';
      case QR_CODE:
        return 'QR CODE';
      case UPC_A:
        return 'UPC A';
      case UPC_E:
        return 'UPC E';
      case PDF417:
        return 'PDF417';
      case AZTEC:
        return 'AZTEC';
      default:
        return 'UNKNOWN';
    }
  }

  String getValueFormatString() {
    switch (valueFormat) {
      case CONTACT_INFO:
        return 'CONTACT INFO';
      case EMAIL:
        return 'EMAIL';
      case ISBN:
        return 'ISBN';
      case PHONE:
        return 'PHONE';
      case PRODUCT:
        return 'PRODUCT';
      case SMS:
        return 'SMS';
      case TEXT:
        return 'TEXT';
      case URL:
        return 'URL';
      case WIFI:
        return 'WIFI';
      case GEO:
        return 'GEO';
      case CALENDAR_EVENT:
        return 'CALENDAR EVENT';
      case DRIVER_LICENSE:
        return 'DRIVER LICENSE';
      default:
        return 'UNKNOWN';
    }
  }
}
