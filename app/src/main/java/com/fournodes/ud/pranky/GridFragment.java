package com.fournodes.ud.pranky;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class GridFragment extends Fragment{

	private int viewPOS;
	private GridView mGridView;
	private GridAdapter mGridAdapter;
	GridItems[] gridItems = {};
	private Activity activity;
	SoundSelectListener soundsel;
	ImageView img;

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
		try{
			soundsel= (SoundSelectListener) activity;

		}catch (ClassCastException e){
			throw new ClassCastException("Activity must implement soundSelectListener");
		}
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if(activity != null) {
			
			mGridAdapter = new GridAdapter(activity, gridItems);
			
			if(mGridView != null){
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
if (img == null){
	img = (ImageView) v.findViewById(R.id.grid_item_bg);
	viewPOS =pos;
}
		Toast.makeText(
				activity,
				"Position Clicked: " + pos + " & Image is: "
						 +	getResources().getResourceEntryName(gridItems[pos].res), Toast.LENGTH_LONG).show();
		String name = getResources().getResourceEntryName(gridItems[pos].res);
		int sound = R.raw.class.getField(name).getInt(null);
		soundsel.selectedSound(sound);
		MediaPlayer mp = MediaPlayer.create(activity, sound);
		mp.start();
		if (viewPOS == pos){
			img.setSelected(true);
			//img.setBackgroundResource(R.drawable.gridselectedanim);


		}else{
			img.setSelected(false);
			//img.setBackgroundResource(R.drawable.gridstates);

			img = (ImageView) v.findViewById(R.id.grid_item_bg);
			viewPOS = pos;
			img.setSelected(true);
			//img.setBackgroundResource(R.drawable.gridselectedanim);

		}
		StateListDrawable  boxsel = (StateListDrawable) img.getBackground();
		Drawable current = boxsel.getCurrent();
		if (current instanceof AnimationDrawable) {
			AnimationDrawable btnAnimation = (AnimationDrawable) current;
			boxsel.setEnterFadeDuration(500);
			boxsel.setExitFadeDuration(500);
			btnAnimation.start();
		}

	}
}
