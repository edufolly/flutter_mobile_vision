package io.github.edufolly.fluttermobilevision.ocr;

import android.util.Log;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.text.TextBlock;

import io.github.edufolly.fluttermobilevision.ui.GraphicOverlay;

public class OcrTrackerFactory implements MultiProcessor.Factory<TextBlock> {
    private GraphicOverlay<OcrGraphic> graphicOverlay;
    private boolean showText;

    public OcrTrackerFactory(GraphicOverlay<OcrGraphic> graphicOverlay, boolean showText) {
        this.graphicOverlay = graphicOverlay;
        this.showText = showText;
    }

    @Override
    public Tracker<TextBlock> create(TextBlock textBlock) {
        OcrGraphic graphic = new OcrGraphic(graphicOverlay, showText);
        try {
            return new OcrGraphicTracker(graphicOverlay, graphic);
        } catch (Exception ex) {
            Log.d("OcrTrackerFactory", ex.getMessage(), ex);
        }
        return null;
    }
}
