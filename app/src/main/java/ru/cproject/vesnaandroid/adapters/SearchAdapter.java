package ru.cproject.vesnaandroid.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
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
import ru.cproject.vesnaandroid.obj.Search;

/**
 * Created by andro on 22.11.2016.
 */

public class SearchAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Search> list;

    private int[] colors;

    public SearchAdapter(Context context, List<Search> list) {
        this.context = context;
        this.list = list;

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
        return new SearchViewHolder(lf.inflate(R.layout.item_search, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Search search = list.get(position);

        float dpi = context.getResources().getDisplayMetrics().density;
        Picasso.with(context)
                .load(ServerApi.getImgUrl(search.getImageURL(), true))
                .fit()
                .centerInside()
                .transform(new RoundedCornersTransformation((int)(2*dpi), 0))
                .into(((SearchViewHolder) holder).image);

        switch (search.getType()) {
            case "shops":
                ((SearchViewHolder) holder).type.setText("Магазин");
                ((SearchViewHolder) holder).shape.setColorFilter(colors[0], PorterDuff.Mode.SRC_IN);
                break;
            case "events":
                ((SearchViewHolder) holder).type.setText("События");
                ((SearchViewHolder) holder).shape.setColorFilter(colors[1], PorterDuff.Mode.SRC_IN);
                break;
            case "stocks":
                ((SearchViewHolder) holder).type.setText("Акции");
                ((SearchViewHolder) holder).shape.setColorFilter(colors[2], PorterDuff.Mode.SRC_IN);
                break;
            case "films":
                ((SearchViewHolder) holder).type.setText("Фильмы");
                ((SearchViewHolder) holder).shape.setColorFilter(colors[3], PorterDuff.Mode.SRC_IN);
                break;
        }
        ((SearchViewHolder) holder).name.setText(search.getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView type;
        TextView name;
        ImageView shape;

        public SearchViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.image);
            type = (TextView) itemView.findViewById(R.id.type);
            name = (TextView) itemView.findViewById(R.id.name);
            shape = (ImageView) itemView.findViewById(R.id.shape);
        }
    }
}
