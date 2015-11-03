package com.fournodes.ud.pranky;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter{

	Context context;
	
	int images[] = { R.mipmap.image_box, R.mipmap.image_box,
			R.mipmap.image_box, R.mipmap.image_box,
			R.mipmap.image_box, R.mipmap.image_box,
			R.mipmap.image_box, R.mipmap.image_box,
			R.mipmap.image_box };
	
	public class ViewHolder {
		public ImageView imageView;
		public ImageView itemImage;
	}
	
	private GridItems[] items;
	private LayoutInflater mInflater;
	
	public GridAdapter(Context context, GridItems[] locations) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		items = locations;
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

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		
		View view = convertView;
		ViewHolder viewHolder;
		
		if(view == null) {
			
			view = mInflater.inflate(R.layout.custom, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.imageView = (ImageView) view.findViewById(R.id.grid_item_bg);
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
		viewHolder.imageView.setImageResource(images[pos]);
		viewHolder.itemImage.setImageResource(img);
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
}
