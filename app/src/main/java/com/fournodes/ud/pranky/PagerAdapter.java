package com.fournodes.ud.pranky;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by Usman on 11/6/2015.
 */public class PagerAdapter extends FragmentStatePagerAdapter {

    private List<GridFragment> gfragments;
    private List<TutorialFragment> Tfragments;

    public PagerAdapter(FragmentManager fm, List<GridFragment> gridFragments,Context context) {
        super(fm);
        this.gfragments = gridFragments;

    }

    public PagerAdapter(FragmentManager fm, Context context, List<TutorialFragment> fragments) {
        super(fm);
        this.Tfragments = fragments;

    }

    @Override
    public android.support.v4.app.Fragment getItem(int pos) {
        if (gfragments !=null)
      return this.gfragments.get(pos);
        else
            return this.Tfragments.get(pos);

    }

    @Override
    public int getCount() {
        if (gfragments !=null)
            return this.gfragments.size();
        else
            return this.Tfragments.size();

    }

}
