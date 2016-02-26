package com.fournodes.ud.pranky.models;

import android.content.Context;

import com.fournodes.ud.pranky.R;

/**
 * Created by Usman on 11/2/2016.
 */
public class ItemCategory {
    private static String[] categories;

    public static String getCategory(Context context, int i) {
        categories = context.getResources().getStringArray(R.array.categories);
        return categories[i];
    }

    public static int getCount() {
        return categories.length;
    }

    public static String[] getCategories(Context context) {
        if (categories == null)
            categories = context.getResources().getStringArray(R.array.categories);
        return categories;
    }
}
