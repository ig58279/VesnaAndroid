package ru.cproject.vesnaandroid.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;

import ru.cproject.vesnaandroid.R;

/**
 * Created by Bitizen on 18.01.17.
 */

public class TrailerSlide extends BaseSliderView {

    public TrailerSlide(Context context) {
        super(context);
    }

    @Override
    public View getView() {
        LayoutInflater lf = LayoutInflater.from(getContext());
        View view = lf.inflate(R.layout.slide_trailer, null);
        ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        bindEventAndShow(view, thumbnail);
        return view;
    }
}
