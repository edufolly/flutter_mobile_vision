import 'package:flutter/material.dart';
import 'package:flutter_mobile_vision/flutter_mobile_vision.dart';

///
///
///
class FaceDetail extends StatefulWidget {
  final Face face;

  FaceDetail(this.face);

  @override
  _FaceDetailState createState() => _FaceDetailState();
}

///
///
///
class _FaceDetailState extends State<FaceDetail> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Face Details'),
      ),
      body: ListView(
        children: <Widget>[
          ListTile(
            title: Text(widget.face.id.toString()),
            subtitle: const Text('Id'),
          ),
          ListTile(
            title: Text(widget.face.eulerY.toString()),
            subtitle: const Text('Euler Y'),
          ),
          ListTile(
            title: Text(widget.face.eulerZ.toString()),
            subtitle: const Text('Euler Z'),
          ),
          ListTile(
            title: Text(widget.face.leftEyeOpenProbability.toString()),
            subtitle: const Text('Left Eye Open Probability'),
          ),
          ListTile(
            title: Text(widget.face.rightEyeOpenProbability.toString()),
            subtitle: const Text('Right Eye Open Probability'),
          ),
          ListTile(
            title: Text(widget.face.smilingProbability.toString()),
            subtitle: const Text('Smiling Probability'),
          ),
          ListTile(
            title: Text(widget.face.top.toString()),
            subtitle: const Text('Top'),
          ),
          ListTile(
            title: Text(widget.face.bottom.toString()),
            subtitle: const Text('Bottom'),
          ),
          ListTile(
            title: Text(widget.face.left.toString()),
            subtitle: const Text('Left'),
          ),
          ListTile(
            title: Text(widget.face.right.toString()),
            subtitle: const Text('Right'),
          ),
        ],
      ),
    );
  }
}
