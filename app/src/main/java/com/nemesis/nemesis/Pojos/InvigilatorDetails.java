package com.nemesis.nemesis.Pojos;

import android.renderscript.ScriptIntrinsicYuvToRGB;

import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by aditya on 4/1/17.
 */

public class InvigilatorDetails {

    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("token")
    @Expose
    private String token;


    @SerializedName("center")
    @Expose
    private int center;

    @SerializedName("profile")
    @Expose
    private String profile;

    @SerializedName("firstname")
    @Expose
    private String firstname;

    @SerializedName("lastname")
    @Expose
    private String lastname;

    @SerializedName("aadhaar")
    @Expose
    private String aadhaar;

    @SerializedName("id")
    @Expose
    private String id;


    public boolean isStatus() {
        return status;
    }

    public String getToken() {
        return token;
    }

    public int getCenter() {
        return center;
    }

    public String getProfile() {
        return profile;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getAadhaar() {
        return aadhaar;
    }

    public String getId() {
        return id;
    }
}
