package com.fournodes.ud.pranky.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
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
import com.fournodes.ud.pranky.CameraControls;
import com.fournodes.ud.pranky.CustomTextView;
import com.fournodes.ud.pranky.CustomToast;
import com.fournodes.ud.pranky.objects.GridItem;
import com.fournodes.ud.pranky.ItemSelected;
import com.fournodes.ud.pranky.PreviewMediaPlayer;
import com.fournodes.ud.pranky.R;
import com.fournodes.ud.pranky.SharedPrefs;
import com.fournodes.ud.pranky.Tutorial;
import com.fournodes.ud.pranky.activities.AddSoundDialogActivity;
import com.fournodes.ud.pranky.activities.MainActivity;
import com.fournodes.ud.pranky.adapters.GridAdapter;
import com.fournodes.ud.pranky.enums.Type;
import com.fournodes.ud.pranky.interfaces.IFragment;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.io.IOException;
import java.util.ArrayList;

import static com.fournodes.ud.pranky.PreviewMediaPlayer.getInstance;

public class GridFragment extends android.support.v4.app.Fragment implements IFragment, MediaPlayer.OnCompletionListener {

    private GridItem[] gridItems;
    private ImageView img;
    private int lastView = -1;
    private int viewPOS;
    private GridView mGridView;
    private Activity activity;
    private PreviewMediaPlayer previewSound;
    private CameraControls cControls;
    private Tutorial mTutorial;
    private String category;

    public GridFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gridview, container, false);
        activity = getActivity();
        previewSound = getInstance(activity);
        cControls = CameraControls.getInstance(activity);
        mGridView = (GridView) view.findViewById(R.id.grid_view);
        CustomTextView mCategory = (CustomTextView) view.findViewById(R.id.lblCatTitle);

        //Log.e("onCreateView", String.valueOf(savedInstanceState));
        if (savedInstanceState != null && savedInstanceState.getParcelableArray("icons") != null) {
            Parcelable[] ps = savedInstanceState.getParcelableArray("icons");
            gridItems = new GridItem[ps.length];
            System.arraycopy(ps, 0, gridItems, 0, ps.length);
            category = savedInstanceState.getString("category");
            mCategory.setText(category);

        } else {

            Bundle args = getArguments();
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

            GridAdapter mGridAdapter = new GridAdapter(activity, gridItems);

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

        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        int currVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (currVol < 7 && SharedPrefs.isVolumeLowNotify()) {
            CustomToast cToast = new CustomToast(activity,activity.getString(R.string.toast_volume_low));
            cToast.show();
            SharedPrefs.setVolumeLowNotify(false);
        }


        if (SharedPrefs.isAppFirstLaunch() && mTutorial != null) {
            ((MainActivity) activity).setTutorial(mTutorial);
        }

        previewSound.release();
        cControls.releaseCamera();

        if (img == null) {
            img = (ImageView) v.findViewById(R.id.grid_item_image);
            viewPOS = pos;
            //Toast.makeText(activity,  Toast.LENGTH_SHORT).show();
        }

        if (viewPOS == pos) {
            if (lastView != -1)
                lastView = viewPOS;

            img.setSelected(true);
            img.setImageResource(R.drawable.item_selected_animation);


        } else {
            img.setSelected(false);
            img.setImageResource(0);

            img = (ImageView) v.findViewById(R.id.grid_item_image);
            lastView = viewPOS;
            viewPOS = pos;

            img.setSelected(true);
            img.setImageResource(R.drawable.item_selected_animation);


        }


        switch (gridItems[pos].item) {

            case "addSound": {

                SharedPrefs.setBgMusicPlaying(true);
                startActivity(new Intent(getActivity(), AddSoundDialogActivity.class));
                if (mTutorial != null)
                    mTutorial.end();

                break;
            }
            case "raw.flash": {

                if ((lastView != viewPOS)) {
                    cControls.turnFlashOn(10);
                    lastView = -2;
                } else {
                    lastView = -1;
                }

                ItemSelected.setValues(activity,
                        gridItems[pos].itemResID,
                        gridItems[pos].item,
                        gridItems[pos].itemRepeatCount,
                        gridItems[pos].itemVolume);
                break;
            }
            case "raw.flash_blink": {

                if ((lastView != viewPOS)) {
                    cControls.blinkFlash(2);
                    lastView = -2;

                } else {
                   lastView = -1;
                }

                ItemSelected.setValues(activity,
                        gridItems[pos].itemResID,
                        gridItems[pos].item,
                        gridItems[pos].itemRepeatCount,
                        gridItems[pos].itemVolume);
                break;
            }
            case "raw.vibrate_hw": {

                Toast.makeText(activity, "Vibrate", Toast.LENGTH_SHORT).show();
                ((Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE)).
                        vibrate(new long[]{0, 2000, 1000, 2000}, -1);

                ItemSelected.setValues(activity,
                        gridItems[pos].itemResID,
                        gridItems[pos].item,
                        gridItems[pos].itemRepeatCount,
                        gridItems[pos].itemVolume);

                break;
            }
            case "raw.message": {

                    ItemSelected.setValues(activity,
                            gridItems[pos].itemResID,
                            gridItems[pos].item,
                            gridItems[pos].itemRepeatCount,
                            gridItems[pos].itemVolume);


                    previewSound.create(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

                break;
            }
            case "raw.ringtone": {
                    //Since ringtone manager does not have onCompleteListner
                    if ((lastView != viewPOS) || (lastView != viewPOS && lastView == -1)) {


                        ItemSelected.setValues(activity,
                                gridItems[pos].itemResID,
                                gridItems[pos].item,
                                gridItems[pos].itemRepeatCount,
                                gridItems[pos].itemVolume);

                        previewSound.create(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
                        lastView = -2;

                    } else
                        lastView = -1;



                break;
            }
            default: {

                ItemSelected.setValues(activity,
                        gridItems[pos].itemResID,
                        gridItems[pos].item,
                        gridItems[pos].itemRepeatCount,
                        gridItems[pos].itemVolume);


                if ((lastView != viewPOS)) {

                    if (ItemSelected.itemSound != -1) {

                        previewSound.create(ItemSelected.itemSound, this);
                        lastView = -2;

                    } else if (ItemSelected.itemCustomSound != null) {
                        previewSound.create(gridItems[pos].item, this);
                        lastView = -2;

                    }

                } else {
                    lastView = -1;
                    previewSound.release();
                    try {

                        BackgroundMusic.play();

                    } catch (Exception e) {
                        Log.e("Grid Fragment", e.toString());
                    }
                }

                break;
            }
        }

        AnimationDrawable selected = (AnimationDrawable) img.getDrawable();
        selected.start();
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
        ItemSelected.clearSoundProp();

    }

    @Override
    public void pageLast(int addSoundLoc) {
        if (SharedPrefs.isLastPageFirstLaunch() && (mTutorial == null || !mTutorial.isShowing())) {
            ImageView gridChildLast = (ImageView) mGridView.getChildAt(addSoundLoc);
            mTutorial = new Tutorial(activity, Type.MainActivityLastPage);
            mTutorial.show(new ViewTarget(gridChildLast),
                    activity.getString(R.string.tut_add_sound_title),
                    activity.getString(R.string.tut_add_sound_desc));
        }
    }

    public void pageFirst() {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mGridView != null) {
                    if (mGridView.getChildCount() == 9) {
                        ImageView gridChild = (ImageView) mGridView.getChildAt(4);
                        mTutorial = new Tutorial(activity, Type.MainActivity);
                        mTutorial.show(new ViewTarget(gridChild),
                                activity.getString(R.string.tut_select_sound_title),
                                activity.getString(R.string.tut_select_sound_desc2));
                        mTutorial.skipButtonDelay();

                    } else
                        handler.postDelayed(this, 50);
                } else
                    handler.postDelayed(this, 50);

            }

        }, 50);
        //Log.e("First Page",String.valueOf(mGridView));

    }

    @Override
    public void shakeIcons() {
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.icons_shake);
        animation.setDuration(200);

        final int size = mGridView.getChildCount();
        for (int i = 0; i < size; i++) {
            ImageView animImg = (ImageView) mGridView.getChildAt(i);
            animImg.startAnimation(animation);
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray("icons", gridItems);
        outState.putString("category", category);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        lastView = -1;
        try {

            BackgroundMusic.play();

        } catch (Exception e) {
            Log.e("Grid Fragment", e.toString());
        }
    }
}
