package com.nemesis.nemesis.Pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by aditya on 4/2/17.
 */

public class CandidateInfo {

    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("enrollment")
    @Expose
    private String enrollment;

    @SerializedName("firstname")
    @Expose
    private String firstname;

    @SerializedName("lastname")
    @Expose
    private String lastname;

    @SerializedName("aadhaar")
    @Expose
    private String aadhaar;

    @SerializedName("profile")
    @Expose
    private String profile;

    public boolean isStatus() {
        return status;
    }

    public String getEnrollment() {
        return enrollment;
    }

    public String getName() {
        return firstname+" "+lastname;
    }

    public String getAadhaar() {
        return aadhaar;
    }

    public String getProfile() {
        return profile;
    }
}
