package com.nemesis.nemesis.Pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by aditya on 4/2/17.
 */

public class CandidateDetails {

    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("firstname")
    @Expose
    private String firstname;

    @SerializedName("lastname")
    @Expose
    private String lastname;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("dob")
    @Expose
    private String dob;

    @SerializedName("enrollment")
    @Expose
    private String enrollment;

    @SerializedName("profile")
    @Expose
    private String profile;

    @SerializedName("attempts")
    @Expose
    private String attempts;

    @SerializedName("cstatus")
    @Expose
    private String cstatus;

    public boolean isStatus() {
        return status;
    }

    public String getName() {
        return firstname+" "+lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getDob() {
        return dob;
    }

    public String getEnrollment() {
        return enrollment;
    }

    public String getProfile() {
        return profile;
    }

    public String getAttempts() {
        return attempts;
    }

    public String getCstatus() {
        return cstatus;
    }
}
