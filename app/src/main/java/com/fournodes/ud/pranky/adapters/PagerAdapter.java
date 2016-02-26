package com.fournodes.ud.pranky.adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.fournodes.ud.pranky.fragments.GridFragment;

import java.util.List;

/**
 * Created by Usman on 11/6/2015.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    private List<GridFragment> fragments;

    public PagerAdapter(FragmentManager fm, List<GridFragment> gridFragments) {
        super(fm);
        this.fragments = gridFragments;

    }


    @Override
    public android.support.v4.app.Fragment getItem(int pos) {
        return this.fragments.get(pos);

    }

    @Override
    public int getCount() {
        return this.fragments.size();

    }

}
