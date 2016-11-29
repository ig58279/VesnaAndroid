package ru.cproject.vesnaandroid.activities.events;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.activities.universal.ProtoSingleActivity;
import ru.cproject.vesnaandroid.helpers.ResponseParser;
import ru.cproject.vesnaandroid.obj.Event;

/**
 * Created by Bitizen on 09.11.16.
 */

public class SingleEventActivity extends ProtoSingleActivity {
    private static final String TAG = "SingleEventActivity";

    private SliderLayout slider;
    private PagerIndicator pagerIndicator;

    private ViewGroup progress;
    private ViewGroup errorMessage;
        private Button retry;
    private ViewGroup contentView;

    private TextView title;
    private TextView timestamp;
    private TextView description;

    private int id;
    private Event event;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent.hasExtra("id")){
            id = intent.getIntExtra("id", 0);
        } else {
            finish();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getLayoutInflater().inflate(R.layout.activity_single_event, contentFrame);

        progress = (ViewGroup) findViewById(R.id.progress);
        errorMessage = (ViewGroup) findViewById(R.id.error_message);
        retry = (Button) findViewById(R.id.retry);
        retry.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);//TODO изменить для версий до 21
        contentView = (ViewGroup) findViewById(R.id.content_view);

        title = (TextView) findViewById(R.id.title);
        timestamp = (TextView) findViewById(R.id.timestamp);
        description = (TextView) findViewById(R.id.description);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorMessage.setVisibility(View.GONE);
                contentView.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                loadEvent();
            }
        });

        loadEvent();
    }

    private void loadEvent() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            boolean isConnected = activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("id", id);

                client.get(ServerApi.GET_EVENT, params, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        SingleEventActivity.this.onFailure(responseString);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.d(TAG, responseString);
                        event = ResponseParser.parseEvent(responseString);
                        showInfo();
                    }
                });
            } else
                onFailure(null);
        } else
            onFailure(null);
    }

    private void onFailure(@Nullable String responseString) {
        if (responseString != null)
            Log.e(TAG, responseString);
        errorMessage.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        contentView.setVisibility(View.GONE);
    }

    private void showInfo() {
        getSupportActionBar().setTitle(event.getTitle());
        getLayoutInflater().inflate(R.layout.header_slider, headerFrame);


        slider = (SliderLayout) findViewById(R.id.slider);
        pagerIndicator = (PagerIndicator) findViewById(R.id.pager_indicator);

        pagerIndicator.setIndicatorStyleResource(R.drawable.pager_indicator_active, R.drawable.pager_indicator_inactive);
        slider.setCustomIndicator(pagerIndicator);

        if (event.getPhotos() != null) {
            for (int i = 0; i < event.getPhotos().size(); i++) {
                DefaultSliderView slide = new DefaultSliderView(this);
                slide
                        .image(ServerApi.getImgUrl(event.getPhotos().get(i),false))
                        .setScaleType(BaseSliderView.ScaleType.CenterInside);
                slider.addSlider(slide);
            }
            if (event.getPhotos().size() < 2) {
                pagerIndicator.setVisibility(View.GONE);

                slider.stopAutoCycle();
                slider.setPagerTransformer(false, new BaseTransformer() {@Override protected void onTransform(View view, float position) {}});
            }
        }
        title.setText(event.getTitle());
        timestamp.setText(event.getTimestamp());
        description.setText(event.getDescription());

        progress.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);
    }
}
