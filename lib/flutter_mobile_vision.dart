import 'dart:async';

import 'package:flutter/services.dart';

class FlutterMobileVision {
  static const MethodChannel _channel = MethodChannel('flutter_mobile_vision');

  static const int CAMERA_BACK = 0;
  static const int CAMERA_FRONT = 1;

  static const Size PREVIEW = Size(640, 480);
  static const Size SCAN_AREA = Size(0, 0);

  static final Map<int, List<Size>> _previewSizes = {};

  ///
  ///
  ///
  static Future<Map<int, List<Size>>> start() async {
    var value = await _channel.invokeMethod('start');
    value.forEach((k, v) {
      List<Size> list = [];
      v.forEach((map) => list.add(Size.fromMap(map)));
      _previewSizes[k] = list;
    });
    return _previewSizes;
  }

  ///
  ///
  ///
  static List<Size> getPreviewSizes(int facing) {
    if (_previewSizes.containsKey(facing)) {
      return _previewSizes[facing];
    }
    return null;
  }

  ///
  ///
  ///
  static Future<List<Barcode>> scan({
    bool flash = false,
    bool autoFocus = true,
    int formats = Barcode.ALL_FORMATS,
    bool multiple = false,
    bool waitTap = false,
    bool showText = false,
    Size preview = PREVIEW,
    Size scanArea = SCAN_AREA,
    int camera = CAMERA_BACK,
    double fps = 15.0,
  }) async {
    if (multiple) {
      waitTap = true;
    }
    Map<String, dynamic> arguments = {
      'flash': flash,
      'autoFocus': autoFocus,
      'formats': formats,
      'multiple': multiple,
      'waitTap': waitTap,
      'showText': showText,
      'previewWidth': preview != null ? preview.width : PREVIEW.width,
      'previewHeight': preview != null ? preview.height : PREVIEW.height,
      'scanAreaWidth': scanArea != null ? scanArea.width : SCAN_AREA.width,
      'scanAreaHeight': scanArea != null ? scanArea.height : SCAN_AREA.height,
      'camera': camera,
      'fps': fps,
    };

    final List list = await _channel.invokeMethod('scan', arguments);

    return list.map((map) => Barcode.fromMap(map)).toList();
  }

  ///
  ///
  ///
  static Future<List<OcrText>> read({
    bool flash = false,
    bool autoFocus = true,
    bool multiple = false,
    bool waitTap = false,
    bool showText = true,
    Size preview = PREVIEW,
    Size scanArea = SCAN_AREA,
    int camera = CAMERA_BACK,
    double fps = 2.0,
  }) async {
    Map<String, dynamic> arguments = {
      'flash': flash,
      'autoFocus': autoFocus,
      'multiple': multiple,
      'waitTap': waitTap,
      'showText': showText,
      'previewWidth': preview != null ? preview.width : PREVIEW.width,
      'previewHeight': preview != null ? preview.height : PREVIEW.height,
      'scanAreaWidth': scanArea != null ? scanArea.width : SCAN_AREA.width,
      'scanAreaHeight': scanArea != null ? scanArea.height : SCAN_AREA.height,
      'camera': camera,
      'fps': fps,
    };

    final List list = await _channel.invokeMethod('read', arguments);

    return list.map((map) => OcrText.fromMap(map)).toList();
  }

  ///
  ///
  ///
  static Future<List<Face>> face({
    bool flash = false,
    bool autoFocus = true,
    bool multiple = true,
    bool showText = true,
    Size preview = PREVIEW,
    Size scanArea = SCAN_AREA,
    int camera = CAMERA_BACK,
    double fps = 15.0,
  }) async {
    Map<String, dynamic> arguments = {
      'flash': flash,
      'autoFocus': autoFocus,
      'multiple': multiple,
      'showText': showText,
      'previewWidth': preview != null ? preview.width : PREVIEW.width,
      'previewHeight': preview != null ? preview.height : PREVIEW.height,
      'scanAreaWidth': scanArea != null ? scanArea.width : SCAN_AREA.width,
      'scanAreaHeight': scanArea != null ? scanArea.height : SCAN_AREA.height,
      'camera': camera,
      'fps': fps,
    };

    final List list = await _channel.invokeMethod('face', arguments);

    return list.map((map) => Face.fromMap(map)).toList();
  }
}

///
///
///
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

  static const Map mapFormat = {
    ALL_FORMATS: 'ALL FORMATS',
    CODE_128: 'CODE 128',
    CODE_39: 'CODE 39',
    CODE_93: 'CODE 93',
    CODABAR: 'CODABAR',
    DATA_MATRIX: 'DATA MATRIX',
    EAN_13: 'EAN 13',
    EAN_8: 'EAN 8',
    ITF: 'ITF',
    QR_CODE: 'QR CODE',
    UPC_A: 'UPC A',
    UPC_E: 'UPC E',
    PDF417: 'PDF417',
    AZTEC: 'AZTEC',
  };

  static const Map mapValueFormat = {
    CONTACT_INFO: 'CONTACT INFO',
    EMAIL: 'EMAIL',
    ISBN: 'ISBN',
    PHONE: 'PHONE',
    PRODUCT: 'PRODUCT',
    SMS: 'SMS',
    TEXT: 'TEXT',
    URL: 'URL',
    WIFI: 'WIFI',
    GEO: 'GEO',
    CALENDAR_EVENT: 'CALENDAR EVENT',
    DRIVER_LICENSE: 'DRIVER LICENSE',
  };

  final String displayValue;
  final String rawValue;
  final int format;
  final int valueFormat;
  final int top;
  final int bottom;
  final int left;
  final int right;

  Barcode(
    this.displayValue, {
    this.rawValue = '',
    this.format = 99,
    this.valueFormat = 99,
    this.top = -1,
    this.bottom = -1,
    this.left = -1,
    this.right = -1,
  });

  Barcode.fromMap(Map map)
      : displayValue = map['displayValue'],
        rawValue = map['rawValue'],
        format = map['format'],
        valueFormat = map['valueFormat'],
        top = map['top'],
        bottom = map['bottom'],
        left = map['left'],
        right = map['right'];

  Map<String, dynamic> toMap() {
    return {
      'displayValue': displayValue,
      'rawValue': rawValue,
      'format': format,
      'valueFormat': valueFormat,
      'top': top,
      'bottom': bottom,
      'left': left,
      'right': right,
    };
  }

  String getFormatString() {
    return mapFormat[format] ?? 'UNKNOWN';
  }

  String getValueFormatString() {
    return mapValueFormat[valueFormat] ?? 'UNKNOWN';
  }
}

///
///
///
class OcrText {
  final String value;
  final String language;
  final int top;
  final int bottom;
  final int left;
  final int right;

  OcrText(
    this.value, {
    this.language = '',
    this.top = -1,
    this.bottom = -1,
    this.left = -1,
    this.right = -1,
  });

  OcrText.fromMap(Map map)
      : value = map['value'],
        language = map['language'],
        top = map['top'],
        bottom = map['bottom'],
        left = map['left'],
        right = map['right'];

  Map<String, dynamic> toMap() {
    return {
      'value': value,
      'language': language,
      'top': top,
      'bottom': bottom,
      'left': left,
      'right': right,
    };
  }
}

///
///
///
class Face {
  final int id;
  final double eulerY;
  final double eulerZ;
  final double leftEyeOpenProbability;
  final double rightEyeOpenProbability;
  final double smilingProbability;
  final int top;
  final int bottom;
  final int left;
  final int right;

  Face(
    this.id, {
    this.eulerY = -1.0,
    this.eulerZ = -1.0,
    this.leftEyeOpenProbability = -1.0,
    this.rightEyeOpenProbability = -1.0,
    this.smilingProbability = -1.0,
    this.top = -1,
    this.bottom = -1,
    this.left = -1,
    this.right = -1,
  });

  Face.fromMap(Map map)
      : id = map['id'],
        eulerY = map['eulerY'],
        eulerZ = map['eulerZ'],
        leftEyeOpenProbability = map['leftEyeOpenProbability'],
        rightEyeOpenProbability = map['rightEyeOpenProbability'],
        smilingProbability = map['smilingProbability'],
        top = map['top'],
        bottom = map['bottom'],
        left = map['left'],
        right = map['right'];

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'eulerY': eulerY,
      'eulerZ': eulerZ,
      'leftEyeOpenProbability': leftEyeOpenProbability,
      'rightEyeOpenProbability': rightEyeOpenProbability,
      'smilingProbability': smilingProbability,
      'top': top,
      'bottom': bottom,
      'left': left,
      'right': right,
    };
  }
}

///
///
///
class Size {
  final int width;
  final int height;

  const Size(this.width, this.height);

  Size.fromMap(Map map)
      : width = map['width'],
        height = map['height'];

  @override
  String toString() {
    return '$width x $height';
  }
}
