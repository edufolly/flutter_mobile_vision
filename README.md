# flutter_mobile_vision

Flutter implementation for Google Mobile Vision.

Based on [Google Mobile Vision](https://developers.google.com/vision/).

[Android Samples](https://github.com/googlesamples/android-vision) -=- [iOS Samples](https://github.com/googlesamples/ios-vision)

Liked? :star: Star the repo to support the project!

## Features

* [ ] Android
   * [ ] Barcode Scan
      * [x] Simple scan.
      * [x] Toggle torch.
      * [x] Toggle auto focus.
      * [x] Specify types of barcodes that will be read.
   * [ ] Detect Faces
   * [ ] Recognize Text

* [ ] iOS
   * [ ] Barcode Scan
   * [ ] Detect Faces
   * [ ] Recognize Text

## Usage

[Example](https://github.com/edufolly/flutter_mobile_vision/blob/master/example/lib/main.dart)

To use this plugin :

* add the dependency to your [pubspec.yaml](https://github.com/iampawan/Flute-Music-Player/blob/master/example/pubspec.yaml) file:

```yaml
  dependencies:
    flutter:
      sdk: flutter
    flutter_mobile_vision: ^0.0.1
```

* read a barcode:

```dart
//...
String code;
try {
  Barcode barcode = await FlutterMobileVision.scan(
    flash: false,
    autoFocus: true,
    formats: Barcode.ALL_FORMATS,
  );
  code = barcode.displayValue;
} on Exception {
  code = 'Failed to get barcode.';
}
//...
```

## Android

For Android, you must do the following before you can use the plugin:

* Add the camera permission to your AndroidManifest.xml

    `<uses-feature android:name="android.hardware.camera" />`

    `<uses-permission android:name="android.permission.CAMERA" />`

* Add the Barcode activity to your AndroidManifest.xml

    `<activity android:name="io.github.edufolly.fluttermobilevision.BarcodeCaptureActivity" />`

## iOS
If you can help, the community thanks. Your fork is needed. :wink: