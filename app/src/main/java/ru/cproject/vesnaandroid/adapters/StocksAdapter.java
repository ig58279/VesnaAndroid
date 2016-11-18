package ru.cproject.vesnaandroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.activities.stocks.SingleStockActivity;
import ru.cproject.vesnaandroid.adapters.holders.LoadingViewHolder;
import ru.cproject.vesnaandroid.obj.Stock;

/**
 * Created by Bitizen on 27.10.16.
 */

public class StocksAdapter extends RecyclerView.Adapter {

    protected static final int ITEM = 0;
    protected static final int LOADER = 1;

    private Context context;
    private List<Stock> stocksList;

    private boolean showLoader = false;

    public StocksAdapter(Context context, List<Stock> stocksList) {
        this.context = context;
        this.stocksList = stocksList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater lf = LayoutInflater.from(parent.getContext());
        if (viewType == ITEM)
            return new StockViewHolder(lf.inflate(R.layout.item_stock, parent, false));
        if (viewType == LOADER)
            return new LoadingViewHolder(lf.inflate(R.layout.item_loading, parent, false));
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == ITEM) {
            final Stock stock = stocksList.get(position);
            ((StockViewHolder) holder).wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SingleStockActivity.class);
                    intent.putExtra("id", stock.getId());
                    context.startActivity(intent);
                }
            });
            float dpi = context.getResources().getDisplayMetrics().density;
            Picasso
                    .with(context)
                    .load(stock.getImage())
                    .fit()
                    .centerCrop()
                    .transform(new RoundedCornersTransformation((int)(4*dpi),0, RoundedCornersTransformation.CornerType.TOP))
                    .into(((StockViewHolder) holder).image);

            ((StockViewHolder) holder).title.setText(stock.getTitle());
            ((StockViewHolder) holder).timestamp.setText(stock.getDate());
            ((StockViewHolder) holder).description.setText(stock.getContent());
            if (stock.isLike())
                ((StockViewHolder) holder).like.setImageResource(R.drawable.ic_like);
            else
                ((StockViewHolder) holder).like.setImageResource(R.drawable.dislike);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (showLoader && position == getItemCount() + 1)
                return LOADER;
        return ITEM;
    }

    @Override
    public int getItemCount() {
        if (showLoader)
            return stocksList.size() + 1;
        else
            return stocksList.size();
    }

    protected class StockViewHolder extends RecyclerView.ViewHolder {

        ViewGroup wrapper;
        ImageView image;
        TextView title;
        TextView timestamp;
        TextView description;
        ImageButton like;

        StockViewHolder(View itemView) {
            super(itemView);
            wrapper = (ViewGroup) itemView.findViewById(R.id.wrapper);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            description = (TextView) itemView.findViewById(R.id.description);
            like = (ImageButton) itemView.findViewById(R.id.like);
        }
    }
}
