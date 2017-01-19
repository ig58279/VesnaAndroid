package ru.cproject.vesnaandroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.activities.stocks.SingleStockActivity;
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
        state = DEFAULT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater lf = LayoutInflater.from(parent.getContext());
        if (viewType == STOCKS_ITEM)
            return new StockViewHolder(lf.inflate(R.layout.item_mini_stock, parent, false));
        else if (viewType == LOADING_ITEM)
            return new StockViewHolder(lf.inflate(R.layout.item_loading, parent, false));
        else if (viewType == ERROR_ITEM)
            return new ErrorViewHolder(lf.inflate(R.layout.item_error_message, parent, false));
        else
            return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ((StockViewHolder) holder).like.setVisibility(View.GONE);
    }

    /*
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) == STOCKS_ITEM) {
            final Stock stock = stocksList.get(position);
            ((StockViewHolder) holder).wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SingleStockActivity.class);
                    intent.putExtra("id", stock.getId());
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                }
            });
            float dpi = context.getResources().getDisplayMetrics().density;
            Picasso
                    .with(context)
                    .load(ServerApi.getImgUrl(stock.getImage(), false))
                    .placeholder(R.drawable.ic_big_placeholder)     // TODO: 17.1.17 выставить фиксированное разрешение
                    .fit()
                    .centerInside()
                    .transform(new RoundedCornersTransformation((int)(4*dpi),0, RoundedCornersTransformation.CornerType.TOP))
                    .into(((StockViewHolder) holder).image);

            ((StockViewHolder) holder).title.setText(stock.getTitle());
            ((StockViewHolder) holder).timestamp.setText(stock.getDate());
            ((StockViewHolder) holder).description.setText(stock.getContent());
            if (stock.isLike()) {
                ((StockViewHolder) holder).like.setImageResource(R.drawable.ic_like);
                ((StockViewHolder) holder).like.setTag(R.drawable.ic_like);
            }
            else {
                ((StockViewHolder) holder).like.setImageResource(R.drawable.dislike);
                ((StockViewHolder) holder).like.setTag(R.drawable.dislike);
            }

            ((StockViewHolder) holder).like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isLike = (int)v.getTag() == R.drawable.ic_like;
                    makeLikeOrDislike(String.valueOf(stock.getId()),!isLike,(ImageButton)v);
                    stock.setLike(!stock.isLike());
                }
            });


            if (stock.isSpecial()) {
                ((StockViewHolder) holder).special.setVisibility(View.VISIBLE);
                ((StockViewHolder) holder).rectangle.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorPrimaryStocks), PorterDuff.Mode.SRC_IN);
                ((StockViewHolder) holder).triangle.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimaryStocks), PorterDuff.Mode.SRC_IN);
            } else
                ((StockViewHolder) holder).special.setVisibility(View.GONE);
        }
        if (getItemViewType(position) == ERROR_ITEM) {
            ((ErrorViewHolder) holder).retry.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            ((ErrorViewHolder) holder).retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    retryInterface.retry();
                }
            });
        }

    }*/
}
