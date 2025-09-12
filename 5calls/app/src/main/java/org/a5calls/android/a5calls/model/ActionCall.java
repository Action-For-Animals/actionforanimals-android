package org.a5calls.android.a5calls.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents the call action configuration within the actions object.
 */
public class ActionCall implements Parcelable {
    public boolean enabled;
    public String script;

    public ActionCall() {
    }

    protected ActionCall(Parcel in) {
        enabled = in.readByte() != 0;
        script = in.readString();
    }

    public static final Creator<ActionCall> CREATOR = new Creator<ActionCall>() {
        @Override
        public ActionCall createFromParcel(Parcel in) {
            return new ActionCall(in);
        }

        @Override
        public ActionCall[] newArray(int size) {
            return new ActionCall[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (enabled ? 1 : 0));
        dest.writeString(script);
    }
}