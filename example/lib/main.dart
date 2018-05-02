import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_mobile_vision/flutter_mobile_vision.dart';

void main() => runApp(new MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {
  int _onlyFormatBarcode = Barcode.ALL_FORMATS;
  bool _autoFocusBarcode = true;
  bool _torchBarcode = false;
  bool _multipleBarcode = false;
  bool _waitTapBarcode = false;
  bool _showTextBarcode = false;
  List<Barcode> _barcodes = [];

  bool _autoFocusOcr = true;
  bool _torchOcr = false;
  bool _multipleOcr = false;
  bool _showTextOcr = true;
  List<OcrText> _textsOcr = [];

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
      home: new DefaultTabController(
        length: 2,
        child: new Scaffold(
          appBar: new AppBar(
            bottom: new TabBar(
              indicatorColor: Colors.black54,
              tabs: [
                new Tab(text: 'Barcode'),
                new Tab(text: 'OCR'),
              ],
            ),
            title: new Text('Flutter Mobile Vision'),
          ),
          body: new TabBarView(children: [
            _getScanScreen(context),
            _getOcrScreen(context),
          ]),
        ),
      ),
    );
  }

  ///
  /// Scan formats
  ///
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

  ///
  /// Scan Screen
  ///
  Widget _getScanScreen(BuildContext context) {
    List<Widget> items = [];

    items.add(new Padding(
      padding: const EdgeInsets.only(
        top: 8.0,
        left: 18.0,
        right: 18.0,
      ),
      child: const Text('Scan format only:'),
    ));

    items.add(new Padding(
      padding: const EdgeInsets.only(
        left: 18.0,
        right: 18.0,
      ),
      child: new DropdownButton(
        items: _getFormats(),
        onChanged: (value) => setState(
              () => _onlyFormatBarcode = value,
            ),
        value: _onlyFormatBarcode,
      ),
    ));

    items.add(new SwitchListTile(
      title: const Text('Auto focus:'),
      value: _autoFocusBarcode,
      onChanged: (value) => setState(() => _autoFocusBarcode = value),
    ));

    items.add(new SwitchListTile(
      title: const Text('Torch:'),
      value: _torchBarcode,
      onChanged: (value) => setState(() => _torchBarcode = value),
    ));

    items.add(new SwitchListTile(
      title: const Text('Multiple Scan:'),
      value: _multipleBarcode,
      onChanged: (value) => setState(() {
            _multipleBarcode = value;
            if (value) _waitTapBarcode = true;
          }),
    ));

    items.add(new SwitchListTile(
      title: const Text('Wait a tap to capture:'),
      value: _waitTapBarcode,
      onChanged: (value) => setState(() {
            _waitTapBarcode = value;
            if (!value) _multipleBarcode = false;
          }),
    ));

    items.add(new SwitchListTile(
      title: const Text('Show text:'),
      value: _showTextBarcode,
      onChanged: (value) => setState(() => _showTextBarcode = value),
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

    return new ListView(
      padding: const EdgeInsets.only(
        top: 12.0,
      ),
      children: items,
    );
  }

  ///
  /// Scan Method
  ///
  Future<Null> _scan() async {
    List<Barcode> barcodes = [];
    try {
      barcodes = await FlutterMobileVision.scan(
        flash: _torchBarcode,
        autoFocus: _autoFocusBarcode,
        formats: _onlyFormatBarcode,
        multiple: _multipleBarcode,
        waitTap: _waitTapBarcode,
        showText: _showTextBarcode,
      );
    } on Exception {
      barcodes.add(new Barcode('Failed to get barcode.'));
    }

    if (!mounted) return;

    setState(() => _barcodes = barcodes);
  }

  ///
  /// OCR Screen
  ///
  Widget _getOcrScreen(BuildContext context) {
    List<Widget> items = [];

    items.add(new SwitchListTile(
      title: const Text('Auto focus:'),
      value: _autoFocusOcr,
      onChanged: (value) => setState(() => _autoFocusOcr = value),
    ));

    items.add(new SwitchListTile(
      title: const Text('Torch:'),
      value: _torchOcr,
      onChanged: (value) => setState(() => _torchOcr = value),
    ));

    items.add(new SwitchListTile(
      title: const Text('Multiple:'),
      value: _multipleOcr,
      onChanged: (value) => setState(() => _multipleOcr = value),
    ));

    items.add(new SwitchListTile(
      title: const Text('Show text:'),
      value: _showTextOcr,
      onChanged: (value) => setState(() => _showTextOcr = value),
    ));

    items.add(
      new Padding(
        padding: const EdgeInsets.only(
          left: 18.0,
          right: 18.0,
          bottom: 12.0,
        ),
        child: new RaisedButton(
          onPressed: _read,
          child: new Text('READ!'),
        ),
      ),
    );

    items.addAll(
      ListTile.divideTiles(
        context: context,
        tiles: _textsOcr
            .map(
              (ocrText) => new OcrTextWidget(ocrText),
            )
            .toList(),
      ),
    );

    return new ListView(
      padding: const EdgeInsets.only(
        top: 12.0,
      ),
      children: items,
    );
  }

  ///
  /// OCR Method
  ///
  Future<Null> _read() async {
    List<OcrText> texts = [];
    try {
      texts = await FlutterMobileVision.read(
        flash: _torchOcr,
        autoFocus: _autoFocusOcr,
        multiple: _multipleOcr,
        showText: _showTextOcr,
      );
    } on Exception {
      texts.add(new OcrText('Failed to recognize text.'));
    }

    if (!mounted) return;

    setState(() => _textsOcr = texts);
  }
}

///
/// BarcodeWidget
///
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

///
/// OcrTextWidget
///
class OcrTextWidget extends StatelessWidget {
  final OcrText ocrText;

  OcrTextWidget(this.ocrText);

  @override
  Widget build(BuildContext context) {
    return new ListTile(
      leading: const Icon(Icons.title),
      title: new Text(ocrText.value),
      subtitle: new Text(ocrText.language),
      trailing: const Icon(Icons.arrow_forward),
      onTap: () => Navigator.of(context).push(
            new MaterialPageRoute(
              builder: (context) => new OcrTextDetails(ocrText),
            ),
          ),
    );
  }
}

///
/// BarcodeDetails
///
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

///
/// OcrTextDetail
///
class OcrTextDetails extends StatefulWidget {
  final OcrText ocrText;

  OcrTextDetails(this.ocrText);

  @override
  _OcrTextDetailsState createState() => new _OcrTextDetailsState();
}

class _OcrTextDetailsState extends State<OcrTextDetails> {
  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        title: new Text('Barcode Details'),
      ),
      body: new ListView(
        children: <Widget>[
          new ListTile(
            title: new Text(widget.ocrText.value),
            subtitle: const Text('Value'),
          ),
          new ListTile(
            title: new Text(widget.ocrText.language),
            subtitle: const Text('Language'),
          ),
          new ListTile(
            title: new Text(widget.ocrText.top.toString()),
            subtitle: const Text('Top'),
          ),
          new ListTile(
            title: new Text(widget.ocrText.bottom.toString()),
            subtitle: const Text('Bottom'),
          ),
          new ListTile(
            title: new Text(widget.ocrText.left.toString()),
            subtitle: const Text('Left'),
          ),
          new ListTile(
            title: new Text(widget.ocrText.right.toString()),
            subtitle: const Text('Right'),
          ),
        ],
      ),
    );
  }
}
