package com.fournodes.ud.pranky;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.drawable.AnimationDrawable;
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
	private View selHolder;
	private int viewPOS;
	private GridView mGridView;
	private GridAdapter mGridAdapter;
	GridItems[] gridItems = {};
	private Activity activity;
	SoundSelectListener soundsel;


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
if (selHolder == null){
	selHolder= v;
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
			selHolder.setSelected(true);
			selHolder.setBackgroundResource(R.drawable.gridselectedanim);
			AnimationDrawable boxsel = (AnimationDrawable) selHolder.getBackground();
			boxsel.start();

		}else{
			selHolder.setSelected(false);
			selHolder.setBackgroundResource(0);

			selHolder = v;
			viewPOS = pos;
			selHolder.setSelected(true);
			selHolder.setBackgroundResource(R.drawable.gridselectedanim);
			AnimationDrawable boxsel = (AnimationDrawable) selHolder.getBackground();
			boxsel.start();
		}

//		Path path = new Path();
//		Canvas c = new Canvas();
//		Paint mPaint= new Paint();
//		path.addRect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom(), Path.Direction.CW);
//		PathEffect pe = new DashPathEffect(new float[] {10, 5, 5, 5},5);
//		mPaint.setPathEffect(pe);
//		c.drawPath(path, mPaint);
		//ImageView test = (ImageView) getActivity().getResources().gridItems[pos].res);
		//test.setBackgroundColor(0xFFFFFFFF);

	}
}
