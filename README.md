# flutter_mobile_vision

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/26accf32303f4260ab867afb2b664bcd)](https://app.codacy.com/app/edufolly/flutter_mobile_vision?utm_source=github.com&utm_medium=referral&utm_content=edufolly/flutter_mobile_vision&utm_campaign=badger) [![pub package](https://img.shields.io/pub/v/flutter_mobile_vision.svg)](https://pub.dartlang.org/packages/flutter_mobile_vision)

Flutter implementation for Google Mobile Vision.

Based on [Google Mobile Vision](https://developers.google.com/vision/).

[Android Samples](https://github.com/googlesamples/android-vision) -=- [iOS Samples](https://github.com/googlesamples/ios-vision)

Liked? :star: Star the repo to support the project!

## Features

* [x] Android
   * [x] Barcode Scan
      * [x] Simple scan.
      * [x] Toggle torch.
      * [x] Toggle auto focus.
      * [x] Specify types of barcodes that will be read.
      * [x] Tap to capture.
      * [x] Select barcode type to be scanned.
      * [x] Scan multiple barcodes.
      * [x] Barcode coordinates.
      * [x] Show barcode text.
      * [x] Standard code.
   * [x] Recognize Text
      * [x] Simple OCR.
      * [x] Toggle torch.
      * [x] Toggle auto focus.
      * [x] Multiple recognition.
      * [x] Text language.
      * [x] Text coordinates.
      * [x] Hide recognized text.
      * [x] Standard code.
   * [x] Detect Faces
      * [x] Simple detection.
      * [x] Toggle torch.
      * [x] Toggle auto focus.
      * [x] Multiple detection.
      * [x] Face coordinates.
      * [x] Hide detection information.
      * [x] Standard code.
   * [x] Generalization of capture activities.
   * [x] Choose between back and front camera.
   * [x] Control camera FPS.

* [ ] iOS
   * [ ] Barcode Scan
      * [ ] _Future Tasks_
   * [ ] Recognize Text
      * [ ] _Future Tasks_
   * [ ] Detect Faces
      * [ ] _Future Tasks_

### Your feature isn't listed? Open a issue right now!

## Screenshots
<img src="docs/flutter_01.png" height="300em"/> <img src="docs/flutter_02.png" height="300em"/> <img src="docs/flutter_03.png" height="300em"/> <img src="docs/flutter_04.png" height="300em"/> <img src="docs/flutter_05.png" height="300em"/> <img src="docs/flutter_06.png" height="300em"/> <img src="docs/flutter_07.png" height="300em"/> <img src="docs/flutter_08.png" height="300em"/>

## Usage

[Example](https://github.com/edufolly/flutter_mobile_vision/blob/master/example/lib/main.dart)

To use this plugin :

* add the dependency to your `pubspec.yaml` file:

```yaml
  dependencies:
    flutter:
      sdk: flutter
    flutter_mobile_vision: ^0.1.1
```

-----

## Barcode

```dart
//...
List<Barcode> barcodes = [];
try {
  barcodes = await FlutterMobileVision.scan(
    flash: _torchBarcode,
    autoFocus: _autoFocusBarcode,
    formats: _onlyFormatBarcode,
    multiple: _multipleBarcode,
    waitTap: _waitTapBarcode,
    showText: _showTextBarcode,
    camera: _cameraBarcode,
    fps: 15.0,
  );
} on Exception {
  barcodes.add(new Barcode('Failed to get barcode.'));
}
//...
```

### Android

For Android, you must do the following before you can use the plugin:

* Add the camera permission to your AndroidManifest.xml

    `<uses-feature android:name="android.hardware.camera" />`

    `<uses-permission android:name="android.permission.CAMERA" />`

* Add the Barcode activity to your AndroidManifest.xml

    `<activity android:name="io.github.edufolly.fluttermobilevision.barcode.BarcodeCaptureActivity" />`

### iOS

If you can help, the community thanks. Your fork is needed. :wink:

------

## OCR

```dart
//...
List<OcrText> texts = [];
try {
  texts = await FlutterMobileVision.read(
    flash: _torchOcr,
    autoFocus: _autoFocusOcr,
    multiple: _multipleOcr,
    showText: _showTextOcr,
    camera: _cameraOcr,
    fps: 2.0,
  );
} on Exception {
  texts.add(new OcrText('Failed to recognize text.'));
}
//...
```

### Android

For Android, you must do the following before you can use the plugin:

* Add the camera permission to your AndroidManifest.xml

    `<uses-feature android:name="android.hardware.camera" />`

    `<uses-permission android:name="android.permission.CAMERA" />`

* Add the OCR activity to your AndroidManifest.xml

   `<activity android:name="io.github.edufolly.fluttermobilevision.ocr.OcrCaptureActivity" />`

### iOS

If you can help, the community thanks. Your fork is needed. :wink:

------

## Face Detection

```dart
//...
List<Face> faces = [];
try {
  faces = await FlutterMobileVision.face(
    flash: _torchFace,
    autoFocus: _autoFocusFace,
    multiple: _multipleFace,
    showText: _showTextFace,
    camera: _cameraFace,
    fps: 15.0,
  );
} on Exception {
  faces.add(new Face(-1));
}
//...
```

### Android

For Android, you must do the following before you can use the plugin:

* Add the camera permission to your AndroidManifest.xml

    `<uses-feature android:name="android.hardware.camera" />`

    `<uses-permission android:name="android.permission.CAMERA" />`

* Add the Face Detection activity to your AndroidManifest.xml

   `<activity android:name="io.github.edufolly.fluttermobilevision.face.FaceCaptureActivity" />`

### iOS

If you can help, the community thanks. Your fork is needed. :wink:
