package com.nemesis.nemesis.Pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by aditya on 4/2/17.
 */

public class MyCandidates {
    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("count")
    @Expose
    private int count;


    @SerializedName("list")
    @Expose
    private ArrayList<HashMap<String,String>> list;

    public int getCount() {
        return count;
    }


    public ArrayList<HashMap<String, String>> getList() {
        return list;
    }
}
