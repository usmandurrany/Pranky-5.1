package com.fournodes.ud.pranky;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Created by Usman-Durrani on 18-Nov-15.
 */
public class TutorialPagerAdapter extends BaseAdapter {
    Context context;


    public class ViewHolder {
        public ImageView imageView;
    }

    private int[] images;
    private LayoutInflater mInflater;

    public TutorialPagerAdapter(Context context, int[] images) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.images = images;
    }

    public int[] getItems() {
        return images;
    }

    public void setItems(int[] images) {
        this.images = images;
    }

    @Override
    public int getCount() {
        if (images != null) {
            return images.length;
        }
        return 0;
    }

    @Override
    public Object getItem(int pos) {
        if (images != null && pos >= 0 && pos < getCount()) {
            return images[pos];
        }
        return null;
    }

    @Override
    public long getItemId(int pos) {
        if (images != null && pos >= 0 && pos < getCount()) {
            return pos;
        }
        return 0;
    }


    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {

            view = mInflater.inflate(R.layout.custom, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.imageView = (ImageView) view.findViewById(R.id.imgTutorial);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        setImage(pos, viewHolder, images[pos]);

        return view;
    }

    private void setImage(int pos, ViewHolder viewHolder, Integer img) {
        viewHolder.imageView.setImageResource(img);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

}
