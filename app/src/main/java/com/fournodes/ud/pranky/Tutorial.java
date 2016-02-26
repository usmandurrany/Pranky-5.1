package com.fournodes.ud.pranky;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;

import com.fournodes.ud.pranky.enums.Type;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

/**
 * Created by Usman on 18/1/2016.
 */
public class Tutorial implements View.OnClickListener {
    private Type page;
    private Activity activity;
    private ShowcaseView showcaseView;
    private int counter = 1;

    public Tutorial(Activity activity, Type page) {
        this.activity = activity;
        this.page = page;
    }

    public void show(ViewTarget target, String title, String description) {
        showcaseView = new ShowcaseView.Builder(activity)
                .withMaterialShowcase()
                .setTarget(target)
                .setContentTitle(title)
                .setContentText(description)
                .setStyle(R.style.ShowCaseViewCloseButton)
                .setOnClickListener(this)
                .build();
    }

    public void moveToNext(ViewTarget target, String title, String description) {
        if (showcaseView != null) {
            showcaseView.setContentTitle(title);
            showcaseView.setContentText(description);
            showcaseView.setShowcase(target, true);
        }
    }

    public void skipButtonDelay() {
        if (showcaseView != null) {
            showcaseView.hideButton();
            showcaseView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (showcaseView != null) {

                        if (showcaseView.onTouch(showcaseView, motionEvent)) {

                            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                                counter++;
                            if (counter > 2)
                                showcaseView.showButton();
                            return true;
                        } else
                            return false;
                    }
                    return false;
                }
            });

        }
    }

    public void setVisible(boolean visible) {
        if (visible && showcaseView != null && !showcaseView.isShowing())
            showcaseView.show();
        else if (showcaseView != null && showcaseView.isShowing())
            showcaseView.hide();
    }

    public boolean isShowing() {
        if (showcaseView != null)
            return showcaseView.isShowing();
        else
            return false;
    }


    public void end() {
        onClick(null);
        showcaseView = null;
    }

    @Override
    public void onClick(View view) {
        if (showcaseView != null) {
            showcaseView.hide();
            switch (page) {
                case MainActivity:
                    SharedPrefs.setAppFirstLaunch(false);
                    break;
                case TimerDialogActivity:
                    SharedPrefs.setTimerFirstLaunch(false);
                    break;
                case MainActivityLastPage:
                    SharedPrefs.setLastPageFirstLaunch(false);
                    break;
                case PrankDialogActivity:
                    SharedPrefs.setRemotePrankFirstLaunch(false);
                    break;
                case AddSoundDialogActivity:
                    SharedPrefs.setAddmoreFirstLaunch(false);
                    break;
                case MainActivityPrankCount:
                    SharedPrefs.setSmPrankLeftFirstLaunch(false);
                    break;

            }
        }
    }
}
