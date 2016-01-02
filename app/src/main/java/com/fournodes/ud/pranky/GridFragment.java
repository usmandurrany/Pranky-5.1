package com.fournodes.ud.pranky;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.io.IOException;

import static com.fournodes.ud.pranky.PreviewMediaPlayer.getInstance;

public class GridFragment extends android.support.v4.app.Fragment implements IFragment {

    static Ringtone r;
    GridItems[] gridItems = {};
    ImageView img;
    Intent soundAct;
    int currVol;
    int lastView = -1;
    int i = 0;
    Camera cam = null;
    Camera.Parameters params;
    boolean isLighOn;
    Runnable flashBlinkRunnable;
    ShowcaseView showcaseView;
    private int viewPOS;
    private GridView mGridView;
    private GridAdapter mGridAdapter;
    private Activity activity;
    private PreviewMediaPlayer previewSound = getInstance();
    private Handler handler = new Handler();

    public GridFragment() {
    }

    @SuppressLint("ValidFragment")
    public GridFragment(GridItems[] gridItems, Activity activity) {
        this.gridItems = gridItems;
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view;

        view = inflater.inflate(R.layout.grid, container, false);

        mGridView = (GridView) view.findViewById(R.id.grid_view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (activity != null) {

            mGridAdapter = new GridAdapter(activity, gridItems);

            if (mGridView != null) {
                mGridView.setAdapter(mGridAdapter);

                mGridView.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int pos, long id) {

                        try {
                            onGridItemClick((GridView) parent, view, pos, id);
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    public void onGridItemClick(GridView g, View v, final int pos, long id) throws NoSuchFieldException, IllegalAccessException, IOException, InterruptedException {

        try {
            if (previewSound.mp.isPlaying()) {
                previewSound.mp.stop();
                previewSound.mp.release();
                previewSound.mp = null;

            }

        } catch (Exception e) {
            Log.e("Preview Sound", e.toString());
        }
        try {
            if (cam != null) {
                cam.stopPreview();
                cam.release();
                cam = null;
            }

        } catch (Exception e) {
            Log.e("Camera Controls", e.toString());
        }
        try {
            if (r.isPlaying()) {
                r.stop();
                r = null;

                if (r == null)
                {
                    Log.e("Ringtone Manager", String.valueOf(r));
                }
            }
        } catch (Exception e) {
            Log.e("Ringtone Manager", e.toString());
        }


        if (img == null) {
            img = (ImageView) v.findViewById(R.id.grid_item_image);

            viewPOS = pos;
            //Toast.makeText(activity,  Toast.LENGTH_SHORT).show();

        }

        if (viewPOS == pos) {
            if (lastView != -1)
                lastView = viewPOS;

            img.setSelected(true);
            img.setImageResource(R.drawable.gridselectedanim);


        } else {
            img.setSelected(false);
            img.setImageResource(0);

            img = (ImageView) v.findViewById(R.id.grid_item_image);
            lastView = viewPOS;
            viewPOS = pos;

            img.setSelected(true);
            img.setImageResource(R.drawable.gridselectedanim);


        }


        //Toast.makeText(activity,"Position Clicked: " + pos + " & Image is: "+ getResources().getResourceEntryName(gridItems[pos].res), Toast.LENGTH_LONG).show();
        //Toast.makeText(activity,"Position Clicked: " + pos + " & Repeat Count is: "+ gridItems[pos].soundRepeat, Toast.LENGTH_LONG).show();
        //Toast.makeText(activity,"Position Clicked: " + pos + " & Volume is: "+ gridItems[pos].soundVol, Toast.LENGTH_LONG).show();
        Log.w("IMAGE CLICKED", gridItems[pos].sound);

        if (gridItems[pos].sound == "addmore") {

            // Toast.makeText(activity, "Add more", Toast.LENGTH_SHORT).show();
            SharedPrefs.setBgMusicPlaying(true);
            soundAct = new Intent(getActivity(), SoundSelect.class);
            startActivity(soundAct);

        } else if (gridItems[pos].sound.equals("raw.flash")) {

            if (activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {

                try {
                    cam = Camera.open();

                } catch (RuntimeException e) {
                    Log.w("Camera Flash", e.toString());
                }
                Camera.Parameters p = cam.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cam.setParameters(p);
                cam.startPreview();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        try {
                            cam.stopPreview();
                            cam.release();
                        } catch (RuntimeException e) {
                            Log.w("Camera Flash", e.toString());
                        }

                    }
                }, 2000);
                Sound.setSoundProp(activity, gridItems[pos].res, gridItems[pos].sound, gridItems[pos].soundRepeat, gridItems[pos].soundVol);
            }

        } else if (gridItems[pos].sound.equals("raw.flash_blink")) {

            if (activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {


                flashBlinkRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            cam = Camera.open();
                        } catch (RuntimeException e) {
                            Log.w("Camera Blink", e.toString());
                        }
                        params = cam.getParameters();
                        try {
                            cam.setPreviewTexture(new SurfaceTexture(0));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        cam.startPreview();

                        for (int i = 0; i < 2; i++) {
                            try {
                                flipFlash();
                                Thread.sleep(100);
                                flipFlash();
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            cam.stopPreview();
                            cam.release();
                        } catch (RuntimeException e) {
                            Log.w("Camera Blink", e.toString());
                        }

                    }
                };

                new Thread(flashBlinkRunnable).start();
                //flashBlinkRunnable.run();
                Sound.setSoundProp(activity, gridItems[pos].res, gridItems[pos].sound, gridItems[pos].soundRepeat, gridItems[pos].soundVol);
            }

        } else if (gridItems[pos].sound.equals("raw.vibrate_hw")) {

            Toast.makeText(activity, "Vibrate", Toast.LENGTH_SHORT).show();
            long[] pattern = {0, 2000, 1000, 2000};
            ((Vibrator) activity.getSystemService(activity.VIBRATOR_SERVICE)).vibrate(pattern, -1);
            Sound.setSoundProp(activity, gridItems[pos].res, gridItems[pos].sound, gridItems[pos].soundRepeat, gridItems[pos].soundVol);

        } else if (gridItems[pos].sound.equals("raw.message")) {
            try {
                Sound.setSoundProp(activity, gridItems[pos].res, gridItems[pos].sound, gridItems[pos].soundRepeat, gridItems[pos].soundVol);

                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                r = RingtoneManager.getRingtone(activity, notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (gridItems[pos].sound.equals("raw.ringtone")) {
            try {

                if ((lastView != viewPOS) || (lastView != viewPOS && lastView == -1)) {


                    Sound.setSoundProp(activity, gridItems[pos].res, gridItems[pos].sound, gridItems[pos].soundRepeat, gridItems[pos].soundVol);

                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                    r = RingtoneManager.getRingtone(activity, notification);
                    r.play();
                    lastView = -2;


                } else
                    lastView = -1;


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Sound.setSoundProp(activity, gridItems[pos].res, gridItems[pos].sound, gridItems[pos].soundRepeat, gridItems[pos].soundVol);
            if (Sound.sysSound != -1) {
                previewSound.mp = MediaPlayer.create(activity, Sound.sysSound);
            } else if (Sound.cusSound != null) {
                previewSound.mp = new MediaPlayer();
                try {
                    previewSound.mp.setDataSource(gridItems[pos].sound);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                previewSound.mp.prepareAsync();

            }
//            final AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
//            currVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
//            previewSound.mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            previewSound.mp.setVolume(100, 100);
            Log.w("MediaPlayer Debug", "Last View " + lastView + " Current View : " + viewPOS);

            if ((lastView != viewPOS) || (lastView != viewPOS && lastView == -1)) {

                previewSound.mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {


                        previewSound.mp.start();
                        lastView = -2;
                        try {
                            if (BackgroundMusic.mp.isPlaying()) {
                                BackgroundMusic.pause();
                            }
                        } catch (Exception e) {
                            Log.e("Grid Fragment", e.toString());
                        }
                    }
                });


            } else {
                lastView = -1;
                try {

                    BackgroundMusic.play();

                } catch (Exception e) {
                    Log.e("Grid Fragment", e.toString());
                }
            }


            previewSound.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    previewSound.mp.release();
                    previewSound.mp = null;
                    // audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currVol, 0);
                    lastView = -1;
                    try {

                        BackgroundMusic.play();

                    } catch (Exception e) {
                        Log.e("Grid Fragment", e.toString());
                    }

                }
            });


            previewSound.mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    previewSound.mp.release();
                    lastView = -1;
                    return false;
                }
            });
        }

        AnimationDrawable boxsel = (AnimationDrawable) img.getDrawable();
        boxsel.start();
    }

    @Override
    public void pageScrolled() {
        if (img != null) {
            img.setSelected(false);
            img.setImageResource(0);
            img = null;
        }
        lastView = -1;
        viewPOS = -1;
        Sound.clearSoundProp();

    }

    @Override
    public void pageLast() {
        if (SharedPrefs.isLastPageFirstLaunch()) {
            final int size = mGridView.getChildCount();
            for (int i = 0; i < size; i++) {
                ImageView gridChild = (ImageView) mGridView.getChildAt(i);
                Log.e("Grid Child", gridChild.getTag().toString());
                Log.e("Grid Child TAG", String.valueOf(R.drawable.addstates));

                if (gridChild.getTag().toString().equals(String.valueOf(R.drawable.addstates))) {
                    Log.e("Add More", "Found");
                    ViewTarget target = new ViewTarget(gridChild);
                    showcaseView = new ShowcaseView.Builder(activity)
                            .withMaterialShowcase()
                            .setTarget(target)
                            .setContentTitle("Add more sounds")
                            .setContentText("Tap on the '+' to add a custom sound of your choice")
                            .setStyle(R.style.CustomShowcaseTheme3)
                            .hideOnTouchOutside()
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showcaseView.hide();
                                    SharedPrefs.setLastPageFirstLaunch(false);
                                }
                            })
                            .build();

                }
            }
        }
    }

    @Override
    public void animateIcon() {
        //final ImageView animImg = (ImageView) mGridView.getChildAt(4);
        //animImg.setImageResource(R.drawable.gridselectedanim);
        Animation animation = AnimationUtils.loadAnimation(activity,R.anim.shake);
        animation.setDuration(200);

        final int size = mGridView.getChildCount();
        for (int i = 0; i < size; i++) {
            ImageView animImg = (ImageView) mGridView.getChildAt(i);
            animImg.startAnimation(animation);
        }


        //AnimationDrawable boxsel = (AnimationDrawable) animImg.getDrawable();
        //boxsel.start();

       /* animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation shrink = AnimationUtils.loadAnimation(activity, R.anim.shrink);
                animImg.startAnimation(shrink);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });*/
        //animImg.startAnimation(animation);


       /* Handler anim = new Handler();
        anim.postDelayed(new Runnable() {
            @Override
            public void run() {
              animImg.setImageResource(0);
            }
        },700);
*/
    }


    private void flipFlash() {
        try {
            if (isLighOn) {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                cam.setParameters(params);
                isLighOn = false;
            } else {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cam.setParameters(params);
                isLighOn = true;
            }
        } catch (RuntimeException e) {
            Log.w("Camera Blink", e.toString());
        }
    }
}
