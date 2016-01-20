package com.fournodes.ud.pranky.fragments;

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
import android.os.Parcelable;
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

import com.fournodes.ud.pranky.BackgroundMusic;
import com.fournodes.ud.pranky.CustomTextView;
import com.fournodes.ud.pranky.GridItem;
import com.fournodes.ud.pranky.PreviewMediaPlayer;
import com.fournodes.ud.pranky.R;
import com.fournodes.ud.pranky.Selection;
import com.fournodes.ud.pranky.SharedPrefs;
import com.fournodes.ud.pranky.Tutorial;
import com.fournodes.ud.pranky.activities.AddSoundDialogActivity;
import com.fournodes.ud.pranky.activities.MainActivity;
import com.fournodes.ud.pranky.adapters.GridAdapter;
import com.fournodes.ud.pranky.enums.TutorialPages;
import com.fournodes.ud.pranky.interfaces.IFragment;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.io.IOException;
import java.util.ArrayList;

import static com.fournodes.ud.pranky.PreviewMediaPlayer.getInstance;

public class GridFragment extends android.support.v4.app.Fragment implements IFragment {

    private static Ringtone r;
    private GridItem[] gridItems;
    private ImageView img;
    private Intent soundAct;
    private int lastView = -1;
    private Camera cam = null;
    private Camera.Parameters params;
    private boolean isLightOn;
    private Runnable flashBlinkRunnable;
    private int viewPOS;
    private GridView mGridView;
    private GridAdapter mGridAdapter;
    private Activity activity;
    private PreviewMediaPlayer previewSound = getInstance();
    private CustomTextView mCategory;
    private Tutorial mTutorial;
    private String category;
    private Bundle args;

    public GridFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gridview, container, false);
        activity = getActivity();
        mGridView = (GridView) view.findViewById(R.id.grid_view);
        mCategory = (CustomTextView) view.findViewById(R.id.lblCatTitle);

        Log.e("onCreateView", String.valueOf(savedInstanceState));
        if (savedInstanceState != null && savedInstanceState.getParcelableArray("icons") != null) {
            Parcelable[] ps = savedInstanceState.getParcelableArray("icons");
            gridItems = new GridItem[ps.length];
            System.arraycopy(ps, 0, gridItems, 0, ps.length);
            category = savedInstanceState.getString("category");
            mCategory.setText(category);

        } else {

            args = getArguments();
            ArrayList<Parcelable> ps = args.getParcelableArrayList("icons");
            ArrayList<GridItem> GridItemList = new ArrayList<>();
            for (Parcelable item : ps) {
                GridItemList.add((GridItem) item);
            }
            gridItems = new GridItem[GridItemList.size()];
            GridItemList.toArray(gridItems);

            category = args.getString("category").replaceAll(".(?=.)", "$0 ");
            mCategory.setText(category);
        }

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
        if (SharedPrefs.isAppFirstLaunch() && mTutorial != null) {
            ((MainActivity) activity).setTutorial(mTutorial);
        }

        try {
            if (previewSound.mp != null) {
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


        Log.w("IMAGE CLICKED", gridItems[pos].item);

        switch (gridItems[pos].item) {
            case "addSound":

                // Toast.makeText(activity, "Add more", Toast.LENGTH_SHORT).show();
                SharedPrefs.setBgMusicPlaying(true);
                soundAct = new Intent(getActivity(), AddSoundDialogActivity.class);
                startActivity(soundAct);

                break;
            case "raw.flash":

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
                    Selection.setValues(activity, gridItems[pos].itemResID, gridItems[pos].item, gridItems[pos].itemRepeatCount, gridItems[pos].itemVolume);
                }

                break;
            case "raw.flash_blink":

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
                    Selection.setValues(activity, gridItems[pos].itemResID, gridItems[pos].item, gridItems[pos].itemRepeatCount, gridItems[pos].itemVolume);
                }

                break;
            case "raw.vibrate_hw":

                Toast.makeText(activity, "Vibrate", Toast.LENGTH_SHORT).show();
                long[] pattern = {0, 2000, 1000, 2000};
                ((Vibrator) activity.getSystemService(activity.VIBRATOR_SERVICE)).vibrate(pattern, -1);

                Selection.setValues(activity,
                        gridItems[pos].itemResID,
                        gridItems[pos].item,
                        gridItems[pos].itemRepeatCount,
                        gridItems[pos].itemVolume);

                break;
            case "raw.message":
                try {
                    Selection.setValues(activity,
                            gridItems[pos].itemResID,
                            gridItems[pos].item,
                            gridItems[pos].itemRepeatCount,
                            gridItems[pos].itemVolume);

                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    r = RingtoneManager.getRingtone(activity, notification);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "raw.ringtone":
                try {

                    if ((lastView != viewPOS) || (lastView != viewPOS && lastView == -1)) {


                        Selection.setValues(activity,
                                gridItems[pos].itemResID,
                                gridItems[pos].item,
                                gridItems[pos].itemRepeatCount,
                                gridItems[pos].itemVolume);

                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                        r = RingtoneManager.getRingtone(activity, notification);
                        r.play();
                        lastView = -2;


                    } else
                        lastView = -1;


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:

                Selection.setValues(activity,
                        gridItems[pos].itemResID,
                        gridItems[pos].item,
                        gridItems[pos].itemRepeatCount,
                        gridItems[pos].itemVolume);

                if (Selection.itemSound != -1) {
                    previewSound.mp = MediaPlayer.create(activity, Selection.itemSound);
                } else if (Selection.itemCustomSound != null) {
                    previewSound.mp = new MediaPlayer();
                    try {
                        previewSound.mp.setDataSource(gridItems[pos].item);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    previewSound.mp.prepareAsync();

                }

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
                break;
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
        Selection.clearSoundProp();

    }

    @Override
    public void pageLast(int addSoundLoc) {
        if (SharedPrefs.isLastPageFirstLaunch() && mTutorial == null) {
            ImageView gridChildLast = (ImageView) mGridView.getChildAt(addSoundLoc);
            mTutorial = new Tutorial(activity, TutorialPages.MainActivityLastPage);
            mTutorial.show(new ViewTarget(gridChildLast), "Add more sounds", "Tap on the '+' to add a custom sound of your choice");
        }
    }

    public void pageFirst() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mGridView.getChildCount() == 9) {
                    Log.e("Child Count", "9");
                    if (mGridView != null) {
                        ImageView gridChild = (ImageView) mGridView.getChildAt(4);
                        mTutorial = new Tutorial(activity, TutorialPages.MainActivity);
                        mTutorial.show(new ViewTarget(gridChild), "Select a sound", "Tap on the icon to preview the sound and select it");

                    }
                } else {

                    //handler.postDelayed(this,50);
                }

            }

        }, 50);
        //Log.e("First Page",String.valueOf(mGridView));

    }

    @Override
    public void shakeIcons() {
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.shake);
        animation.setDuration(200);

        final int size = mGridView.getChildCount();
        for (int i = 0; i < size; i++) {
            ImageView animImg = (ImageView) mGridView.getChildAt(i);
            animImg.startAnimation(animation);
        }

    }

    private void flipFlash() {
        try {
            if (isLightOn) {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                cam.setParameters(params);
                isLightOn = false;
            } else {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cam.setParameters(params);
                isLightOn = true;
            }
        } catch (RuntimeException e) {
            Log.w("Camera Blink", e.toString());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v("Grid Fragment Sate", category);
        Log.v("Grid Fragment Sate", String.valueOf(gridItems.length));
        outState.putParcelableArray("icons", gridItems);
        outState.putString("category", category);
    }

}
