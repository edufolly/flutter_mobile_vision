package io.github.edufolly.fluttermobilevision.ocr;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.vision.text.TextBlock;

import java.util.HashMap;
import java.util.Map;

public class MyTextBlock implements Parcelable {

    private String language;
    private String value;
    private Rect boundingBox;

    public static final Creator<MyTextBlock> CREATOR = new Creator<MyTextBlock>() {
        @Override
        public MyTextBlock createFromParcel(Parcel in) {
            return new MyTextBlock(in);
        }

        @Override
        public MyTextBlock[] newArray(int size) {
            return new MyTextBlock[size];
        }
    };

    public MyTextBlock(TextBlock textBlock) {
        this.language = textBlock.getLanguage();
        this.value = textBlock.getValue();
        this.boundingBox = textBlock.getBoundingBox();
    }

    protected MyTextBlock(Parcel in) {
        this.language = in.readString();
        this.value = in.readString();
        this.boundingBox = in.readParcelable(Rect.class.getClassLoader());
    }

    public String getLanguage() {
        return language;
    }

    public String getValue() {
        return value;
    }

    public Rect getBoundingBox() {
        return boundingBox;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(language);
        dest.writeString(value);
        dest.writeParcelable(boundingBox, flags);
    }

    public Map<String, Object> getMap() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("value", getValue());
        ret.put("language", getLanguage());
        ret.put("top", boundingBox.top);
        ret.put("bottom", boundingBox.bottom);
        ret.put("left", boundingBox.left);
        ret.put("right", boundingBox.right);
        return ret;
    }
}
