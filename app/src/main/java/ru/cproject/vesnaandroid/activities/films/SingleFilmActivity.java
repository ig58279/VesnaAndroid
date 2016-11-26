package ru.cproject.vesnaandroid.activities.films;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.activities.universal.ProtoSingleActivity;
import ru.cproject.vesnaandroid.helpers.ResponseParser;
import ru.cproject.vesnaandroid.helpers.TabBar;
import ru.cproject.vesnaandroid.obj.Film;

/**
 * Created by Bitizen on 02.11.16.
 */

public class SingleFilmActivity extends ProtoSingleActivity {
    private static final String TAG = "SingleFilmActivity";

    private SliderLayout slider;
    private PagerIndicator pagerIndicator;

    private View shadow;
    private View background;

    private ImageView poster;
    private TextView title;
    private TextView age;
    private TextView genre;
    private TextView country;
    private TextView rating;
    private TextView seanseTable;

    private TabBar tabBar;
    private TextView[] tabs;
    private View[] tabsView;
    private ViewGroup content;

    private int id;
    private Film film;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent.hasExtra("id"))
            id = intent.getIntExtra("id", 0);
        else
            finish();

        getLayoutInflater().inflate(R.layout.activity_single_film, contentFrame);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Весна");


        shadow = findViewById(R.id.slider_shadow);
        background = findViewById(R.id.background);

        shadow.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        background.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);


        poster = (ImageView) findViewById(R.id.poster);
        title = (TextView) findViewById(R.id.title);
        age = (TextView) findViewById(R.id.age);
        genre = (TextView) findViewById(R.id.genre);
        country = (TextView) findViewById(R.id.country);
        rating = (TextView) findViewById(R.id.rating);
        seanseTable = (TextView) findViewById(R.id.seanse_table);

        tabs = new TextView[]{
                (TextView) findViewById(R.id.info),
                (TextView) findViewById(R.id.description)
        };

        content = (ViewGroup) findViewById(R.id.content_tabs);




        loadFilm();
    }

    private void loadFilm() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id", id);
        // TODO token

        client.get(ServerApi.GET_FILM, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (responseString != null)
                    Log.e(TAG, responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString);
                film = ResponseParser.parseFilm(responseString);
                showInfo();
            }
        });
    }

    private void showInfo() {

        getLayoutInflater().inflate(R.layout.header_slider, headerFrame);

        slider = (SliderLayout) findViewById(R.id.slider);
        pagerIndicator = (PagerIndicator) findViewById(R.id.pager_indicator);

        pagerIndicator.setIndicatorStyleResource(R.drawable.pager_indicator_active, R.drawable.pager_indicator_inactive);
        slider.setCustomIndicator(pagerIndicator);

        for (int i = 0; i < film.getPhotos().size(); i++) {
            DefaultSliderView slide = new DefaultSliderView(this);
            slide
                    .image(film.getPhotos().get(i).getSmall())
                    .setScaleType(BaseSliderView.ScaleType.CenterInside);

            slider.addSlider(slide);
        }
        if (film.getPhotos().size() < 2) {
            pagerIndicator.setVisibility(View.GONE);

            slider.stopAutoCycle();
            slider.setPagerTransformer(false, new BaseTransformer() {@Override protected void onTransform(View view, float position) {}});
        }


        float dpi = getResources().getDisplayMetrics().density;
        Picasso
                .with(this)
                .load(film.getPoster())
                .fit()
                .centerCrop()
                .transform(new RoundedCornersTransformation((int)(8*dpi), 0))
                .into(poster);

        title.setText(film.getName());
        age.setText(film.getAge() + "+");
        if (film.getGenre().size() != 0) {
            String genre = film.getGenre().get(0);
            for (int i = 1; i < film.getGenre().size(); i++)
                genre += ", " + film.getGenre().get(i);

            this.genre.setText(genre);
        }

        if (film.getCountry().size() != 0) {
            String country = film.getCountry().get(0);
            for (int i = 1; i < film.getCountry().size(); i++)
                country += ", " + film.getCountry().get(i);

            this.country.setText(country);
        }

        rating.setText(film.getRating() + " на КиноПоиске");
        if (film.getRating() >= 7f)
            rating.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorHighRating), PorterDuff.Mode.SRC_IN);
        else if (film.getRating() >= 5f)
            rating.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorMiddleRating), PorterDuff.Mode.SRC_IN);
        else
            rating.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorLowRating), PorterDuff.Mode.SRC_IN);


        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;
        String seanses = "";
        boolean span = false;
        int spanFuture = 0;
        Date date = new Date();
        for (int i = 0; i < film.getSeanse().size(); i++) {
            long seanse = film.getSeanse().get(i);
            Calendar calendar = Calendar.getInstance();
            if (new Date(seanse).after(date) && !span) {
                spanFuture = seanses.length() - 1;
                span = true;
            }
            calendar.setTimeInMillis(seanse);
            String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
            if (hour.length() == 1)
                hour = "0" + hour;
            String minute = String.valueOf(calendar.get(Calendar.MINUTE));
            if (minute.length() == 1)
                minute = "0" + minute;
            seanses += hour + ":" + minute + "   ";
        }
        SpannableString string = new SpannableString(seanses);
        if (span)
            string.setSpan(new ForegroundColorSpan(color), spanFuture, seanses.length(), 0);
        seanseTable.setText(string);

        View filmInfo = getLayoutInflater().inflate(R.layout.tab_film_info, content, false);
        ViewGroup director = (ViewGroup) filmInfo.findViewById(R.id.director);
        if (film.getDirector() != null && film.getDirector().size() != 0) {
            TextView directorsNames = (TextView) director.findViewById(R.id.director_names);
            String directors = film.getDirector().get(0);

            for (int i = 1; i < film.getDirector().size(); i++)
                directors += ", " + film.getDirector().get(i);

            directorsNames.setText(directors);
        } else
            director.setVisibility(View.GONE);

        ViewGroup producer = (ViewGroup) filmInfo.findViewById(R.id.producer);
        if (film.getProducer() != null && film.getProducer().size() != 0) {
            TextView producersNames = (TextView) filmInfo.findViewById(R.id.producer_names);
            String producers = film.getProducer().get(0);

            for (int i = 1; i < film.getProducer().size(); i++)
                producers += ", " + film.getProducer().get(i);

            producersNames.setText(producers);
        } else
            producer.setVisibility(View.GONE);

        ViewGroup actors = (ViewGroup) filmInfo.findViewById(R.id.actors);
        if (film.getCast() != null && film.getCast().size() != 0) {
            TextView actorsNames = (TextView) filmInfo.findViewById(R.id.actors_names);
            String actrosString = film.getCast().get(0);

            for (int i = 1; i < film.getCast().size(); i++)
                actrosString += ", "  + film.getCast().get(i);

            actorsNames.setText(actrosString);
        } else
            actors.setVisibility(View.GONE);

        ViewGroup durationView = (ViewGroup) filmInfo.findViewById(R.id.length);
        if (film.getDuration() != 0) {
            TextView duration = (TextView) filmInfo.findViewById(R.id.length_view);
            duration.setText( (film.getDuration() / 60)  + " ч " + film.getDuration() % 60 + " мин");
        } else
            durationView.setVisibility(View.GONE);


        View description = getLayoutInflater().inflate(R.layout.tab_film_description, content, false);
        TextView textView = (TextView) description.findViewById(R.id.description);
        textView.setText(film.getContent());
        tabsView = new View[]{
                filmInfo,
                description
        };

        tabBar = new TabBar(tabs, tabsView, content, color, ContextCompat.getColor(this, R.color.colorTextGray));
    }
}
