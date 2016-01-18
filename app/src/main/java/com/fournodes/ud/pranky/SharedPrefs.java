package com.fournodes.ud.pranky;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Usman on 11/23/2015.
 */
public class SharedPrefs {

    public static String APP_SERVER_ID="appServerID";// Name of the font used throughout the app


    public static String DEFAULT_FONT="grinched-regular";// Name of the font used throughout the app

    public static SharedPreferences prefs;
    private static Calendar defaultExpDate;

    //public static final String APP_SERVER_ADDR = "http://192.168.1.3/pranky/";
    public static final String APP_SERVER_ADDR = "http://pranky.four-nodes.com/appserver/";

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
    public static final String PRANK_BTN_ENABLED = "prankBtnEnabled"; // boolean
    public static final String NETWORK_AVAILABLE = "networkAvailable"; // boolean
    public static final String APP_FONT = "appFont";

    public static final String APP_FIRST_LAUNCH = "appFirstLaunch"; // boolean
    public static final String LAST_PAGE_FIRST_LAUNCH = "lastPageFirstLaunch"; // boolean
    public static final String TIMER_FIRST_LAUNCH = "timerFirstLaunch"; // boolean
    public static final String CLOCK_FIRST_LAUNCH = "clockFirstLaunch"; // boolean
    public static final String SETTINGS_FIRST_LAUNCH = "settingsFirstLaunch"; // boolean
    public static final String ADD_MORE_FIRST_LAUNCH = "addmoreFirstLaunch"; // boolean
    public static final String REMOTE_PRANK_FIRST_LAUNCH = "remotePrankFirstLaunch"; // boolean

    public static final String BG_MUSIC_PLAYING = "bgMusicPlaying"; // boolean

    public static final String CUSTOM_SOUND_ADDED = "cusSoundAdded"; // boolean

    public static final String INVALID_ID_COUNT = "invalidIDCount";



    public static final String USER_NAME = "userName";
    public static final String USER_COUNTRY = "userCountry";
    public static final String USER_COUNTRY_CODE = "userCountryCode";
    public static final String USER_PHONE_NUMBER = "userPhoneNumber";


    public static final String SIGN_UP_SKIPPED = "signUpSkipped";
    public static final String SIGN_UP_COMPLETE = "signUpComplete";

    public static final String CONTACTS_STORED = "contactsStored";




    private static int invalidIDCount;

    private static int serverState;

    private static String appServerID;

    private static String myGcmID;
    private static String myAppID;
    private static String expDate;
    private static String frndAppID;
    private static String prankableResp;
    private static String appFont;
    private static boolean prankable;
    private static boolean bgMusicEnabled;
    private static boolean sentGcmIDToServer;
    private static boolean registrationComplete;
    private static boolean prankBtnEnabled;
    private static boolean networkAvailable;

    private static boolean appFirstLaunch;
    private static boolean timerFirstLaunch;
    private static boolean clockFirstLaunch;
    private static boolean settingsFirstLaunch;
    private static boolean addmoreFirstLaunch;
    private static boolean remotePrankFirstLaunch;
    private static boolean lastPageFirstLaunch;

    private static boolean bgMusicPlaying;

    private static boolean cusSoundAdded;


    private static String userName;
    private static String userCountry;
    private static String userCountryCode;
    private static String userPhoneNumber;


    private static boolean signUpSkipped;
    private static boolean signUpComplete;
    private static boolean contactsStored;






    private static Context context;

    public static void setContext(Context ctx){
        context=ctx;
    }

    public SharedPrefs(Context ctx) {
        context = ctx;
        prefs = context.getSharedPreferences(SharedPrefs.SHARED_PREF_FILE, 0);

    }

    // Must be called every time an object of this class is created  to initialize the attributes;
    public void initAllPrefs() {
        defaultExpDate = Calendar.getInstance(TimeZone.getDefault());
        prankBtnEnabled=prefs.getBoolean(SharedPrefs.PRANK_BTN_ENABLED, true); // Unused for now

        appServerID=prefs.getString(SharedPrefs.APP_SERVER_ID,null);


        networkAvailable=prefs.getBoolean(SharedPrefs.NETWORK_AVAILABLE, true);
        bgMusicEnabled=prefs.getBoolean(SharedPrefs.BG_MUSIC_ENABLED, true);
        sentGcmIDToServer=prefs.getBoolean(SharedPrefs.SENT_GCM_ID_TO_SERVER, false);
        registrationComplete=prefs.getBoolean(SharedPrefs.REGISTRATION_COMPLETE, false);
        myGcmID=prefs.getString(SharedPrefs.MY_GCM_ID,null);
        myAppID=prefs.getString(SharedPrefs.MY_APP_ID,null);
        expDate=prefs.getString(SharedPrefs.EXP_DATE,defaultExpDate.getTime().toString());
        frndAppID=prefs.getString(SharedPrefs.FRND_APP_ID, null);
        prankable=prefs.getBoolean(SharedPrefs.PRANKABLE, true);
        prankableResp=prefs.getString(SharedPrefs.PRANKABLE_RESP, "enabled");
        serverState=prefs.getInt(SharedPrefs.SERVER_STATE, 1);
        appFont=prefs.getString(SharedPrefs.APP_FONT,DEFAULT_FONT);

        appFirstLaunch=prefs.getBoolean(SharedPrefs.APP_FIRST_LAUNCH, true);
        timerFirstLaunch=prefs.getBoolean(SharedPrefs.TIMER_FIRST_LAUNCH, true);
        clockFirstLaunch=prefs.getBoolean(SharedPrefs.CLOCK_FIRST_LAUNCH, true);
        settingsFirstLaunch=prefs.getBoolean(SharedPrefs.SETTINGS_FIRST_LAUNCH, true);
        addmoreFirstLaunch=prefs.getBoolean(SharedPrefs.ADD_MORE_FIRST_LAUNCH, true);
        remotePrankFirstLaunch=prefs.getBoolean(SharedPrefs.REMOTE_PRANK_FIRST_LAUNCH, true);
        lastPageFirstLaunch=prefs.getBoolean(SharedPrefs.LAST_PAGE_FIRST_LAUNCH, true);

        bgMusicPlaying=prefs.getBoolean(SharedPrefs.BG_MUSIC_PLAYING, false);

        cusSoundAdded=prefs.getBoolean(SharedPrefs.CUSTOM_SOUND_ADDED, false);

        userName=prefs.getString(SharedPrefs.USER_NAME, "null");
        userCountry=prefs.getString(SharedPrefs.USER_COUNTRY, "null");
        userCountryCode=prefs.getString(SharedPrefs.USER_COUNTRY_CODE, "null");
        userPhoneNumber=prefs.getString(SharedPrefs.USER_PHONE_NUMBER, "null");

        invalidIDCount=prefs.getInt(SharedPrefs.INVALID_ID_COUNT,0);

        signUpSkipped=prefs.getBoolean(SharedPrefs.SIGN_UP_SKIPPED, false);
        signUpComplete=prefs.getBoolean(SharedPrefs.SIGN_UP_COMPLETE, false);

        contactsStored=prefs.getBoolean(SharedPrefs.CONTACTS_STORED, false);
    }

    public static boolean isAppFirstLaunch() {
        return appFirstLaunch;
    }

    public static boolean isBgMusicEnabled() {
        return bgMusicEnabled;
    }

    public static boolean isPrankable() {
        return prankable;
    }

    public static boolean isSentGcmIDToServer() {
        return sentGcmIDToServer;
    }

    public static boolean isPrankBtnEnabled() {
        return prankBtnEnabled;
    }

    public static boolean isRegistrationComplete() { return registrationComplete;}

    public static boolean isNetworkAvailable() {
        return networkAvailable;
    }

    public static String getMyGcmID() {
        return myGcmID;
    }

    public static String getMyAppID() {
        return myAppID;
    }

    public static String getExpDate() {
        return expDate;
    }

    public static String getFrndAppID() { return frndAppID; }

    public static String getPrankableResp() { return prankableResp; }

    public static int getServerState() {
        return serverState;
    }

    public static String getAppFont() {
        return appFont;
    }

    public static boolean isTimerFirstLaunch() {
        return timerFirstLaunch;
    }

    public static boolean isClockFirstLaunch() {
        return clockFirstLaunch;
    }

    public static boolean isSettingsFirstLaunch() {
        return settingsFirstLaunch;
    }

    public static boolean isAddmoreFirstLaunch() {
        return addmoreFirstLaunch;
    }

    public static boolean isRemotePrankFirstLaunch() {
        return remotePrankFirstLaunch;
    }

    public static boolean isBgMusicPlaying() {
        return bgMusicPlaying;
    }

    public static boolean isCusSoundAdded() {
        return cusSoundAdded;
    }

    public static boolean isLastPageFirstLaunch() {
        return lastPageFirstLaunch;
    }

    public static String getUserName() {
        return userName;
    }

    public static String getUserCountry() {
        return userCountry;
    }

    public static String getUserCountryCode() {
        return userCountryCode;
    }

    public static String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public static int getInvalidIDCount() {
        return invalidIDCount;
    }


    public static boolean isSignUpSkipped() {
        return signUpSkipped;
    }

    public static boolean isSignUpComplete() {
        return signUpComplete;
    }

    public static boolean isContactsStored() {
        return contactsStored;
    }

    public static String getAppServerID() {
        return appServerID;
    }

    public static void setAppFirstLaunch(boolean appFirstLaunch) {
        prefs.edit().putBoolean(SharedPrefs.APP_FIRST_LAUNCH,appFirstLaunch).apply();
        SharedPrefs.appFirstLaunch=appFirstLaunch;
    }

    public static void setBgMusicEnabled(boolean bgMusicEnabled) {
        prefs.edit().putBoolean(SharedPrefs.BG_MUSIC_ENABLED,bgMusicEnabled).apply();
        SharedPrefs.bgMusicEnabled=bgMusicEnabled;
    }

    public static void setMyGcmID(String myGcmID) {
        prefs.edit().putString(SharedPrefs.MY_GCM_ID, myGcmID).apply();
        SharedPrefs.myGcmID=myGcmID;

    }

    public static void setMyAppID(String myAppID) {
        prefs.edit().putString(SharedPrefs.MY_APP_ID,myAppID).apply();
        SharedPrefs.myAppID=myAppID;

    }

    public static void setExpDate(String expDate) {
        prefs.edit().putString(SharedPrefs.EXP_DATE,expDate).apply();
        SharedPrefs.expDate=expDate;

    }

    public static void setFrndAppID(String frndAppID) {
        prefs.edit().putString(SharedPrefs.FRND_APP_ID,frndAppID).apply();
        SharedPrefs.frndAppID=frndAppID;

    }

    public static  void setPrankable(boolean prankable) {
        prefs.edit().putBoolean(SharedPrefs.PRANKABLE, prankable).apply();
        SharedPrefs.prankable=prankable;

    }

    public static void setPrankableResp(String prankableResp) {
        prefs.edit().putString(SharedPrefs.PRANKABLE_RESP, prankableResp).apply();
        SharedPrefs.prankableResp=prankableResp;

    }
    public static void setServerState(int serverState) {
        prefs.edit().putInt(SharedPrefs.SERVER_STATE, serverState).apply();
        SharedPrefs.serverState=serverState;

    }

    public static void setSentGcmIDToServer(boolean sentGcmIDToServer) {
        prefs.edit().putBoolean(SharedPrefs.SENT_GCM_ID_TO_SERVER, sentGcmIDToServer).apply();
        SharedPrefs.sentGcmIDToServer=sentGcmIDToServer;

    }

    public static void setRegistrationComplete(boolean registrationComplete) {
        prefs.edit().putBoolean(SharedPrefs.REGISTRATION_COMPLETE,registrationComplete).apply();
        SharedPrefs.registrationComplete=registrationComplete;

    }

    public static void setPrankBtnEnabled(boolean prankBtnEnabled) {
        prefs.edit().putBoolean(SharedPrefs.PRANK_BTN_ENABLED,prankBtnEnabled).apply();
        SharedPrefs.prankBtnEnabled = prankBtnEnabled;
    }

    public static void setAppFont(String appFont) {
        prefs.edit().putString(SharedPrefs.APP_FONT,appFont).apply();
        SharedPrefs.appFont = appFont;
    }

    public static void setNetworkAvailable(boolean networkAvailable) {
        prefs.edit().putBoolean(SharedPrefs.NETWORK_AVAILABLE,networkAvailable).apply();
        SharedPrefs.networkAvailable = networkAvailable;
    }


    public static void setTimerFirstLaunch(boolean timerFirstLaunch) {
        prefs.edit().putBoolean(SharedPrefs.TIMER_FIRST_LAUNCH,timerFirstLaunch).apply();
        SharedPrefs.timerFirstLaunch = timerFirstLaunch;
    }

    public static void setClockFirstLaunch(boolean clockFirstLaunch) {
        prefs.edit().putBoolean(SharedPrefs.CLOCK_FIRST_LAUNCH,clockFirstLaunch).apply();
        SharedPrefs.clockFirstLaunch = clockFirstLaunch;
    }

    public static void setSettingsFirstLaunch(boolean settingsFirstLaunch) {
        prefs.edit().putBoolean(SharedPrefs.SETTINGS_FIRST_LAUNCH,settingsFirstLaunch).apply();
        SharedPrefs.settingsFirstLaunch = settingsFirstLaunch;
    }

    public static void setAddmoreFirstLaunch(boolean addmoreFirstLaunch) {
        prefs.edit().putBoolean(SharedPrefs.ADD_MORE_FIRST_LAUNCH,addmoreFirstLaunch).apply();
        SharedPrefs.addmoreFirstLaunch = addmoreFirstLaunch;
    }

    public static void setRemotePrankFirstLaunch(boolean remotePrankFirstLaunch) {
        prefs.edit().putBoolean(SharedPrefs.REMOTE_PRANK_FIRST_LAUNCH,remotePrankFirstLaunch).apply();
        SharedPrefs.remotePrankFirstLaunch = remotePrankFirstLaunch;
    }

    public static void setBgMusicPlaying(boolean bgMusicPlaying) {
        prefs.edit().putBoolean(SharedPrefs.BG_MUSIC_PLAYING,bgMusicPlaying).apply();
        SharedPrefs.bgMusicPlaying = bgMusicPlaying;
    }

    public static void setCusSoundAdded(boolean cusSoundAdded) {
        prefs.edit().putBoolean(SharedPrefs.CUSTOM_SOUND_ADDED,cusSoundAdded).apply();
        SharedPrefs.cusSoundAdded = cusSoundAdded;
    }

    public static void setLastPageFirstLaunch(boolean lastPageFirstLaunch) {
        prefs.edit().putBoolean(SharedPrefs.LAST_PAGE_FIRST_LAUNCH,lastPageFirstLaunch).apply();
        SharedPrefs.lastPageFirstLaunch = lastPageFirstLaunch;
    }

    public static void setUserName(String userName) {
        prefs.edit().putString(SharedPrefs.USER_NAME,userName.trim()).apply();
        SharedPrefs.userName = userName;
    }

    public static void setUserCountry(String userCountry) {
        prefs.edit().putString(SharedPrefs.USER_COUNTRY,userCountry).apply();
        SharedPrefs.userCountry = userCountry;
    }

    public static void setUserCountryCode(String userCountryCode) {
        prefs.edit().putString(SharedPrefs.USER_COUNTRY_CODE,userCountryCode.replace("+","")).apply();
        SharedPrefs.userCountryCode = userCountryCode;
    }

    public static void setUserPhoneNumber(String userPhoneNumber) {
        prefs.edit().putString(SharedPrefs.USER_PHONE_NUMBER,userPhoneNumber).apply();
        SharedPrefs.userPhoneNumber = userPhoneNumber;
    }

    public static void setInvalidIDCount(int invalidIDCount) {
        prefs.edit().putInt(SharedPrefs.INVALID_ID_COUNT,invalidIDCount).apply();
        SharedPrefs.invalidIDCount = invalidIDCount;
    }

    public static void setSignUpSkipped(boolean signUpSkipped) {
        prefs.edit().putBoolean(SharedPrefs.SIGN_UP_SKIPPED,signUpSkipped).apply();
        SharedPrefs.signUpSkipped = signUpSkipped;
    }

    public static void setSignUpComplete(boolean signUpComplete) {
        prefs.edit().putBoolean(SharedPrefs.SIGN_UP_COMPLETE,signUpComplete).apply();
        SharedPrefs.signUpComplete = signUpComplete;
    }

    public static void setContactsStored(boolean contactsStored) {
        prefs.edit().putBoolean(SharedPrefs.CONTACTS_STORED,contactsStored).apply();
        SharedPrefs.contactsStored = contactsStored;
    }

    public static void setAppServerID(String appServerID) {
        prefs.edit().putString(SharedPrefs.APP_SERVER_ID,appServerID).apply();
        SharedPrefs.appServerID = appServerID;
    }
}
