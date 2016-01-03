package com.fournodes.ud.pranky;

public class GridItem {

    public Integer id;
    public Integer res;
    public String sound;
    public Integer soundVol;
    public Integer soundRepeat;

    public GridItem(Integer id, Integer res, String sound) {
        this.id = id;
        this.res = res;
        this.sound = sound;
    }

    public GridItem(Integer id, Integer res, String sound, Integer soundRepeat, Integer soundVol) {
        this.id = id;
        this.res = res;
        this.sound = sound;
        this.soundVol = soundVol;
        this.soundRepeat = soundRepeat;
    }
}
