package ru.cproject.vesnaandroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.activities.events.SingleEventActivity;
import ru.cproject.vesnaandroid.activities.films.SingleFilmActivity;
import ru.cproject.vesnaandroid.activities.shops.SingleShopActivity;
import ru.cproject.vesnaandroid.activities.stocks.SingleStockActivity;
import ru.cproject.vesnaandroid.adapters.holders.ErrorViewHolder;
import ru.cproject.vesnaandroid.obj.Search;



/**
 * Created by andro on 22.11.2016.
 */

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int SEARCH_ITEM = 0;
    private final int LOADING_ITEM = 1;
    private final int ERROR_ITEM = 2;

    private Context context;
    private List<Search> list;
    private int color;

    private int state = 0;
    public static final int DEFAULT = 0;
    public static final int LOADING = 1;
    public static final int ERROR = 2;

    private int[] colors;

    public SearchAdapter(Context context, List<Search> list, int color) {
        this.context = context;
        this.list = list;
        this.color = color;

        colors = new int[]{
                ContextCompat.getColor(context, R.color.colorPrimaryShops),  // магазины
                ContextCompat.getColor(context, R.color.colorPrimaryEvents), // события
                ContextCompat.getColor(context, R.color.colorPrimaryStocks), // акции
                ContextCompat.getColor(context, R.color.colorPrimaryCinema)  // фильмы
        };
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater lf = LayoutInflater.from(parent.getContext());
        if (viewType == SEARCH_ITEM)
            return new SearchViewHolder(lf.inflate(R.layout.item_search, parent, false));
        else if (viewType == LOADING_ITEM)
            return new SearchViewHolder(lf.inflate(R.layout.item_loading, parent, false));
        else if (viewType == ERROR_ITEM)
            return new ErrorViewHolder(lf.inflate(R.layout.item_error_message, parent, false));
        else
            return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == SEARCH_ITEM) {
            final Search search = list.get(position);

            float dpi = context.getResources().getDisplayMetrics().density;
            Picasso.with(context)
                    .load(ServerApi.getImgUrl(search.getImageURL(), true))
                    .placeholder(R.drawable.ic_small_placeholder)
                    .fit()
                    .centerInside()
                    .transform(new RoundedCornersTransformation((int) (2 * dpi), 0))
                    .into(((SearchViewHolder) holder).image);

            Log.e("id", String.valueOf(search.getId()));

            switch (search.getType()) {
                case "shops":
                    ((SearchViewHolder) holder).type.setText("Магазин");
                    ((SearchViewHolder) holder).shape.setColorFilter(colors[0], PorterDuff.Mode.SRC_IN);
                    ((SearchViewHolder) holder).wrapper.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, SingleShopActivity.class);
                            intent.putExtra("id", search.getId());
                            intent.putExtra("style", R.style.ShopsTheme);
                            context.startActivity(intent);
                        }
                    });
                    break;
                case "events":
                    ((SearchViewHolder) holder).type.setText("События");
                    ((SearchViewHolder) holder).shape.setColorFilter(colors[1], PorterDuff.Mode.SRC_IN);
                    ((SearchViewHolder) holder).wrapper.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, SingleEventActivity.class);
                            intent.putExtra("id", search.getId());
                            context.startActivity(intent);
                        }
                    });
                    break;
                case "stocks":
                    ((SearchViewHolder) holder).type.setText("Акции");
                    ((SearchViewHolder) holder).shape.setColorFilter(colors[2], PorterDuff.Mode.SRC_IN);
                    ((SearchViewHolder) holder).wrapper.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, SingleStockActivity.class);
                            intent.putExtra("id", search.getId());
                            context.startActivity(intent);
                        }
                    });
                    break;
                case "films":
                    ((SearchViewHolder) holder).type.setText("Фильмы");
                    ((SearchViewHolder) holder).shape.setColorFilter(colors[3], PorterDuff.Mode.SRC_IN);
                    ((SearchViewHolder) holder).wrapper.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, SingleFilmActivity.class);
                            intent.putExtra("id", search.getId());
                            context.startActivity(intent);
                        }
                    });
                    break;
            }
            ((SearchViewHolder) holder).name.setText(search.getName());
        }
        if (getItemViewType(position) == ERROR_ITEM) {
            ((ErrorViewHolder) holder).retry.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            ((ErrorViewHolder) holder).retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (state == DEFAULT)
            return list.size();
        else
            return list.size() + 1;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public int getItemViewType(int position) {
        if (position != getItemCount() - 1)
            return SEARCH_ITEM;
        else {
            if (state == ERROR)
                return ERROR_ITEM;
            else if (state == LOADING)
                return LOADING_ITEM;
            else
                return SEARCH_ITEM;
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder {
        ViewGroup wrapper;
        ImageView image;
        TextView type;
        TextView name;
        ImageView shape;

        public SearchViewHolder(View itemView) {
            super(itemView);

            wrapper = (ViewGroup) itemView.findViewById(R.id.wrapper);
            image = (ImageView) itemView.findViewById(R.id.image);
            type = (TextView) itemView.findViewById(R.id.type);
            name = (TextView) itemView.findViewById(R.id.name);
            shape = (ImageView) itemView.findViewById(R.id.shape);
        }
    }
}
