package com.fournodes.ud.pranky;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import static com.fournodes.ud.pranky.PreviewMediaPlayer.getInstance;

public class GridFragment extends android.support.v4.app.Fragment implements IFragment {

    GridItems[] gridItems = {};
    SoundSelectListener soundsel;
    ImageView img;
    Intent soundAct;
    int currVol;
    int sound;
    int lastView = -1;
    private int viewPOS;
    private GridView mGridView;
    private GridAdapter mGridAdapter;
    private Activity activity;
    private PreviewMediaPlayer previewSound = getInstance();


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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            soundsel = (SoundSelectListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement soundSelectListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (activity != null) {

            mGridAdapter = new GridAdapter(activity, gridItems);

            if (mGridView != null) {
                mGridView.setAdapter(mGridAdapter);
            }

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
                    }
                }
            });
        }
    }

    public void onGridItemClick(GridView g, View v, final int pos, long id) throws NoSuchFieldException, IllegalAccessException {


        try {
            if (previewSound.mp.isPlaying()) {
                previewSound.mp.stop();
                previewSound.mp.release();
                previewSound.mp = null;

            }
        } catch (Exception e) {
            Log.e("Preview MediaPlayer", e.toString());
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
        String name = getResources().getResourceEntryName(gridItems[pos].res);


        if (getResources().getResourceEntryName(gridItems[pos].res).equals("addmore")) {
            Toast.makeText(activity, "Add more", Toast.LENGTH_SHORT).show();
            //SoundSelect soundseldiag = new SoundSelect(activity);
            //soundseldiag.show();
            soundAct = new Intent(getActivity(), SoundSelect.class);
            startActivity(soundAct);
        } else {

            if (gridItems[pos].sound.equals("raw." + name)) {
                sound = R.raw.class.getField(name).getInt(null);
                soundsel.selectedSound(sound, gridItems[pos].sound, gridItems[pos].soundRepeat, gridItems[pos].soundVol);

                previewSound.mp = MediaPlayer.create(activity, sound);


            } else if (gridItems[pos].sound != ("raw." + name)) {
                soundsel.selectedSound(sound, gridItems[pos].sound, gridItems[pos].soundRepeat, gridItems[pos].soundVol);
                previewSound.mp = new MediaPlayer();
                try {
                    previewSound.mp.setDataSource(gridItems[pos].sound);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                previewSound.mp.prepareAsync();

            }
            final AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            currVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
            previewSound.mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            previewSound.mp.setVolume(100, 100);
            Log.w("MediaPlayer Debug", "Last View " + lastView + " Current View : " + viewPOS);

            if ((lastView != viewPOS) || (lastView != viewPOS && lastView == -1)) {

                previewSound.mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {


                        previewSound.mp.start();
                        lastView=-2;

                    }
                });


            }else
            lastView = -1;


            previewSound.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    previewSound.mp.release();
                    previewSound.mp = null;
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currVol, 0);

                }
            });


            AnimationDrawable boxsel = (AnimationDrawable) img.getDrawable();
            boxsel.start();

            previewSound.mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    previewSound.mp.release();
                    lastView = -1;
                    return false;
                }
            });
        }

    }

    @Override
    public void pageScrolled() {
        if (img != null) {
            img.setSelected(false);
            img.setImageResource(0);
            img = null;
        }
        try {
            soundsel.selectedSound(-1, null, 0, 0);
        } catch (Exception e) {
            Log.e("Sound Sel Remover", e.toString());
        }

        lastView = -1;
        viewPOS = -1;

    }

    @Override
    public void TutImageClick() {

    }

    @Override
    public void TutImageTouch(View view, MotionEvent motionEvent) {

    }


}
