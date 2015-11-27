package com.fournodes.ud.pranky;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Usman on 11/23/2015.
 */
public class SharedPrefs {

    private static SharedPreferences prefs;
    private static Calendar defaultExpDate;

    public static final String APP_SERVER_ADDR = "http://192.168.1.2/pranky/"; // Shared Pref File Name

    public static final String SHARED_PREF_FILE = "PrankySharedPref"; // Shared Pref File Name
    public static final String SENT_GCM_ID_TO_SERVER = "sentGcmIDToServer"; // boolean
    public static final String REGISTRATION_COMPLETE = "registrationComplete"; // boolean
    public static final String MY_GCM_ID = "myGcmID"; // String
    public static final String EXP_DATE = "expDate"; // String
    public static final String MY_APP_ID = "myAppID"; // String
    public static final String FRND_APP_ID = "frndAppID"; // String
    public static final String PRANKABLE = "prankable"; // boolean
    public static final String PRANKABLE_RESP = "prankableResp"; //disabled for response msg, enabled for normal prank msg
    public static final String SERVER_STATE = "serverState"; // 0 for null, 1 for ready
    public static final String BG_MUSIC_ENABLED = "bgMusicEnabled"; // boolean
    public static final String APP_FIRST_LAUNCH = "appFirstLaunch"; // boolean

    private static int serverState;
    private static String myGcmID;
    private static String myAppID;
    private static String expDate;
    private static String frndAppID;
    private static String prankableResp;
    private static boolean prankable;
    private static boolean bgMusicEnabled;
    private static boolean appFirstLaunch;
    private static boolean sentGcmIDToServer;
    private static boolean registrationComplete;


    Context context;

    public SharedPrefs(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(SharedPrefs.SHARED_PREF_FILE, 0);

    }

    // Must be called every time an object of this class is created  to initialize the attributes;
    public void initAllPrefs() {
        defaultExpDate = Calendar.getInstance(TimeZone.getDefault());

        bgMusicEnabled=prefs.getBoolean(SharedPrefs.BG_MUSIC_ENABLED, true);
        appFirstLaunch=prefs.getBoolean(SharedPrefs.APP_FIRST_LAUNCH, true);
        sentGcmIDToServer=prefs.getBoolean(SharedPrefs.SENT_GCM_ID_TO_SERVER, false);
        registrationComplete=prefs.getBoolean(SharedPrefs.REGISTRATION_COMPLETE, false);
        myGcmID=prefs.getString(SharedPrefs.MY_GCM_ID,null);
        myAppID=prefs.getString(SharedPrefs.MY_APP_ID,null);
        expDate=prefs.getString(SharedPrefs.EXP_DATE,defaultExpDate.getTime().toString());
        frndAppID=prefs.getString(SharedPrefs.FRND_APP_ID, null);
        prankable=prefs.getBoolean(SharedPrefs.PRANKABLE, true);
        prankableResp=prefs.getString(SharedPrefs.PRANKABLE_RESP, "enabled");
        serverState=prefs.getInt(SharedPrefs.SERVER_STATE, 1);
    }

    public static boolean isAppFirstLaunch() {
        return appFirstLaunch;
    }

    public static void setAppFirstLaunch(boolean appFirstLaunch) {
        prefs.edit().putBoolean(SharedPrefs.APP_FIRST_LAUNCH,appFirstLaunch).apply();
        SharedPrefs.appFirstLaunch=prefs.getBoolean(SharedPrefs.APP_FIRST_LAUNCH, true);
    }

    public static boolean isBgMusicEnabled() {
        return bgMusicEnabled;
    }

    public static void setBgMusicEnabled(boolean bgMusicEnabled) {
        prefs.edit().putBoolean(SharedPrefs.BG_MUSIC_ENABLED,bgMusicEnabled).apply();
        SharedPrefs.bgMusicEnabled=prefs.getBoolean(SharedPrefs.BG_MUSIC_ENABLED, true);
    }

    public static String getMyGcmID() {
        return myGcmID;
    }

    public static void setMyGcmID(String myGcmID) {
        prefs.edit().putString(SharedPrefs.MY_GCM_ID,myGcmID).apply();
        SharedPrefs.myGcmID=prefs.getString(SharedPrefs.MY_GCM_ID,null);

    }

    public static String getMyAppID() {
        return myAppID;
    }

    public static void setMyAppID(String myAppID) {
        prefs.edit().putString(SharedPrefs.MY_APP_ID,myAppID).apply();
        SharedPrefs.myAppID=prefs.getString(SharedPrefs.MY_APP_ID,null);

    }

    public static String getExpDate() {
        return expDate;
    }

    public static void setExpDate(String expDate) {
        prefs.edit().putString(SharedPrefs.EXP_DATE,expDate).apply();
        SharedPrefs.expDate=prefs.getString(SharedPrefs.EXP_DATE,defaultExpDate.getTime().toString());

    }

    public static String getFrndAppID() {
        return frndAppID;
    }

    public static void setFrndAppID(String frndAppID) {
        prefs.edit().putString(SharedPrefs.FRND_APP_ID,frndAppID).apply();
        SharedPrefs.frndAppID=prefs.getString(SharedPrefs.FRND_APP_ID, null);

    }

    public static boolean isPrankable() {
        return prankable;
    }

    public static  void setPrankable(boolean prankable) {
        prefs.edit().putBoolean(SharedPrefs.PRANKABLE, prankable).apply();
        SharedPrefs.prankable=prefs.getBoolean(SharedPrefs.PRANKABLE, true);

    }

    public static String getPrankableResp() {
        return prankableResp;
    }

    public static void setPrankableResp(String prankableResp) {
        prefs.edit().putString(SharedPrefs.PRANKABLE_RESP,prankableResp).apply();
        SharedPrefs.prankableResp=prefs.getString(SharedPrefs.PRANKABLE_RESP, "enabled");

    }

    public static int getServerState() {
        return serverState;
    }

    public static void setServerState(int serverState) {
        prefs.edit().putInt(SharedPrefs.SERVER_STATE, serverState).apply();
        SharedPrefs.serverState=prefs.getInt(SharedPrefs.SERVER_STATE, 1);

    }

    public static boolean isSentGcmIDToServer() {
        return sentGcmIDToServer;
    }

    public static void setSentGcmIDToServer(boolean sentGcmIDToServer) {
        prefs.edit().putBoolean(SharedPrefs.SENT_GCM_ID_TO_SERVER,sentGcmIDToServer).apply();
        SharedPrefs.sentGcmIDToServer=prefs.getBoolean(SharedPrefs.SENT_GCM_ID_TO_SERVER, false);

    }

    public static boolean isRegistrationComplete() {
        return registrationComplete;
    }

    public static void setRegistrationComplete(boolean registrationComplete) {
        prefs.edit().putBoolean(SharedPrefs.REGISTRATION_COMPLETE,registrationComplete).apply();
        SharedPrefs.registrationComplete=prefs.getBoolean(SharedPrefs.REGISTRATION_COMPLETE, false);

    }


}
