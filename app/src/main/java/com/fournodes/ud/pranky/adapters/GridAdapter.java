package com.fournodes.ud.pranky.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.StateListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.fournodes.ud.pranky.objects.GridItem;
import com.fournodes.ud.pranky.R;

public class GridAdapter extends BaseAdapter {

    private Context context;


    public class ViewHolder {
        public ImageView itemImage;
    }

    private GridItem[] items;
    private LayoutInflater mInflater;

    public GridAdapter(Context context, GridItem[] images) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        items = images;
    }


    @Override
    public int getCount() {
        if (items != null) {
            return items.length;
        }
        return 0;
    }

    @Override
    public Object getItem(int pos) {
        if (items != null && pos >= 0 && pos < getCount()) {
            return items[pos];
        }
        return null;
    }

    @Override
    public long getItemId(int pos) {
        if (items != null && pos >= 0 && pos < getCount()) {
            return items[pos].itemID;
        }
        return 0;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        View view = convertView;
        ViewHolder viewHolder;

         if (view == null) {

            view = mInflater.inflate(R.layout.fragment_gridview_item, parent, false);
            viewHolder = new ViewHolder();
            //viewHolder.imageView = (ImageView) view.findViewById(R.id.grid_item_bg);
            viewHolder.itemImage = (ImageView) view.findViewById(R.id.grid_item_image);
            view.setTag(viewHolder);
            //Log.e("Grid Adapter View","Null");

        } else {
         //   Log.e("Grid Adapter View","Not Null");

           viewHolder = (ViewHolder) view.getTag();

        }

        GridItem gridItem = items[pos];
        setCatImage(pos, viewHolder, gridItem.itemResID);


        return view;
    }

    private void setCatImage(int pos, ViewHolder viewHolder, Integer img) {
        if (img == R.mipmap.addmore) {
        viewHolder.itemImage.setBackgroundResource(R.drawable.btn_addsound_states);
        //viewHolder.itemImage.setTag(R.drawable.btn_addsound_states);

        }
       else {

            try {
                String name = context.getResources().getResourceEntryName(img) + "_hover";

                int imageStatePressed = R.mipmap.class.getField(name).getInt(null);
                StateListDrawable states = new StateListDrawable();
                states.addState(new int[]{android.R.attr.state_pressed},
                        context.getResources().getDrawable(imageStatePressed));
                states.addState(new int[]{android.R.attr.state_selected},
                        context.getResources().getDrawable(imageStatePressed));
                states.addState(new int[]{},
                        context.getResources().getDrawable(img));
                viewHolder.itemImage.setBackgroundDrawable(states);
            } catch (IllegalAccessException e) {
                e.printStackTrace();

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (Resources.NotFoundException e) {
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
