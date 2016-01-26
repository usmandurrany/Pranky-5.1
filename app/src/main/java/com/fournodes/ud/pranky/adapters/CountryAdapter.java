package com.fournodes.ud.pranky.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.fournodes.ud.pranky.Country;
import com.fournodes.ud.pranky.R;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Usman on 25/1/2016.
 */
public class CountryAdapter extends ArrayAdapter<Country> implements Filterable{

    private Context context;
    private int layoutResource;
    private ArrayList<Country> countryList;
    private ArrayList<Country> cNameFilter;
    private int charLen;



    public CountryAdapter(Context context, int layoutResource, ArrayList<Country> countryList) {
        super(context, layoutResource, countryList);
        this.context=context;
        this.layoutResource=layoutResource;
        this.countryList = countryList;
        cNameFilter = new ArrayList<>(countryList);
        charLen = 9; //Could be any value greater than the filter threshold value

    }


    @Override
    public Country getItem(int pos) {
            return cNameFilter.get(pos);
    }



    @Override
    public long getItemId(int pos) {
        return cNameFilter.get(pos).getId();

    }

    @Override
    public int getCount() {
        return cNameFilter.size();
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        CountryHolder countryHolder;

        if (view == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
//            view = inflater.inflate(R.layout.spinner_row,null);
            view = inflater.inflate(layoutResource,null);
            countryHolder = new CountryHolder();
            countryHolder.countryName = (TextView) view.findViewById(R.id.country);
            view.setTag(countryHolder);
        }else{
            countryHolder=(CountryHolder) view.getTag();

        }
        countryHolder.countryName.setText(cNameFilter.get(position).getCountryName());

        return view;

    }

    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Country) resultValue).getCountryName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            if (charSequence == null || charLen > charSequence.length())
                cNameFilter = new ArrayList<>(countryList);

            //Log.e("Country List", String.valueOf(countryList.size()));
            //Log.e("Country List Filter", String.valueOf(cNameFilter.size()));
            if (charSequence != null && charSequence.length()>0) {
               // Log.e("If",charSequence.toString());
                //cNameFilter.clear();
                Iterator<Country> iterator = cNameFilter.iterator();
                while(iterator.hasNext()) {
                    Country country = iterator.next();
                    if (country.getCountryName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        //Log.e("Country",country.getCountryName());
                        //cNameFilter.add(country);
                        continue;
                    }
                    iterator.remove();
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = cNameFilter;
                filterResults.count = cNameFilter.size();
                charLen=charSequence.length();
                return filterResults;
            } else {
                FilterResults filterResults = new FilterResults();
                filterResults.values=cNameFilter;
                filterResults.count=cNameFilter.size();
                return filterResults;
            }
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            if (filterResults.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }

        }
    };

    static class CountryHolder
    {
        //ImageView imgIcon;
        TextView countryName;
    }


    public String getCountryCode(int pos){return cNameFilter.get(pos).getCountryCode();}
    public String getShortCode(int pos){return cNameFilter.get(pos).getCountryShortCode();}
    public String getCountryName(int pos){return cNameFilter.get(pos).getCountryName();}
    public int getFilterResultSize(){return cNameFilter.size();}
}
