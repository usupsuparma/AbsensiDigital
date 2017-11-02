package id.usup.absensidigital.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by acer on 10/30/2017.
 */

public class ResponseModel {
    @SerializedName("code") String code;
    @SerializedName("message") String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
