package com.fournodes.ud.pranky.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fournodes.ud.pranky.R;
import com.fournodes.ud.pranky.SharedPrefs;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by Usman on 2/2/2016.
 */
public class GetPremiumDialogActivity extends Activity {

    private InterstitialAd mInterstitialAd;
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_get_premium);
        onWindowFocusChanged(true);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        // mInterstitialAd.setAdUnitId("ca-app-pub-7260426673133841/1414623255");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                SharedPrefs.setPrankCount(0);
            }
        });
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("9EA2162DEEE83254D6CB1770786B77EE")
                .build();

        mInterstitialAd.loadAd(adRequest);

        ImageView showAd = (ImageView) findViewById(R.id.btnShowAd);
        ImageView buyNow = (ImageView) findViewById(R.id.btnBuyNow);
        ImageView close = (ImageView) findViewById(R.id.close);
        TextView prankCount = (TextView) findViewById(R.id.lblPrankCount);
        int pranksLeft = (Integer.parseInt(SharedPrefs.PRANK_LIMIT) - SharedPrefs.getPrankCount());
        prankCount.setText(String.valueOf(pranksLeft));

        showAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
finish();            }
        });

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
finish();
            }
        });


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
finish();            }
        });
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

}
