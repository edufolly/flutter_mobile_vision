package io.github.edufolly.fluttermobilevision.barcode;

import com.google.android.gms.vision.barcode.Barcode;

public interface CaptureInterface {

    void captured(Barcode barcode);

}
