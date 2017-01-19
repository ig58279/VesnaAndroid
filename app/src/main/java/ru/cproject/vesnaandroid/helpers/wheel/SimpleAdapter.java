package ru.cproject.vesnaandroid.helpers.wheel;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.helpers.wheel.adapters.WheelViewAdapter;


/**
 * Created by Bitizen on 04.10.16.
 */

public class SimpleAdapter implements WheelViewAdapter {
    private ArrayList<String> list;
    private Context context;

    public SimpleAdapter(ArrayList<String>  list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getItemsCount() {
        return list.size();
    }

    @Override
    public View getItem(int index, View convertView, ViewGroup parent) {
        LayoutInflater lf = LayoutInflater.from(parent.getContext());
        View item = lf.inflate(R.layout.item_wheel_item, parent, false);
        ((TextView) item.findViewById(R.id.item)).setText(list.get(index));
        return item;
    }

    @Override
    public View getEmptyItem(View convertView, ViewGroup parent) {
        LayoutInflater lf = LayoutInflater.from(parent.getContext());
        return lf.inflate(R.layout.item_wheel_item, parent, false);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    public ArrayList<String>  getList() {
        return list;
    }
}
