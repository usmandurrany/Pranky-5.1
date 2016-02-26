package com.fournodes.ud.pranky.models;

import android.content.Context;
import android.util.Log;

import com.fournodes.ud.pranky.R;

/**
 * Created by Home on 11/25/2015.
 */
public class ItemSelected {

    public static int itemSound = -1;
    public static String itemCustomSound = null;
    public static String itemName = null;
    public static int itemRepeatCount = -1;
    public static float itemVolume = 0f;

    //<summary>
    // Takes Image ID as 1st parameter to convert into image/item name
    // Takes Custom Sound location as 2nd parameter
    // The rest are Repeat count and Volume, respectively
    //</summary>
    public static void setValues(Context context, int imgResource, String item, int itemRepeatCount, int itemVolume) {
        if (item.equals("raw.flash") || item.equals("raw.flash_blink") || item.equals("raw.vibrate_hw") || item.equals("raw.message") || item.equals("raw.ringtone")){
            itemSound = -2; //Special condition to treat HW functions as a special type of  itemSound for network
            itemCustomSound = item;
        } else {

            itemName = getSoundName(imgResource, context);
            if (item.equals("raw." + itemName)) {
                itemCustomSound = null;
                itemSound = getSoundRes(itemName);
            } else {
                itemSound = -1;
                itemCustomSound = item;
            }
        }
        ItemSelected.itemRepeatCount = itemRepeatCount;
        ItemSelected.itemVolume = (float) itemVolume;
    }

    public static void clearSoundProp(){
        itemCustomSound = null;
        itemName = null;
        itemSound = -1;
        itemRepeatCount = -1;
        itemVolume = 0f;
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


