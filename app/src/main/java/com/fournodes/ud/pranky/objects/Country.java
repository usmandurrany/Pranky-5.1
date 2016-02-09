package com.fournodes.ud.pranky.objects;

/**
 * Created by Usman on 25/1/2016.
 */
public class Country {
    private int id;
    private String countryShortCode;
    private String countryCode;
    private String countryName;

    public Country(int id,String countryShortCode, String countryCode, String countryName) {
        this.countryShortCode = countryShortCode;
        this.id = id;
        this.countryCode = countryCode;
        this.countryName = countryName;
    }

    public int getId() {
        return id;
    }

    public String getCountryShortCode() {
        return countryShortCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCountryName() {
        return countryName;
    }
}
