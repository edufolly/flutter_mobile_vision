package io.github.edufolly.fluttermobilevision.ocr;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.text.TextBlock;

import io.github.edufolly.fluttermobilevision.ui.GraphicOverlay;

class OcrGraphicTracker extends Tracker<TextBlock> {

    private GraphicOverlay<OcrGraphic> overlay;
    private OcrGraphic graphic;

    public OcrGraphicTracker(GraphicOverlay<OcrGraphic> overlay, OcrGraphic graphic) {
        this.overlay = overlay;
        this.graphic = graphic;
    }

    @Override
    public void onNewItem(int id, TextBlock textBlock) {
        graphic.setId(id);
    }

    @Override
    public void onUpdate(Detector.Detections<TextBlock> detections, TextBlock textBlock) {
        overlay.add(graphic);
        graphic.updateItem(textBlock);
    }

    @Override
    public void onMissing(Detector.Detections<TextBlock> detections) {
        overlay.remove(graphic);
    }

    @Override
    public void onDone() {
        overlay.remove(graphic);
    }
}
