package com.fournodes.ud.pranky;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TutorialActivity extends FragmentActivity implements IFragment {
    protected   View decorView;
    PagerAdapter pm;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mTutPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */

    int[] images={R.drawable.help_screen1,R.drawable.help_screen2,R.drawable.help_screen3,R.drawable.help_screen4,R.drawable.help_screen5,R.drawable.help_screen6,R.drawable.help_screen7};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);


        List<TutorialFragment> TFList = new ArrayList<TutorialFragment>();

        for(int i=0;i<7;i++){
            TutorialFragment tutFrag = new TutorialFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("Image",images[i]);
            tutFrag.setArguments(bundle);
            TFList.add(tutFrag);
        }


        pm = new PagerAdapter(getSupportFragmentManager(),TutorialActivity.this,TFList);


        // Instantiate a ViewPager and a PagerAdapter.
        mTutPager = (ViewPager) findViewById(R.id.pagerTutorial);
        mTutPager.setAdapter(pm);
        mTutPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == (pm.getCount()-1)) {
                    Intent mainIntent = new Intent(TutorialActivity.this, Main.class);
                    TutorialActivity.this.startActivity(mainIntent);
                    TutorialActivity.this.finish();
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        if (mTutPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mTutPager.setCurrentItem(mTutPager.getCurrentItem() - 1);
        }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }


    @Override
    public void pageScrolled() {

    }

    @Override
    public void TutImageClick() {
        if (mTutPager.getCurrentItem() ==(pm.getCount()-1)) {
            Intent mainIntent = new Intent(TutorialActivity.this, Main.class);
            TutorialActivity.this.startActivity(mainIntent);
            this.finish();
        }
        else
        mTutPager.setCurrentItem(mTutPager.getCurrentItem()+1);
    }
}
