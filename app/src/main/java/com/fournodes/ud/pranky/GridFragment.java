package com.fournodes.ud.pranky;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

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

	public GridFragment(){}
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
		if (img == null) {
			img = (ImageView) v.findViewById(R.id.grid_item_image);

			viewPOS = pos;
			//Toast.makeText(activity,  Toast.LENGTH_SHORT).show();

		}

		Toast.makeText(
				activity,
				"Position Clicked: " + pos + " & Image is: "
						+ getResources().getResourceEntryName(gridItems[pos].res), Toast.LENGTH_LONG).show();
		String name = getResources().getResourceEntryName(gridItems[pos].res);
		if (getResources().getResourceEntryName(gridItems[pos].res).equals("addmore") ) {
			Toast.makeText(activity, "Add more", Toast.LENGTH_SHORT).show();
			//SoundSelect soundseldiag = new SoundSelect(activity);
			//soundseldiag.show();
			soundAct= new Intent(getActivity(), SoundSelect.class);
			startActivity(soundAct);
		} else {
			int sound = R.raw.class.getField(name).getInt(null);
			soundsel.selectedSound(sound);
			final MediaPlayer mp = MediaPlayer.create(activity, sound);
			mp.start();

			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					// Actions to do after 2 seconds
					mp.stop();
					mp.release();
				}
			}, 2000);

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

		soundsel.selectedSound(Null);


	}


}
