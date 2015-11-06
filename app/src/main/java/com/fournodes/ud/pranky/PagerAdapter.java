package com.fournodes.ud.pranky;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by Usman on 11/6/2015.
 */public class PagerAdapter extends FragmentStatePagerAdapter {

    private List<GridFragment> fragments;

    public PagerAdapter(FragmentManager fm, List<GridFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int pos) {
        return this.fragments.get(pos);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }
}
