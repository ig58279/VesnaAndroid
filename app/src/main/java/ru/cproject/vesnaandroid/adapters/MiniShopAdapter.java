package ru.cproject.vesnaandroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.activities.shops.SingleShopActivity;
import ru.cproject.vesnaandroid.adapters.holders.ErrorViewHolder;
import ru.cproject.vesnaandroid.helpers.RetryInterface;
import ru.cproject.vesnaandroid.obj.Shop;
import ru.cproject.vesnaandroid.obj.Stock;

/**
 * Created by Bitizen on 01.11.16.
 */

public class MiniShopAdapter extends ShopsAdapter {

    public MiniShopAdapter(Context context, List<Shop> shopsList, int color, int style) {
        super(context, shopsList, color, style);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater lf = LayoutInflater.from(parent.getContext());
        return new ShopViewHolder(lf.inflate(R.layout.item_account_shop, parent, false));
    }

    private class ShopViewHolder extends RecyclerView.ViewHolder {

        ViewGroup wrapper;
        ImageView image;
        TextView title;
        ImageButton like;

        public ShopViewHolder(View itemView) {
            super(itemView);
            wrapper = (ViewGroup) itemView.findViewById(R.id.wrapper);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            like = (ImageButton) itemView.findViewById(R.id.like);
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final Shop shop = shops.get(position);
     //   ((ShopViewHolder) holder).title.getBackground().setColorFilter(Color.parseColor("#f4f4f4"), PorterDuff.Mode.SRC_IN);
        ((ShopViewHolder) holder).title.setText(shop.getName());
        float dpi = context.getResources().getDisplayMetrics().density;
        Picasso
                .with(context)
                .load(ServerApi.getImgUrl(shop.getLogo(), false))
                .placeholder(R.drawable.ic_big_placeholder)
                .fit()
                .centerInside()
                .transform(new RoundedCornersTransformation((int)(4*dpi),0, RoundedCornersTransformation.CornerType.TOP))
                .into(((ShopViewHolder) holder).image);

        ((ShopViewHolder) holder).image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ((ShopViewHolder) holder).wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SingleShopActivity.class);
                intent.putExtra("id", shop.getId());
                intent.putExtra("style", style);
                context.startActivity(intent);
            }
        });

        ((ShopViewHolder) holder).like.setVisibility(View.GONE);
         /*
        if (getItemViewType(position) == STOCKS_ITEM) {
            final Shop shop = shops.get(position);
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
                    .transform(new RoundedCornersTransformation((int) (4 * dpi), 0, RoundedCornersTransformation.CornerType.TOP))
                    .into(((StockViewHolder) holder).image);

            ((StockViewHolder) holder).title.setText(stock.getTitle());
            ((StockViewHolder) holder).timestamp.setText(stock.getDate());
            ((StockViewHolder) holder).description.setText(stock.getContent());
            if (stock.isLike()) {
                ((StockViewHolder) holder).like.setImageResource(R.drawable.ic_like);
                ((StockViewHolder) holder).like.setTag(R.drawable.ic_like);
            } else {
                ((StockViewHolder) holder).like.setImageResource(R.drawable.dislike);
                ((StockViewHolder) holder).like.setTag(R.drawable.dislike);
            }

            ((StockViewHolder) holder).like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isLike = (int) v.getTag() == R.drawable.ic_like;
                    makeLikeOrDislike(String.valueOf(stock.getId()), !isLike, (ImageButton) v);
                    stock.setLike(!stock.isLike());
                }
            });
        }*/
    }
}
