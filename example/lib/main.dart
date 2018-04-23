import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_mobile_vision/flutter_mobile_vision.dart';

void main() => runApp(new MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _displayValue = 'Unknown';
  String _rawValue = 'Unknown';
  String _format = 'Unknown';
  String _valueFormat = 'Unknown';

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      theme: new ThemeData(
        primarySwatch: Colors.lime,
        buttonColor: Colors.lime,
      ),
      home: new Scaffold(
        appBar: new AppBar(
          title: new Text('Flutter Mobile Vision'),
        ),
        body: new ListView(
          padding: EdgeInsets.all(40.0),
          children: <Widget>[
            new RaisedButton(
              onPressed: _scan,
              child: new Text('SCAN!'),
            ),
            new Padding(
              padding: const EdgeInsets.only(top: 12.0),
              child: new Text('Display Value: $_displayValue'),
            ),
            new Padding(
              padding: const EdgeInsets.only(top: 12.0),
              child: new Text('Raw Value: $_rawValue'),
            ),
            new Padding(
              padding: const EdgeInsets.only(top: 12.0),
              child: new Text('Format: $_format'),
            ),
            new Padding(
              padding: const EdgeInsets.only(top: 12.0),
              child: new Text('Value Format: $_valueFormat'),
            ),
          ],
        ),
      ),
    );
  }

  Future<Null> _scan() async {
    String displayValue = 'Unknow';
    String rawValue = 'Unknown';
    String format = 'Unknown';
    String valueFormat = 'Unknown';

    try {
      Barcode barcode = await FlutterMobileVision.scan(
        flash: false,
        autoFocus: true,
        formats: Barcode.ALL_FORMATS,
      );

      print(barcode.toMap());

      displayValue = barcode.displayValue;
      rawValue = barcode.rawValue;
      format = '${barcode.getFormatString()} (${barcode.format})';
      valueFormat =
          '${barcode.getValueFormatString()} (${barcode.valueFormat})';
    } on Exception {
      displayValue = 'Failed to get barcode.';
    }

    if (!mounted) return;

    setState(() {
      _displayValue = displayValue;
      _rawValue = rawValue;
      _format = format;
      _valueFormat = valueFormat;
    });
  }
}
