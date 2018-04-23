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
  bool _multiple = false;
  bool _waitTap = false;

  List<Barcode> _barcodes = [];

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
          padding: const EdgeInsets.only(
            top: 12.0,
          ),
          children: _getItems(context),
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

  List<Widget> _getItems(BuildContext context) {
    List<Widget> items = [];

    items.add(new Padding(
      padding: const EdgeInsets.only(
        top: 8.0,
        left: 18.0,
        right: 18.0,
      ),
      child: new Text('Scan format only:'),
    ));

    items.add(new Padding(
      padding: const EdgeInsets.only(
        left: 18.0,
        right: 18.0,
      ),
      child: new DropdownButton(
        items: _getFormats(),
        onChanged: (value) => setState(
              () => _onlyFormat = value,
            ),
        value: _onlyFormat,
      ),
    ));

    items.add(new SwitchListTile(
      title: new Text('Auto focus:'),
      value: _autoFocus,
      onChanged: (value) => setState(() => _autoFocus = value),
    ));

    items.add(new SwitchListTile(
      title: new Text('Torch:'),
      value: _torch,
      onChanged: (value) => setState(() => _torch = value),
    ));

    items.add(new SwitchListTile(
      title: new Text('Multiple Scan:'),
      value: _multiple,
      onChanged: (value) => setState(() {
            _multiple = value;
            if (value) _waitTap = true;
          }),
    ));

    items.add(new SwitchListTile(
      title: new Text('Wait a tap to capture:'),
      value: _waitTap,
      onChanged: (value) => setState(() {
            _waitTap = value;
            if (!value) _multiple = false;
          }),
    ));

    items.add(
      new Padding(
        padding: const EdgeInsets.only(
          left: 18.0,
          right: 18.0,
          bottom: 12.0,
        ),
        child: new RaisedButton(
          onPressed: _scan,
          child: new Text('SCAN!'),
        ),
      ),
    );

    items.addAll(
      ListTile.divideTiles(
        context: context,
        tiles: _barcodes
            .map(
              (barcode) => new BarcodeWidget(barcode),
            )
            .toList(),
      ),
    );

    return items;
  }

  Future<Null> _scan() async {
    List<Barcode> barcodes = [];
    try {
      barcodes = await FlutterMobileVision.scan(
        flash: _torch,
        autoFocus: _autoFocus,
        formats: _onlyFormat,
        multiple: _multiple,
        waitTap: _waitTap,
      );
    } on Exception {
      barcodes.add(new Barcode('Failed to get barcode.'));
    }

    if (!mounted) return;

    setState(() => _barcodes = barcodes);
  }
}

class BarcodeWidget extends StatelessWidget {
  final Barcode barcode;

  BarcodeWidget(this.barcode);

  @override
  Widget build(BuildContext context) {
    return new ListTile(
      leading: const Icon(Icons.star),
      title: new Text(barcode.displayValue),
      subtitle: new Text(
        '${barcode.getFormatString()} (${barcode.format}) - '
            '${barcode.getValueFormatString()} (${barcode.valueFormat})',
      ),
      trailing: const Icon(Icons.arrow_forward),
      onTap: () => Navigator.of(context).push(
            new MaterialPageRoute(
              builder: (context) => new BarcodeDetails(barcode),
            ),
          ),
    );
  }
}

class BarcodeDetails extends StatefulWidget {
  final Barcode barcode;

  BarcodeDetails(this.barcode);

  @override
  _BarcodeDetailsState createState() => new _BarcodeDetailsState();
}

class _BarcodeDetailsState extends State<BarcodeDetails> {
  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        title: new Text('Barcode Details'),
      ),
      body: new ListView(
        children: <Widget>[
          new ListTile(
            title: new Text(widget.barcode.displayValue),
            subtitle: const Text('Display Value'),
          ),
          new ListTile(
            title: new Text(widget.barcode.rawValue),
            subtitle: const Text('Raw Value'),
          ),
          new ListTile(
            title: new Text('${widget.barcode.getFormatString()} '
                '(${widget.barcode.format})'),
            subtitle: const Text('Format'),
          ),
          new ListTile(
            title: new Text('${widget.barcode.getValueFormatString()} '
                '(${widget.barcode.valueFormat})'),
            subtitle: const Text('Value Format'),
          ),
          new ListTile(
            title: new Text(widget.barcode.top.toString()),
            subtitle: const Text('Top'),
          ),
          new ListTile(
            title: new Text(widget.barcode.bottom.toString()),
            subtitle: const Text('Bottom'),
          ),
          new ListTile(
            title: new Text(widget.barcode.left.toString()),
            subtitle: const Text('Left'),
          ),
          new ListTile(
            title: new Text(widget.barcode.right.toString()),
            subtitle: const Text('Right'),
          ),
        ],
      ),
    );
  }
}
