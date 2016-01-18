package com.fournodes.ud.pranky;

import android.app.Activity;
import android.view.View;

import com.fournodes.ud.pranky.enums.TutorialPages;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

/**
 * Created by Usman on 18/1/2016.
 */
public class Tutorial implements View.OnClickListener {
    private TutorialPages page;
    private Activity activity;
    private ShowcaseView showcaseView;

    public Tutorial(Activity activity, TutorialPages page) {
        this.activity=activity;
        this.page=page;
    }

    public void show(ViewTarget target, String title, String description){
        showcaseView = new ShowcaseView.Builder(activity)
                .withMaterialShowcase()
                .setTarget(target)
                .setContentTitle(title)
                .setContentText(description)
                .setStyle(R.style.ShowCaseViewCloseButton)
                .setOnClickListener(this)
                .build();
    }

    public void moveToNext(ViewTarget target, String title, String description){
        if (showcaseView != null){
            showcaseView.setContentTitle(title);
            showcaseView.setContentText(description);
            showcaseView.setShowcase(target,true);
        }
    }

    public void moveToNextAuto(boolean value){
        showcaseView.setOnShowcaseEventListener(new OnShowcaseEventListener() {
            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView) {

            }

            @Override
            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

            }

            @Override
            public void onShowcaseViewShow(ShowcaseView showcaseView) {

            }
        });
        showcaseView.setHideOnTouchOutside(true);
    }

    public void setVisible(boolean visible){
     if (visible && showcaseView != null && !showcaseView.isShowing())
         showcaseView.show();
     else if(showcaseView != null && showcaseView.isShowing())
        showcaseView.hide();
    }
    public void end(){
        onClick(null);
        showcaseView = null;
    }

    @Override
    public void onClick(View view) {
       if(showcaseView!= null){
            showcaseView.hide();
            switch (page){
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

            }
        }
    }
}
