package ru.cproject.vesnaandroid.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.Transformers.ForegroundToBackgroundTransformer;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.Settings;
import ru.cproject.vesnaandroid.activities.account.AccountActivity;
import ru.cproject.vesnaandroid.activities.account.LoginActivity;
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
    private TextView accountNameTextView;
    private TextView toCabinetTextView;
    private TextView myCouponsTextView;
    private ImageView info;
    private ImageView search;
    private ViewGroup login;
    private SliderLayout slider;
    private PagerIndicator pagerIndicator;
    private TableLayout menu;
    private LinearLayout accountLinearLayout;
    private ImageView photoAccountImageView;

    private MallInfo mall;
    private Show[] shows;

    private SharedPreferences settingsSharedPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences mallInfo = getSharedPreferences(Settings.MALL_INFO, MODE_PRIVATE);
        settingsSharedPrefs = getSharedPreferences(Settings.REGISTRATION_INFO, MODE_PRIVATE);
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
        toCabinetTextView = (TextView) findViewById(R.id.accountToCabinetTextView);
        accountNameTextView = (TextView) findViewById(R.id.accountNameTextView);
        accountLinearLayout = (LinearLayout) findViewById(R.id.account);
        photoAccountImageView = (ImageView) findViewById(R.id.photoAccountImageView);
        myCouponsTextView = (TextView) findViewById(R.id.myCouponsAccountTextView);

        pagerIndicator.setIndicatorStyleResource(R.drawable.pager_indicator_active, R.drawable.pager_indicator_inactive);
        slider.setCustomIndicator(pagerIndicator);
        slider.setPagerTransformer(false, new ForegroundToBackgroundTransformer());

        try {
            for (Show show : shows) {
                CardSlide slide = new CardSlide(this, ServerApi.getImgUrl(show.getImage(), false));
                slide.setOnSliderClickListener(new SlideOnClickListener(this, show.getType(), show.getId()));
                slider.addSlider(slide);
            }
        }catch(Exception e){
            shows = new Show[0];
        }

        worktime.setText(mall.getMode());

        ViewCreatorHelper.createMenu(this, menu);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TcInfoActivity.class));
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        toCabinetTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AccountActivity.class));
            }
        });
        myCouponsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void requestMapInfo() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(settingsSharedPrefs.contains(Settings.RegistrationInfo.ID) && settingsSharedPrefs.contains(Settings.RegistrationInfo.NAME)){
            accountLinearLayout.setVisibility(View.VISIBLE);
            accountNameTextView.setText(settingsSharedPrefs.getString(Settings.RegistrationInfo.NAME,""));
            login.setVisibility(View.GONE);
            final int radius = 16;
            final int margin = 0;
            final Transformation transformation = new RoundedCornersTransformation(radius, margin, RoundedCornersTransformation.CornerType.LEFT);
            if(settingsSharedPrefs.contains(Settings.RegistrationInfo.PHOTO)){
                Picasso.with(MainActivity.this).load(settingsSharedPrefs.getString(Settings.RegistrationInfo.PHOTO,""))
                        .resize(200,200)
                        .transform(transformation)
                        .error(getBaseContext().getResources().getDrawable(R.drawable.ic_small_placeholder))
                        .into(photoAccountImageView);
            }else{
                Picasso.with(MainActivity.this).load(R.drawable.ic_small_placeholder)
                        .resize(200,200)
                        .transform(transformation)
                        .into(photoAccountImageView);
            }
        }
    }
}
