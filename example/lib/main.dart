import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_mobile_vision/flutter_mobile_vision.dart';

void main() => runApp(new MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {
  int _onlyFormat = Barcode.ALL_FORMATS;
  bool _autoFocus = true;
  bool _torch = false;
  bool _waitTap = false;

  String _displayValue = 'Unknown';
  String _rawValue = 'Unknown';
  String _format = 'Unknown';
  String _valueFormat = 'Unknown';

  @override
  void initState() {
    super.initState();
  }

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
          padding: const EdgeInsets.only(top: 12.0, left: 40.0, right: 40.0),
          children: <Widget>[
            new Text('Scan format only:'),
            new DropdownButton(
              items: _getFormats(),
              onChanged: (value) => setState(() => _onlyFormat = value),
              value: _onlyFormat,
            ),
            new SwitchListTile(
              title: new Text('Auto focus:'),
              value: _autoFocus,
              onChanged: (value) => setState(() => _autoFocus = value),
            ),
            new SwitchListTile(
              title: new Text('Torch:'),
              value: _torch,
              onChanged: (value) => setState(() => _torch = value),
            ),
            new SwitchListTile(
              title: new Text('Wait a tap to capture:'),
              value: _waitTap,
              onChanged: (value) => setState(() => _waitTap = value),
            ),
            new Padding(
              padding: const EdgeInsets.only(
                top: 12.0,
                bottom: 12.0,
              ),
              child: new RaisedButton(
                onPressed: _scan,
                child: new Text('SCAN!'),
              ),
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

  List<DropdownMenuItem<int>> _getFormats() {
    List<DropdownMenuItem<int>> formatItems = [];

    Barcode.mapFormat.forEach((key, value) {
      formatItems.add(new DropdownMenuItem(
        child: new Text(value),
        value: key,
      ));
    });

    return formatItems;
  }

  Future<Null> _scan() async {
    String displayValue = 'Unknown';
    String rawValue = 'Unknown';
    String format = 'Unknown';
    String valueFormat = 'Unknown';

    try {
      Barcode barcode = await FlutterMobileVision.scan(
        flash: _torch,
        autoFocus: _autoFocus,
        formats: _onlyFormat,
        waitTap: _waitTap,
      );
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
