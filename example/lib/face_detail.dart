import 'package:flutter/material.dart';
import 'package:flutter_mobile_vision/flutter_mobile_vision.dart';

class FaceDetail extends StatefulWidget {
  final Face face;

  FaceDetail(this.face);

  @override
  _FaceDetailState createState() => new _FaceDetailState();
}

class _FaceDetailState extends State<FaceDetail> {
  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        title: new Text('Face Details'),
      ),
      body: new ListView(
        children: <Widget>[
          new ListTile(
            title: new Text(widget.face.id.toString()),
            subtitle: const Text('Id'),
          ),
          new ListTile(
            title: new Text(widget.face.eulerY.toString()),
            subtitle: const Text('Euler Y'),
          ),
          new ListTile(
            title: new Text(widget.face.eulerZ.toString()),
            subtitle: const Text('Euler Z'),
          ),
          new ListTile(
            title: new Text(widget.face.leftEyeOpenProbability.toString()),
            subtitle: const Text('Left Eye Open Probability'),
          ),
          new ListTile(
            title: new Text(widget.face.rightEyeOpenProbability.toString()),
            subtitle: const Text('Right Eye Open Probability'),
          ),
          new ListTile(
            title: new Text(widget.face.smilingProbability.toString()),
            subtitle: const Text('Smiling Probability'),
          ),
          new ListTile(
            title: new Text(widget.face.top.toString()),
            subtitle: const Text('Top'),
          ),
          new ListTile(
            title: new Text(widget.face.bottom.toString()),
            subtitle: const Text('Bottom'),
          ),
          new ListTile(
            title: new Text(widget.face.left.toString()),
            subtitle: const Text('Left'),
          ),
          new ListTile(
            title: new Text(widget.face.right.toString()),
            subtitle: const Text('Right'),
          ),
        ],
      ),
    );
  }
}
