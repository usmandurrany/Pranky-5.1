package com.fournodes.ud.pranky;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TutorialActivity extends FragmentActivity implements IFragment {
    protected   View decorView;
    PagerAdapter pm;
    int pagePOS;
    float pageOffset;
    float mDown;
    float mUP;
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private CustomViewPager mTutPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */

    int[] images={R.mipmap.help_screen_1,R.mipmap.help_screen_2,R.mipmap.help_screen_3,R.mipmap.help_screen_4,R.mipmap.help_screen_5,R.mipmap.help_screen_6,R.mipmap.help_screen_7,R.mipmap.help_screen_8,R.mipmap.help_screen_9,R.mipmap.help_screen_10};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);


        List<TutorialFragment> TFList = new ArrayList<TutorialFragment>();

        for(int i=0;i<10;i++){
            TutorialFragment tutFrag = new TutorialFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("Image",images[i]);
            tutFrag.setArguments(bundle);
            TFList.add(tutFrag);
        }


        pm = new PagerAdapter(getSupportFragmentManager(),TutorialActivity.this,TFList);


        // Instantiate a ViewPager and a PagerAdapter.
        mTutPager = (CustomViewPager) findViewById(R.id.pagerTutorial);
        mTutPager.setAdapter(pm);
        mTutPager.setOnSwipeOutListener(new CustomViewPager.OnSwipeOutListener() {
            @Override
            public void onSwipeOutAtEnd(float x) {
                Log.w("UP",String.valueOf(x));
                mUP=x;
                if(mTutPager.getCurrentItem()==(pm.getCount()-1)) {
                    if (mUP < mDown) {
                        startActivity(new Intent(TutorialActivity.this, Main.class));
                        SharedPreferences settings = TutorialActivity.this.getSharedPreferences("PrankySharedPref", 0);
                        final SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("ShowTutorial",false);
                        editor.commit();
                        TutorialActivity.this.finish();
                    }
                }
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

    }

    @Override
    public void TutImageTouch(View view, MotionEvent motionEvent) {
        if(mTutPager.getCurrentItem()==(pm.getCount()-1)){
            switch(motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    Log.w("DOWN", String.valueOf(motionEvent.getX()));
                    mDown = motionEvent.getX();
                    break;

            }
        }
    }


}
