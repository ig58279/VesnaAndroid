package ru.cproject.vesnaandroid.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import ru.cproject.vesnaandroid.R;

/**
 * Created by Bitizen on 04.11.16.
 */

public class CardSlide extends BaseSliderView {

    private String url;

    public CardSlide(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    public View getView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.slide_card, null);
        ImageView image = (ImageView) v.findViewById(R.id.image);
        float dpi = getContext().getResources().getDisplayMetrics().density;
        Picasso
                .with(getContext())
                .load(url)
                .fit()
                .centerCrop()
                .transform(new RoundedCornersTransformation((int)(4*dpi), 0))
                .into(image);
        bindEventAndShow(v, image);
        return v;
    }
}
