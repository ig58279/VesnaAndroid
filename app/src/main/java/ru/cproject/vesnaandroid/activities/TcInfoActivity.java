package ru.cproject.vesnaandroid.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.gson.Gson;

import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.Settings;
import ru.cproject.vesnaandroid.activities.universal.ProtoSingleActivity;
import ru.cproject.vesnaandroid.obj.mall.Link;
import ru.cproject.vesnaandroid.obj.mall.MallInfo;
import ru.cproject.vesnaandroid.obj.mall.ShopMode;

/**
 * Created by Bitizen on 01.11.16.
 */

public class TcInfoActivity extends ProtoSingleActivity {

    private SliderLayout slider;
    private PagerIndicator pagerIndicator;

    private LinearLayout workTimes;
    private TextView phone;
    private TextView site;
    private LinearLayout social;
    private TextView address;
    private Button makeRoute;
    private TextView description;

    private MallInfo mall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.header_slider, headerFrame);
        getLayoutInflater().inflate(R.layout.activity_tc_info, contentFrame);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Весна");

        SharedPreferences mallInfo = getSharedPreferences(Settings.MALL_INFO, MODE_PRIVATE);
        Gson gson = new Gson();
        mall = gson.fromJson(mallInfo.getString(Settings.MallInfo.MALL, ""), MallInfo.class);

        slider = (SliderLayout) findViewById(R.id.slider);
        pagerIndicator = (PagerIndicator) findViewById(R.id.pager_indicator);
        workTimes = (LinearLayout) findViewById(R.id.work_times);
        phone = (TextView) findViewById(R.id.phone);
        site = (TextView) findViewById(R.id.site);
        social = (LinearLayout) findViewById(R.id.social);
        address = (TextView) findViewById(R.id.address);
        makeRoute = (Button) findViewById(R.id.route);
        description = (TextView) findViewById(R.id.description);

        pagerIndicator.setIndicatorStyleResource(R.drawable.pager_indicator_active, R.drawable.pager_indicator_inactive);
        slider.setCustomIndicator(pagerIndicator);

        for (String s : mall.getPhotos()) {
            DefaultSliderView slide = new DefaultSliderView(this);
            slide.image(ServerApi.getImgUrl(s, false));
            slider.addSlider(slide);
        }

        View tcWorktime = getLayoutInflater().inflate(R.layout.info_worktime, workTimes, false);
        workTimes.addView(tcWorktime);
        for (ShopMode shopMode : mall.getShopsModes()) {
            View v = getLayoutInflater().inflate(R.layout.info_worktime, workTimes, false);
            TextView name = (TextView) v.findViewById(R.id.name);
            name.setText(shopMode.getName());
            TextView worktime = (TextView) v.findViewById(R.id.worktime);
            worktime.setText(shopMode.getMode());
            workTimes.addView(v);
        }

        phone.setText(mall.getPhone());

        for (Link link : mall.getLinks()) {
            Log.e("type", link.getType());
            switch (link.getType()){
                case "site":
                    site.setText(link.getParametr());
                    break;
                // TODO: 08.11.16 другие типы
            }
        }

        address.setText(mall.getAddress().getTitle());
        // TODO: 08.11.16 координаты

        description.setText(mall.getContent());
    }
}
