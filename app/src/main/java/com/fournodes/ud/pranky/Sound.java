package com.fournodes.ud.pranky;

import android.content.Context;
import android.util.Log;

/**
 * Created by Home on 11/25/2015.
 */
public class Sound {

    public static int sysSound = -1;
    public static String cusSound = null;
    public static String soundName = null;
    public static int repeatCount = -1;
    public static float volume = 0f;

    //<summary>
    // Takes Image ID as 1st parameter to convert into image/sound name
    // Takes Custom Sound location as 2nd parameter
    // The rest are Repeat count and Volume, respectively
    //</summary>
    public static void setSoundProp(Context context, int iRes, String sound, int rCount, int vol) {
        soundName = getSoundName(iRes, context);
        if(sound.equals("raw." + soundName)){
            cusSound = null;
            sysSound = getSoundRes(soundName);
        }else{
            sysSound = -1;
            cusSound = sound;
        }
        repeatCount = rCount;
        volume = (float) vol;
    }

    public static void clearSoundProp(){
        cusSound = null;
        soundName = null;
        sysSound = -1;
        repeatCount = -1;
        volume = 0f;
    }

    public static int getSoundRes(String soundName) {
        try {
            return R.raw.class.getField(soundName).getInt(null);
        } catch (IllegalAccessException e) {
            Log.w("Sound", e.toString());
        } catch (NoSuchFieldException e) {
            Log.w("Sound", e.toString());
        }

        return 0;
    }


    public static String getSoundName(int iRes, Context context) {
        try {
            return context.getResources().getResourceEntryName(iRes);
        } catch (Exception e) {
            Log.w("Sound", e.toString());
        }

        return null;
    }
}


