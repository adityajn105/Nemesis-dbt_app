package com.nemesis.nemesis;

import java.util.HashMap;

/**
 * Created by aditya on 4/1/17.
 */

public class ARC {
    public static String getPhrase(int code){
        HashMap<Integer,String> codes;
        codes=new HashMap<Integer,String>();
        codes.put(200,"Success");
        codes.put(208,"Candidate Already Authenticated or belong to different test center.");
        codes.put(400,"Invalid User Agent.");
        codes.put(401,"Invalid or Expired Session. Please login again as invigilator.");
        codes.put(409,"Candidate not Found.");
        codes.put(404,"API endpoint not found. Contact Administration.");
        codes.put(406,"Invalid invigilator ID or Secret.");
        codes.put(500,"Unexpected Server Error. Try Again.");
        return codes.get(code);
    }
}
