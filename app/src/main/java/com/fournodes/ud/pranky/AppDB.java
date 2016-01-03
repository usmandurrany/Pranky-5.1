package com.fournodes.ud.pranky;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Usman on 11/9/2015.
 */
public class AppDB extends SQLiteOpenHelper {

    public static final String TABLE_USER_SOUNDS = "usr_sounds";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PIC_LOC = "pic_loc";
    public static final String COLUMN_PIC_ALIAS = "pic_alias";
    public static final String COLUMN_SOUND_LOC = "sound_loc";
    public static final String COLUMN_REPEAT_COUNT = "repeat_count";
    public static final String COLUMN_SOUND_VOL = "sound_vol";


    private static final String DATABASE_NAME = "pranky.db";
    private static final int DATABASE_VERSION = 23;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_USER_SOUNDS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_PIC_LOC
            + " integer not null, " + COLUMN_PIC_ALIAS
            + " text not null, " + COLUMN_SOUND_LOC
            + " text not null, " + COLUMN_REPEAT_COUNT
            + " integer not null, " + COLUMN_SOUND_VOL
            + " integer not null);";

    public AppDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);

        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.annoyed + ",'annoyed','raw.annoyed'," + 1 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.vibrate + ",'vibrate','raw.vibrate'," + 3 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.bee + ",'bee','raw.bee'," + 1 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.cricket + ",'cricket','raw.cricket'," + 2 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.farting + ",'farting','raw.farting'," + 1 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.hammer + ",'hammer','raw.hammer'," + 1 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.gun + ",'gun','raw.gun'," + 1 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.cat + ",'cat','raw.cat'," + 1 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.current + ",'current','raw.current'," + 1 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.waterdrop + ",'waterdrop','raw.waterdrop'," + 3 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.knock + ",'knock','raw.knock'," + 1 + "," + 1 + ");");

        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.glassbreak + ",'glassbreak','raw.glassbreak'," + 1 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.bomb + ",'bomb','raw.bomb'," + 8 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.footsteps + ",'footsteps','raw.footsteps'," + 1 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.windblow + ",'windblow','raw.windblow'," + 1 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.elecrazor + ",'elecrazor','raw.elecrazor'," + 2 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.spoon + ",'spoon','raw.spoon'," + 1 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.door + ",'door','raw.door'," + 1 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.airhorn + ",'airhorn','raw.airhorn'," + 1 + "," + 1 + ");");

        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.bird + ",'bird','raw.bird'," + 2 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.scratch + ",'scratch','raw.scratch'," + 2 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.watertap + ",'watertap','raw.watertap'," + 3 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.paper + ",'paper','raw.paper'," + 1 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.nail + ",'nail','raw.nail'," + 1 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.mosquito + ",'mosquito','raw.mosquito'," + 2 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.siren + ",'siren','raw.siren'," + 1 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.drill + ",'drill','raw.drill'," + 1 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.cough + ",'cough','raw.cough'," + 1 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.message + ",'message','raw.message'," + 1 + "," + 1 + ");");

        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.flash + ",'flash','raw.flash'," + 10 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.flash_blink + ",'flash_blink','raw.flash_blink'," + 1 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.vibrate_hw + ",'vibrate_hw','raw.vibrate_hw'," + 10 + "," + 1 + ");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count,sound_vol) VALUES (" + R.mipmap.ringtone + ",'ringtone','raw.ringtone'," + 1 + "," + 1 + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(AppDB.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_SOUNDS);
        onCreate(db);
    }
}
