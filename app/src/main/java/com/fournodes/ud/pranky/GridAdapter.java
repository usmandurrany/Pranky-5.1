package com.fournodes.ud.pranky;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter{

	Context context;
	

	
	public class ViewHolder {
		public ImageView imageView;
		public ImageView itemImage;
	}
	
	private GridItems[] items;
	private LayoutInflater mInflater;
	
	public GridAdapter(Context context, GridItems[] images) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		items = images;
	}
	
	public GridItems[] getItems() {
		return items;
	}

	public void setItems(GridItems[] items) {
		this.items = items;
	}

	@Override
	public int getCount() {
		if(items != null) {
			return items.length;
		}
		return 0;
	}

	@Override
	public Object getItem(int pos) {
		if(items != null && pos >= 0 && pos < getCount()) {
			return items[pos];
		}
		return null;
	}

	@Override
	public long getItemId(int pos) {
		if(items != null && pos >= 0 && pos < getCount()) {
			return items[pos].id;
		}
		return 0;
	}

	public String getImageSound(int pos) {
		if(items != null && pos >= 0 && pos < getCount()) {
			return items[pos].sound;
		}
		return null;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		
		View view = convertView;
		ViewHolder viewHolder;
		
		if(view == null) {
			
			view = mInflater.inflate(R.layout.custom, parent, false);
			viewHolder = new ViewHolder();
			//viewHolder.imageView = (ImageView) view.findViewById(R.id.grid_item_bg);
			viewHolder.itemImage = (ImageView) view.findViewById(R.id.grid_item_image);
			view.setTag(viewHolder);
			
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		GridItems gridItems = items[pos];
		setCatImage(pos, viewHolder, gridItems.res);
		
		return view;
	}
	
	private void setCatImage(int pos, ViewHolder viewHolder, Integer img) {
		if(img == R.mipmap.addmore)
		viewHolder.itemImage.setBackgroundResource(R.drawable.addstates);
		else{

			try {
				String name = context.getResources().getResourceEntryName(img)+"_hover";

				int imageStatePressed = R.mipmap.class.getField(name).getInt(null);
				StateListDrawable states = new StateListDrawable();
				states.addState(new int[] {android.R.attr.state_pressed},
						context.getResources().getDrawable(imageStatePressed));
				states.addState(new int[] {android.R.attr.state_selected},
						context.getResources().getDrawable(imageStatePressed));
				states.addState(new int[] { },
						context.getResources().getDrawable(img));
				viewHolder.itemImage.setBackground(states);
			} catch (IllegalAccessException e ) {
				e.printStackTrace();

			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}


		}
		//viewHolder.itemImage.setImageResource(img);
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

}
