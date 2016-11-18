package ru.cproject.vesnaandroid.helpers;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import ru.cproject.vesnaandroid.activities.stocks.SingleStockActivity;

/**
 * Created by Bitizen on 08.11.16.
 */

public class SlideOnClickListener implements View.OnClickListener {

    private Context context;
    private String type;
    private int id;

    public SlideOnClickListener(Context context, String type, int id) {
        this.context = context;
        this.type = type;
        this.id = id;
    }

    @Override
    public void onClick(View view) {
        switch (type){
            // TODO отработать все типы
            case "stock":
                Intent intent = new Intent(context, SingleStockActivity.class);
                intent.putExtra("id", id);
                context.startActivity(intent);
                break;
            case "event":
                break;
        }
    }
}
