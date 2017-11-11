package id.usup.absensidigital.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by acer on 10/30/2017.
 */

public class Value {
    @SerializedName("value") String mValue;
    @SerializedName("message") String mMessage;

    public String getmValue() {
        return mValue;
    }

    public void setmValue(String mValue) {
        this.mValue = mValue;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public Value(String mValue, String mMessage) {
        this.mValue = mValue;
        this.mMessage = mMessage;
    }

    @Override
    public String toString() {
        return "Value{" +
                "mValue='" + mValue + '\'' +
                ", mMessage='" + mMessage + '\'' +
                '}';
    }
}
