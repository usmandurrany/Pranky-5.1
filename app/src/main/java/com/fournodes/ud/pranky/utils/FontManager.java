package com.fournodes.ud.pranky.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

/**
 * Created by Usman on 11/21/2015.
 */
public class FontManager {

    public static final String TYPEFACE_FOLDER = "fonts";
    public static final String TYPEFACE_EXTENSION = ".ttf";
    private static Typeface cTypeface;

    private static Hashtable<String, Typeface> sTypeFaces = new Hashtable<String, Typeface>(
            4);

    public static Typeface getTypeFace(Context context, String fontName) {
        if (cTypeface == null)
            cTypeface = sTypeFaces.get(fontName);
        else
            createTypeFace(context, fontName);

        return cTypeface;
    }

    public static void createTypeFace(Context context, String fontName) {
        String fontPath = new StringBuilder(TYPEFACE_FOLDER).append('/').append(fontName).append(TYPEFACE_EXTENSION).toString();
        cTypeface = Typeface.createFromAsset(context.getAssets(), fontPath);
        sTypeFaces.put(fontName, cTypeface);
    }
}
