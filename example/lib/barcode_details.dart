import 'package:flutter/material.dart';
import 'package:flutter_mobile_vision/flutter_mobile_vision.dart';

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
