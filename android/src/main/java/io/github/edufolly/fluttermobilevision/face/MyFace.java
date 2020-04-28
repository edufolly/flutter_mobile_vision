package io.github.edufolly.fluttermobilevision.face;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.vision.face.Face;

import java.util.HashMap;
import java.util.Map;

public class MyFace implements Parcelable {

    // TODO: Landmark
    private int id;
    private float x;
    private float y;
    private float width;
    private float height;
    private float eulerY;
    private float eulerZ;
    private float leftEyeOpenProbability;
    private float rightEyeOpenProbability;
    private float smilingProbability;

    public static final Creator<MyFace> CREATOR = new Creator<MyFace>() {
        @Override
        public MyFace createFromParcel(Parcel in) {
            return new MyFace(in);
        }

        @Override
        public MyFace[] newArray(int size) {
            return new MyFace[size];
        }
    };

    public MyFace(Face face) {
        this.id = face.getId();
        this.x = face.getPosition().x;
        this.y = face.getPosition().y;
        this.width = face.getWidth();
        this.height = face.getHeight();
        this.eulerY = face.getEulerY();
        this.eulerZ = face.getEulerZ();
        this.leftEyeOpenProbability = face.getIsLeftEyeOpenProbability();
        this.rightEyeOpenProbability = face.getIsRightEyeOpenProbability();
        this.smilingProbability = face.getIsSmilingProbability();
    }

    protected MyFace(Parcel in) {
        this.id = in.readInt();
        this.x = in.readFloat();
        this.y = in.readFloat();
        this.width = in.readFloat();
        this.height = in.readFloat();
        this.eulerY = in.readFloat();
        this.eulerZ = in.readFloat();
        this.leftEyeOpenProbability = in.readFloat();
        this.rightEyeOpenProbability = in.readFloat();
        this.smilingProbability = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeFloat(x);
        dest.writeFloat(y);
        dest.writeFloat(width);
        dest.writeFloat(height);
        dest.writeFloat(eulerY);
        dest.writeFloat(eulerZ);
        dest.writeFloat(leftEyeOpenProbability);
        dest.writeFloat(rightEyeOpenProbability);
        dest.writeFloat(smilingProbability);
    }

    public Map<String, Object> getMap() {
        float x = this.x + width / 2f;
        float y = this.y + height / 2f;
        float xOffset = width / 2f;
        float yOffset = height / 2f;
        int left = (int) (x - xOffset);
        int top = (int) (y - yOffset);
        int right = (int) (x + xOffset);
        int bottom = (int) (y + yOffset);

        Map<String, Object> ret = new HashMap<>();
        ret.put("id", id);
        ret.put("eulerY", eulerY);
        ret.put("eulerZ", eulerZ);
        ret.put("leftEyeOpenProbability", leftEyeOpenProbability);
        ret.put("rightEyeOpenProbability", rightEyeOpenProbability);
        ret.put("smilingProbability", smilingProbability);
        ret.put("top", top);
        ret.put("bottom", bottom);
        ret.put("left", left);
        ret.put("right", right);

        return ret;
    }

}
