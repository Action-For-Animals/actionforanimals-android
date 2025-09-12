package org.a5calls.android.a5calls.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents the actions object containing call and email action configurations.
 */
public class Actions implements Parcelable {
    public ActionCall call;
    public ActionEmail email;

    public Actions() {
    }

    protected Actions(Parcel in) {
        call = in.readParcelable(ActionCall.class.getClassLoader());
        email = in.readParcelable(ActionEmail.class.getClassLoader());
    }

    public static final Creator<Actions> CREATOR = new Creator<Actions>() {
        @Override
        public Actions createFromParcel(Parcel in) {
            return new Actions(in);
        }

        @Override
        public Actions[] newArray(int size) {
            return new Actions[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(call, flags);
        dest.writeParcelable(email, flags);
    }

    /**
     * Helper method to get the call script, with null safety.
     */
    public String getCallScript() {
        if (call != null && call.enabled) {
            return call.script;
        }
        return null;
    }
}