import 'package:flutter/material.dart';
import 'package:flutter_mobile_vision/flutter_mobile_vision.dart';

///
///
///
class BarcodeDetail extends StatefulWidget {
  final Barcode barcode;

  BarcodeDetail(this.barcode);

  @override
  _BarcodeDetailState createState() => _BarcodeDetailState();
}

///
///
///
class _BarcodeDetailState extends State<BarcodeDetail> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Barcode Details'),
      ),
      body: ListView(
        children: <Widget>[
          ListTile(
            title: Text(widget.barcode.displayValue),
            subtitle: const Text('Display Value'),
          ),
          ListTile(
            title: Text(widget.barcode.rawValue),
            subtitle: const Text('Raw Value'),
          ),
          ListTile(
            title: Text('${widget.barcode.getFormatString()} '
                '(${widget.barcode.format})'),
            subtitle: const Text('Format'),
          ),
          ListTile(
            title: Text('${widget.barcode.getValueFormatString()} '
                '(${widget.barcode.valueFormat})'),
            subtitle: const Text('Value Format'),
          ),
          ListTile(
            title: Text(widget.barcode.top.toString()),
            subtitle: const Text('Top'),
          ),
          ListTile(
            title: Text(widget.barcode.bottom.toString()),
            subtitle: const Text('Bottom'),
          ),
          ListTile(
            title: Text(widget.barcode.left.toString()),
            subtitle: const Text('Left'),
          ),
          ListTile(
            title: Text(widget.barcode.right.toString()),
            subtitle: const Text('Right'),
          ),
        ],
      ),
    );
  }
}
