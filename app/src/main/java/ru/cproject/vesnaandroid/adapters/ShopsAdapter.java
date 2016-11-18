package ru.cproject.vesnaandroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.activities.shops.SingleShopActivity;
import ru.cproject.vesnaandroid.obj.Category;
import ru.cproject.vesnaandroid.obj.Shop;

/**
 * Created by Bitizen on 29.10.16.
 */

public class ShopsAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Shop> shops;
    private int color;
    private int style;

    public ShopsAdapter(Context context, List<Shop> shops, int color, int style) {
        this.context = context;
        this.shops = shops;
        this.color = color;
        this.style = style;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater lf = LayoutInflater.from(parent.getContext());
        return new ShopViewHolder(lf.inflate(R.layout.item_shop, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Shop shop = shops.get(position);
        ((ShopViewHolder) holder).name.getBackground().setColorFilter(Color.parseColor("#f4f4f4"), PorterDuff.Mode.SRC_IN);
        ((ShopViewHolder) holder).name.setText(shop.getName());
        ((ShopViewHolder) holder).showOnMap.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        Log.e("logo", shop.getLogo());

        Picasso
                .with(context)
                .load(shop.getLogo())
                .fit()
                .centerInside()
                .into(((ShopViewHolder) holder).logo);

        ((ShopViewHolder) holder).wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SingleShopActivity.class);
                intent.putExtra("id", shop.getId());
                intent.putExtra("style", style);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shops.size();
    }

    private class ShopViewHolder extends RecyclerView.ViewHolder {

        ViewGroup wrapper;
        ImageView logo;
        TextView name;
        ImageView showOnMap;

        public ShopViewHolder(View itemView) {
            super(itemView);
            wrapper = (ViewGroup) itemView.findViewById(R.id.wrapper);
            logo = (ImageView) itemView.findViewById(R.id.logo);
            name = (TextView) itemView.findViewById(R.id.name);
            showOnMap = (ImageView) itemView.findViewById(R.id.show_on_map);
        }
    }
}
