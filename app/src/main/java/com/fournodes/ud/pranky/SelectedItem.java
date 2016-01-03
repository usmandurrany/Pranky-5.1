package com.fournodes.ud.pranky;

import android.content.Context;
import android.util.Log;

/**
 * Created by Home on 11/25/2015.
 */
public class SelectedItem {

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
        if (sound.equals("raw.flash") || sound.equals("raw.flash_blink") || sound.equals("raw.vibrate_hw") || sound.equals("raw.message") || sound.equals("raw.ringtone")){
            sysSound = -2; //Special condition to treat HW functions as a special type of  sysSound for network
            cusSound = sound;
        } else {

            soundName = getSoundName(iRes, context);
            if (sound.equals("raw." + soundName)) {
                cusSound = null;
                sysSound = getSoundRes(soundName);
            } else {
                sysSound = -1;
                cusSound = sound;
            }
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
            Log.w("SoundRes", e.toString());
        } catch (NoSuchFieldException e) {
            Log.w("SoundRes", e.toString());
        }

        return 0;
    }


    public static String getSoundName(int iRes, Context context) {
        try {
            return context.getResources().getResourceEntryName(iRes);
        } catch (Exception e) {
            Log.w("SoundName", e.toString());
        }

        return null;
    }
}


