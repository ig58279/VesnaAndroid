package ru.cproject.vesnaandroid.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.adapters.holders.ErrorViewHolder;
import ru.cproject.vesnaandroid.adapters.holders.LoadingViewHolder;
import ru.cproject.vesnaandroid.helpers.RetryInterface;
import ru.cproject.vesnaandroid.obj.Stock;

/**
 * Created by Bitizen on 01.11.16.
 */

public class MiniStocksAdapter extends StocksAdapter {

    public MiniStocksAdapter(Context context, List<Stock> stocksList, int color, RetryInterface retryInterface) {
        super(context, stocksList, color, retryInterface);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater lf = LayoutInflater.from(parent.getContext());
        if (viewType == STOCKS_ITEM)
            return new StockViewHolder(lf.inflate(R.layout.item_stock, parent, false));
        else if (viewType == LOADING_ITEM)
            return new StockViewHolder(lf.inflate(R.layout.item_loading, parent, false));
        else if (viewType == ERROR_ITEM)
            return new ErrorViewHolder(lf.inflate(R.layout.item_error_message, parent, false));
        else
            return null;
    }
}
