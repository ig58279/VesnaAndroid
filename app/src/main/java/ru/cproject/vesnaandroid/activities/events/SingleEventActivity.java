package ru.cproject.vesnaandroid.activities.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.squareup.picasso.Picasso;

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

    private ImageView image;

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

        title = (TextView) findViewById(R.id.title);
        timestamp = (TextView) findViewById(R.id.timestamp);
        description = (TextView) findViewById(R.id.description);

        loadEvent();
    }

    private void loadEvent() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id", id);

        client.get(ServerApi.GET_EVENT, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (responseString != null)
                    Log.e(TAG, responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString);
                event = ResponseParser.parseEvent(responseString);
                showInfo();
            }
        });

    }

    private void showInfo() {
        getSupportActionBar().setTitle(event.getTitle());
        getLayoutInflater().inflate(R.layout.header_image, headerFrame);

        image = (ImageView) findViewById(R.id.image);

        Picasso
                .with(this)
                .load(event.getPhoto())
                .fit()
                .centerCrop()
                .into(image);

        title.setText(event.getTitle());
        timestamp.setText(event.getTimestamp());
        description.setText(event.getDescription());
    }
}
