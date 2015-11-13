package com.fournodes.ud.pranky;

import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Usman on 11/9/2015.
 */
public class PrankyDB extends SQLiteOpenHelper {

    public static final String TABLE_USER_SOUNDS = "usr_sounds";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PIC_LOC   = "pic_loc";
    public static final String COLUMN_PIC_ALIAS   = "pic_alias";
    public static final String COLUMN_SOUND_LOC   = "sound_loc";
    public static final String COLUMN_REPEAT_COUNT   = "repeat_count";


    private static final String DATABASE_NAME = "pranky.db";
    private static final int DATABASE_VERSION = 7;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_USER_SOUNDS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_PIC_LOC
            + " integer not null, " + COLUMN_PIC_ALIAS
            + " text not null, " + COLUMN_SOUND_LOC
            + " text not null, "+ COLUMN_REPEAT_COUNT
            + " integer not null);";

    public PrankyDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);

        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.annoyed+",'annoyed','raw.annoyed',"+1+");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.vibrate+",'vibrate','raw.vibrate',"+1+");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.bee+",'bee','raw.bee',"+1+");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.gun+",'gun','raw.gun',"+1+");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.cricket+",'cricket','raw.cricket',"+1+");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.hammer+",'hammer','raw.hammer',"+1+");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.cat+",'cat','raw.cat',"+1+");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.current+",'current','raw.current',"+1+");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.waterdrop+",'waterdrop','raw.waterdrop',"+1+");");

        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.glassbreak+",'glassbreak','raw.glassbreak',"+1+");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.bomb+",'bomb','raw.bomb',"+1+");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.footsteps+",'footsteps','raw.footsteps',"+1+");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.windblow+",'windblow','raw.windblow',"+1+");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.farting+",'farting','raw.farting',"+1+");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.door+",'door','raw.door',"+1+");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.spoon+",'spoon','raw.spoon',"+1+");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.elecrazor+",'elecrazor','raw.elecrazor',"+1+");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.airhorn+",'airhorn','raw.airhorn',"+1+");");

        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.bird+",'bird','raw.bird',"+1+");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.scratch+",'scratch','raw.scratch',"+1+");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.nail+",'nail','raw.nail',"+1+");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.watertap+",'watertap','raw.watertap',"+1+");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.paper+",'paper','raw.paper',"+1+");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.mosquito+",'mosquito','raw.mosquito',"+1+");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.siren+",'siren','raw.siren',"+1+");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.cough+",'cough','raw.cough',"+1+");");
        database.execSQL("INSERT INTO usr_sounds (pic_loc,pic_alias,sound_loc,repeat_count) VALUES ("+R.mipmap.drill+",'drill','raw.drill',"+1+");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(PrankyDB.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_SOUNDS);
        onCreate(db);
    }
}
