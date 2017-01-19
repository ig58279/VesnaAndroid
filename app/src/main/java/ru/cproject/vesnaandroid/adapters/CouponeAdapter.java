package ru.cproject.vesnaandroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
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
import ru.cproject.vesnaandroid.adapters.holders.ErrorViewHolder;
import ru.cproject.vesnaandroid.helpers.RetryInterface;
import ru.cproject.vesnaandroid.obj.Coupon;

/**
 * Created by andro on 19.01.2017.
 */

public class CouponeAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Coupon> couponList;
    protected int color;

    public CouponeAdapter(Context context, List<Coupon> couponList, int color) {
        this.context = context;
        this.couponList = couponList;
        this.color = color;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater lf = LayoutInflater.from(parent.getContext());
        return new CouponeAdapter.CouponViewHolder(lf.inflate(R.layout.item_coupone, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Coupon coupon = couponList.get(position);
        float dpi = context.getResources().getDisplayMetrics().density;
        Picasso
                .with(context)
                .load(ServerApi.getImgUrl(coupon.getImage(), false))
                .placeholder(R.drawable.ic_small_placeholder)
                .fit()
                .centerInside()
                .transform(new RoundedCornersTransformation((int)(4*dpi),0, RoundedCornersTransformation.CornerType.TOP))
                .into(((CouponViewHolder) holder).image);

        ((CouponViewHolder) holder).name.setText(coupon.getName());
    }

    @Override
    public int getItemCount() {
        return couponList.size();
    }

    protected class CouponViewHolder extends RecyclerView.ViewHolder {

        ViewGroup wrapper;
        ImageView image;
        TextView name;

        public CouponViewHolder(View itemView) {
            super(itemView);
            wrapper = (ViewGroup) itemView.findViewById(R.id.wrapper);
            image = (ImageView) itemView.findViewById(R.id.image);
            name = (TextView) itemView.findViewById(R.id.name);
        }
    }
}
