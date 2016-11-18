package ru.cproject.vesnaandroid.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.adapters.holders.LoadingViewHolder;
import ru.cproject.vesnaandroid.obj.Stock;

/**
 * Created by Bitizen on 01.11.16.
 */

public class MiniStocksAdapter extends StocksAdapter {

    public MiniStocksAdapter(Context context, List<Stock> stocksList) {
        super(context, stocksList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater lf = LayoutInflater.from(parent.getContext());
        if (viewType == ITEM)
            return new StockViewHolder(lf.inflate(R.layout.item_mini_stock, parent, false));
        if (viewType == LOADER)
            return new LoadingViewHolder(lf.inflate(R.layout.item_loading, parent, false));
        return null;
    }
}
