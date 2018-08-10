package com.nemesis.nemesis.Prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nemesis.nemesis.Pojos.InvigilatorDetails;

/**
 * Created by aditya on 4/1/17.
 */

public class PrefUtils {

    private static final String INVIGILATOR_ID="INVIGILATOR_ID";
    private static final String INVIGILATOR_TOKEN="INVIGILATOR_TOKEN";
    private static final String INVIGILATOR_FIRSTNAME="INVIGILATOR_FIRSTNAME";
    private static final String INVIGILATOR_LASTNAME="INVIGILATOR_LASTNAME";
    private static final String INVIGILATOR_CENTER="INVIGILATOR_CENTER";
    private static final String INVIGILATOR_LOGIN_STATUS="INVIGILATOR_LOGIN_STATUS";
    private static final String INVIGILATOR_PROFILE="INVIGILATOR_PROFILE";


    public static String getInvigilatorId(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(INVIGILATOR_ID,"");
    }
    public static String getAccessToken(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(INVIGILATOR_TOKEN,"");
    }
    public static int getInvigilatorCenter(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(INVIGILATOR_CENTER,0);
    }
    public static String getInvigilatorFirstName(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(INVIGILATOR_FIRSTNAME,"");
    }
    public static String getInvigilatorLastName(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(INVIGILATOR_LASTNAME,"");
    }
    public static String getInvigilatorProfile(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(INVIGILATOR_PROFILE,"");
    }
    public static boolean getLoginStatus(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(INVIGILATOR_LOGIN_STATUS,false);
    }

    public static void logout(Context context){
        SharedPreferences.Editor edit=PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putString(INVIGILATOR_TOKEN,"");
        edit.putInt(INVIGILATOR_CENTER,0);
        edit.putBoolean(INVIGILATOR_LOGIN_STATUS,false);
        edit.putInt(INVIGILATOR_ID,0);
        edit.putString(INVIGILATOR_FIRSTNAME,"");
        edit.putString(INVIGILATOR_LASTNAME,"");
        edit.putString(INVIGILATOR_PROFILE,"");
        edit.apply();
    }

    public static void login(Context context,InvigilatorDetails invigilatorDetails){
        SharedPreferences.Editor edit=PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putString(INVIGILATOR_TOKEN,invigilatorDetails.getToken());
        edit.putString(INVIGILATOR_ID,invigilatorDetails.getId());
        edit.putString(INVIGILATOR_FIRSTNAME,invigilatorDetails.getFirstname());
        edit.putString(INVIGILATOR_LASTNAME,invigilatorDetails.getLastname());
        edit.putInt(INVIGILATOR_CENTER,invigilatorDetails.getCenter());
        edit.putString(INVIGILATOR_PROFILE,invigilatorDetails.getProfile());
        edit.putBoolean(INVIGILATOR_LOGIN_STATUS,true);
        edit.apply();
    }

}
