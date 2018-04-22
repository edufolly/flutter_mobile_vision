import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_mobile_vision/flutter_mobile_vision.dart';

void main() => runApp(new MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _barcode = 'Unknown';

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      theme: new ThemeData(
        primarySwatch: Colors.lime,
        buttonColor: Colors.lime,
      ),
      home: new Scaffold(
        appBar: new AppBar(
          title: new Text('Plugin example app'),
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
              child: new Text('Barcode: $_barcode'),
            ),
          ],
        ),
      ),
    );
  }

  Future<Null> _scan() async {
    String barcode;
    try {
      barcode = await FlutterMobileVision.scan(
        flash: false,
      );
    } on PlatformException {
      barcode = 'Failed to get barcode.';
    }

    if (!mounted) return;

    setState(() {
      _barcode = barcode;
    });
  }
}
