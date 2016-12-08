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
import android.widget.ImageView;
import android.widget.TextView;

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
 * Created by Bitizen on 27.10.16.
 */

public class StocksAdapter extends RecyclerView.Adapter {

    protected final int STOCKS_ITEM = 0;
    protected final int LOADING_ITEM = 1;
    protected final int ERROR_ITEM = 2;

    protected int state = 1;
    public static final int DEFAULT = 0;
    public static final int LOADING = 1;
    public static final int ERROR = 2;

    private Context context;
    private List<Stock> stocksList;
    protected int color;

    private RetryInterface retryInterface;

    private boolean showLoader = false;

    public StocksAdapter(Context context, List<Stock> stocksList, int color, RetryInterface retryInterface) {
        this.context = context;
        this.stocksList = stocksList;
        this.color = color;
        this.retryInterface = retryInterface;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater lf = LayoutInflater.from(parent.getContext());
        if (viewType == STOCKS_ITEM)
            return new StocksAdapter.StockViewHolder(lf.inflate(R.layout.item_stock, parent, false));
        else if (viewType == LOADING_ITEM)
            return new StocksAdapter.StockViewHolder(lf.inflate(R.layout.item_loading, parent, false));
        else if (viewType == ERROR_ITEM)
            return new ErrorViewHolder(lf.inflate(R.layout.item_error_message, parent, false));
        else
            return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == STOCKS_ITEM) {
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
                    .load(ServerApi.getImgUrl(stock.getImage(), false))
                    .placeholder(R.drawable.ic_big_placeholder)
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
    }

    @Override
    public int getItemViewType(int position) {
        if (position != getItemCount() - 1)
            return STOCKS_ITEM;
        else {
            if (state == ERROR)
                return ERROR_ITEM;
            else if (state == LOADING)
                return LOADING_ITEM;
            else
                return STOCKS_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        if (state == DEFAULT)
            return stocksList.size();
        else
            return stocksList.size() + 1;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    protected class StockViewHolder extends RecyclerView.ViewHolder {

        ViewGroup wrapper;
        ViewGroup special;
        ImageView rectangle;
        ImageView triangle;
        ImageView image;
        TextView title;
        TextView timestamp;
        TextView description;
        ImageButton like;

        public StockViewHolder(View itemView) {
            super(itemView);
            wrapper = (ViewGroup) itemView.findViewById(R.id.wrapper);
            special = (ViewGroup) itemView.findViewById(R.id.special);
            rectangle = (ImageView) itemView.findViewById(R.id.rectangle);
            triangle = (ImageView) itemView.findViewById(R.id.triangle);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            description = (TextView) itemView.findViewById(R.id.description);
            like = (ImageButton) itemView.findViewById(R.id.like);
        }
    }
}
