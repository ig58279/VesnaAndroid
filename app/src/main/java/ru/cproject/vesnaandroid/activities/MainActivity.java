package ru.cproject.vesnaandroid.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.Transformers.ForegroundToBackgroundTransformer;
import com.google.gson.Gson;

import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.Settings;
import ru.cproject.vesnaandroid.activities.login.LoginActivity;
import ru.cproject.vesnaandroid.helpers.CardSlide;
import ru.cproject.vesnaandroid.helpers.SlideOnClickListener;
import ru.cproject.vesnaandroid.helpers.ViewCreatorHelper;
import ru.cproject.vesnaandroid.obj.Show;
import ru.cproject.vesnaandroid.obj.mall.MallInfo;

/**
 * Created by Bitizen on 25.10.16.
 */

public class MainActivity extends AppCompatActivity {

    private TextView worktime;
    private ImageView info;
    private ImageView search;
    private ViewGroup login;
    private SliderLayout slider;
    private PagerIndicator pagerIndicator;
    private TableLayout menu;


    private MallInfo mall;
    private Show[] shows;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences mallInfo = getSharedPreferences(Settings.MALL_INFO, MODE_PRIVATE);
        Gson gson = new Gson();
        mall = gson.fromJson(mallInfo.getString(Settings.MallInfo.MALL, ""), MallInfo.class);
        shows = gson.fromJson(mallInfo.getString(Settings.MallInfo.SHOWS, ""), Show[].class);

        slider = (SliderLayout) findViewById(R.id.slider);
        pagerIndicator = (PagerIndicator) findViewById(R.id.pager_indicator);
        worktime = (TextView) findViewById(R.id.work_time);
        search = (ImageView) findViewById(R.id.search);
        login = (ViewGroup) findViewById(R.id.login);
        menu = (TableLayout) findViewById(R.id.menu);
        info = (ImageView) findViewById(R.id.info);
        search = (ImageView) findViewById(R.id.search);

        pagerIndicator.setIndicatorStyleResource(R.drawable.pager_indicator_active, R.drawable.pager_indicator_inactive);
        slider.setCustomIndicator(pagerIndicator);
        slider.setPagerTransformer(false, new ForegroundToBackgroundTransformer());

        for (Show show : shows) {
            CardSlide slide = new CardSlide(this, show.getImage());
            slider.addSlider(slide);
            slider.setOnClickListener(new SlideOnClickListener(this, show.getType(), show.getId()));
        }

        worktime.setText(mall.getMode());

        ViewCreatorHelper.createMenu(this, menu);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TcInfoActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }
}
