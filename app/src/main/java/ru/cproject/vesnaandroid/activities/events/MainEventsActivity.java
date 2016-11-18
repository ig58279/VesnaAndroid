package ru.cproject.vesnaandroid.activities.events;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.activities.universal.ProtoMainActivity;
import ru.cproject.vesnaandroid.adapters.EventsAdapter;
import ru.cproject.vesnaandroid.helpers.ResponseParser;
import ru.cproject.vesnaandroid.obj.Event;

/**
 * Created by Bitizen on 03.11.16.
 */

public class MainEventsActivity extends ProtoMainActivity {
    private static final String TAG = "MainEventsActivity";

    private RecyclerView eventsView;
    private EventsAdapter adapter;
    private List<Event> eventList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_main_events, contentFrame);

        getSupportActionBar().setTitle("События");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;
        drawerBack.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        eventsView = (RecyclerView) findViewById(R.id.events_view);
        adapter = new EventsAdapter(this, eventList);
        eventsView.setAdapter(adapter);
        eventsView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        eventsView.setHasFixedSize(false);

        loadEvents();
    }

    private void loadEvents() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        client.get(ServerApi.GET_EVENTS, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (responseString != null)
                    Log.e(TAG, responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString);

                List<Event> buf = ResponseParser.parseEvents(responseString);
                for (Event event : buf)
                    eventList.add(event);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
