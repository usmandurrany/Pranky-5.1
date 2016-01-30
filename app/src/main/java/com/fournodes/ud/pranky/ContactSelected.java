package com.fournodes.ud.pranky;

/**
 * Created by Usman on 30/1/2016.
 */
public class ContactSelected {
    private static int id;
    private static String app_id;
    private static String name;
    private static String number;

    public static void detials(int id, String app_id, String name, String number){
        ContactSelected.id = id;
        ContactSelected.app_id = app_id;
        ContactSelected.name = name;
        ContactSelected.number = number;
    }

    public static void setId(int id) {
        ContactSelected.id = id;
    }

    public static void setApp_id(String app_id) {
        ContactSelected.app_id = app_id;
    }

    public static void setName(String name) {
        ContactSelected.name = name;
    }

    public static void setNumber(String number) {
        ContactSelected.number = number;
    }

    public static int getId() {
        return id;
    }

    public static String getApp_id() {
        return app_id;
    }

    public static String getName() {
        return name;
    }

    public static String getNumber() {
        return number;
    }
}
