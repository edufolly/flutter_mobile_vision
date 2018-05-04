import 'package:flutter/material.dart';
import 'package:flutter_mobile_vision/flutter_mobile_vision.dart';

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
