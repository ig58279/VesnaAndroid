package ru.cproject.vesnaandroid.helpers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;

import ru.cproject.vesnaandroid.activities.events.SingleEventActivity;
import ru.cproject.vesnaandroid.activities.stocks.SingleStockActivity;

/**
 * Created by Bitizen on 08.11.16.
 */

public class SlideOnClickListener implements BaseSliderView.OnSliderClickListener {

    private Context context;
    private String type;
    private int id;

    public SlideOnClickListener(Context context, String type, int id) {
        this.context = context;
        this.type = type;
        this.id = id;
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Log.e("slide", "CLICK");
        switch (type){
            case "stocks":
                Intent stocksIntent = new Intent(context, SingleStockActivity.class);
                stocksIntent.putExtra("id", id);
                context.startActivity(stocksIntent);
                break;
            case "events":
                Intent eventIntent = new Intent(context, SingleEventActivity.class);
                eventIntent.putExtra("id", id);
                context.startActivity(eventIntent);
                break;
        }
    }
}
