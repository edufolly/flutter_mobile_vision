package io.github.edufolly.fluttermobilevision.ocr;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.text.TextBlock;

import android.graphics.RectF;

import io.github.edufolly.fluttermobilevision.ui.GraphicOverlay;
import io.github.edufolly.fluttermobilevision.ui.ScanAreaGraphic;


class OcrGraphicTracker extends Tracker<TextBlock> {

    private GraphicOverlay<OcrGraphic> overlay;
    private OcrGraphic graphic;
    private GraphicOverlay<ScanAreaGraphic> scanAreaOverlay;

    public OcrGraphicTracker(GraphicOverlay<OcrGraphic> overlay, OcrGraphic graphic, GraphicOverlay<ScanAreaGraphic> scanAreaOverlay) {
        this.overlay = overlay;
        this.graphic = graphic;
        this.scanAreaOverlay = scanAreaOverlay;
    }

    @Override
    public void onNewItem(int id, TextBlock textBlock) {
        graphic.setId(id);
    }

    @Override
    public void onUpdate(Detector.Detections<TextBlock> detections, TextBlock textBlock) {
        // Only one ScanAreaGraphic exists in scanAreaOverlay
        ScanAreaGraphic scanAreaGraphic = this.scanAreaOverlay.getBest(0,0);
        // Translate textBlock to scanAreaOverlay's scale 
        RectF scaledTextBlockRectF = scanAreaGraphic.translateRect(new RectF(textBlock.getBoundingBox()));
        // // Check that detected text fits within scan area in order to add
        if (scanAreaGraphic.getBoundingBox().contains(scaledTextBlockRectF)) {
          overlay.add(graphic);
          graphic.updateItem(textBlock);
        }
        // If not, remove potential text graphic from overlay (graphic no longer in scan area)
        else {
          overlay.remove(graphic);
        }
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
