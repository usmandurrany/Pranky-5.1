package com.fournodes.ud.pranky.models;

/**
 * Created by Usman on 12/1/2016.
 */
public class ContactDetails {
    private int id;
    private String[] numIDs;
    private String name;
    private String[] regNumbers;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String[] getRegNumbers() {
        return regNumbers;
    }

    public String[] getNumIDs() {
        return numIDs;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumIDs(String numIDs) {
        if (numIDs != null)
            this.numIDs = toArray(numIDs);
        else
            this.numIDs = new String[]{"-1"};
    }

    public void setRegNumbers(String regNumbers) {
        this.regNumbers = toArray(regNumbers);
    }

    public String[] toArray(String s) {
        return s.replace(" ", "").replace("[", "").replace("]", "").split(",");
    }
}
