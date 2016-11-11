package com.snapsigns.my_signs;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.snapsigns.ImageSign;

import java.util.ArrayList;

/**
 * Adapter is in charge of populating the listview with list item contents, in this case ImageSigns.
 */
public class MySignsAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<ImageSign> mySigns;

    public MySignsAdapter(Context mContext, ArrayList<ImageSign> mySigns){
        this.mContext = mContext;
        this.mySigns = mySigns;
    }

    @Override
    public int getCount() {
        return mySigns.size();
    }

    @Override
    public Object getItem(int position) {
        return mySigns.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //In charge of populating each list item with a view.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
