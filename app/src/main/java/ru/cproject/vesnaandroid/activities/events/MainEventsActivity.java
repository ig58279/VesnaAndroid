package ru.cproject.vesnaandroid.activities.events;

import android.content.Context;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.activities.universal.ProtoMainActivity;
import ru.cproject.vesnaandroid.adapters.EventsAdapter;
import ru.cproject.vesnaandroid.helpers.EndlessRecyclerOnScrollListener;
import ru.cproject.vesnaandroid.helpers.ResponseParser;
import ru.cproject.vesnaandroid.obj.Event;

/**
 * Created by Bitizen on 03.11.16.
 */

public class MainEventsActivity extends ProtoMainActivity {
    private static final String TAG = "MainEventsActivity";

    private static final int LIMIT = 20;

    private ViewGroup loading;
    private ViewGroup errorMassage;
        private Button retry;
    private ViewGroup content;

    private RecyclerView eventsView;
    private EventsAdapter adapter;
    private List<Event> eventList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_main_events, contentFrame);

        getSupportActionBar().setTitle("События");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loading = (ViewGroup) findViewById(R.id.progress);
        errorMassage = (ViewGroup) findViewById(R.id.error_message);
        retry = (Button) findViewById(R.id.retry);
        retry.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);//TODO изменить для версий до 21
        content = (ViewGroup) findViewById(R.id.content);

        eventsView = (RecyclerView) findViewById(R.id.events_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter = new EventsAdapter(this, eventList);
        eventsView.setAdapter(adapter);
        eventsView.setLayoutManager(linearLayoutManager);
        eventsView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadEvents();
            }
        });
        eventsView.setHasFixedSize(false);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorMassage.setVisibility(View.GONE);
                content.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                loadEvents();
            }
        });

        loadEvents();
    }

    private void loadEvents() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            boolean isConnected = activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("mod", "events");
                params.put("offset", eventList.size());
                params.put("count", LIMIT);
                params.setUseJsonStreamer(true);

                client.post(ServerApi.GET_EVENTS, params, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        MainEventsActivity.this.onFailure(responseString);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.d(TAG, responseString);

                        List<Event> buf = ResponseParser.parseEvents(responseString);
                        int prevSize = eventList.size();
                        for (Event event : buf) eventList.add(event);
                        adapter.notifyItemRangeInserted(prevSize, buf.size());

                        loading.setVisibility(View.GONE);
                        content.setVisibility(View.VISIBLE);
                    }
                });
            } else
                onFailure(null);
        } else
            onFailure(null);
    }
    private void onFailure (@Nullable String responseString) {
        if (responseString != null)
            Log.e(TAG, responseString);
        loading.setVisibility(View.GONE);
        content.setVisibility(View.GONE);
        errorMassage.setVisibility(View.VISIBLE);
    }
}
