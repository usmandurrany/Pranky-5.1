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
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import static com.fournodes.ud.pranky.PreviewMediaPlayer.getInstance;

import java.io.IOException;

public class GridFragment extends Fragment implements IGridFragment{

	private int viewPOS;
	private GridView mGridView;
	private GridAdapter mGridAdapter;
	GridItems[] gridItems = {};
	private Activity activity;
	SoundSelectListener soundsel;
	ImageView img;
	ImageView addSoundImg;
	Intent soundAct;
	int Null;
	int currVol;
	CountDownTimer timer;
	private PreviewMediaPlayer 	previewSound = getInstance();
	int sound;


	public GridFragment(){}

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

	public void onGridItemClick(GridView g, View v, int pos, long id) throws NoSuchFieldException, IllegalAccessException {
		//int animDrawable = getResources().getIdentifier("com.fournodes.ud.pranky:drawable/gridselectedanim", null, null);
		try {
			if (previewSound.mp.isPlaying()) {
				previewSound.mp.stop();
			}
		}catch(Exception e){Log.e("Preview MediaPlayer",e.toString());}
		if (img == null) {
			img = (ImageView) v.findViewById(R.id.grid_item_image);

			viewPOS = pos;
			//Toast.makeText(activity,  Toast.LENGTH_SHORT).show();

		}

		//Toast.makeText(activity,"Position Clicked: " + pos + " & Image is: "+ getResources().getResourceEntryName(gridItems[pos].res), Toast.LENGTH_LONG).show();
		//Toast.makeText(activity,"Position Clicked: " + pos + " & Repeat Count is: "+ gridItems[pos].soundRepeat, Toast.LENGTH_LONG).show();
		//Toast.makeText(activity,"Position Clicked: " + pos + " & Volume is: "+ gridItems[pos].soundVol, Toast.LENGTH_LONG).show();
		String name = getResources().getResourceEntryName(gridItems[pos].res);


		if (getResources().getResourceEntryName(gridItems[pos].res).equals("addmore") ) {
			Toast.makeText(activity, "Add more", Toast.LENGTH_SHORT).show();
			//SoundSelect soundseldiag = new SoundSelect(activity);
			//soundseldiag.show();
			soundAct= new Intent(getActivity(), SoundSelect.class);
			startActivity(soundAct);
		} else {

			if (gridItems[pos].sound.equals("raw." + name)) {
				sound = R.raw.class.getField(name).getInt(null);
				soundsel.selectedSound(sound,gridItems[pos].sound,gridItems[pos].soundRepeat,gridItems[pos].soundVol);
				previewSound.mp = MediaPlayer.create(activity, sound);
			} else if (gridItems[pos].sound != ("raw." + name)) {
				soundsel.selectedSound(sound,gridItems[pos].sound,gridItems[pos].soundRepeat,gridItems[pos].soundVol);
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
			previewSound.mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mediaPlayer) {
					if(timer!=null)
						timer.cancel();
					previewSound.mp.start();

					timer = new CountDownTimer(2000, 1000) {
						public void onTick(long millisUntilFinished) {
						}

						public void onFinish() {
							try {
								previewSound.mp.stop();


							} catch (Exception e) {
								Log.e("MediaPlayer Killer", e.toString());
							}
							audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currVol, 0);
						}
					}.start();
				}
			});


			if (viewPOS == pos) {
				img.setSelected(true);
				img.setImageResource(R.drawable.gridselectedanim);


			} else {
				img.setSelected(false);
				img.setImageResource(0);

				img = (ImageView) v.findViewById(R.id.grid_item_image);
				viewPOS = pos;
				img.setSelected(true);
				img.setImageResource(R.drawable.gridselectedanim);

				//img.setBackgroundResource(R.drawable.gridselectedanim);

			}

	AnimationDrawable boxsel = (AnimationDrawable) img.getDrawable();
			boxsel.start();

		}
	}

	@Override
	public void pageScrolled() {
		if (img != null) {
			img.setSelected(false);
			img.setImageResource(0);
		}
		try {
			soundsel.selectedSound(Null,"null",0,0);
		}catch (Exception e){
			Log.e("Sound Sel Remover", e.toString());
		}

	}







}
