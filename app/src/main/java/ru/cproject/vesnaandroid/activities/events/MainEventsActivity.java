package ru.cproject.vesnaandroid.activities.events;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
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

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;
        drawerBack.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        loading = (ViewGroup) findViewById(R.id.progress);
        errorMassage = (ViewGroup) findViewById(R.id.error_message);
        retry = (Button) findViewById(R.id.retry);
        content = (ViewGroup) findViewById(R.id.content);

        eventsView = (RecyclerView) findViewById(R.id.events_view);
        adapter = new EventsAdapter(this, eventList);
        eventsView.setAdapter(adapter);
        eventsView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("mod", "events");
        params.put("offset", eventList.size());
        params.put("count", LIMIT);
        params.setUseJsonStreamer(true);

        client.post(ServerApi.GET_EVENTS, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (responseString != null)
                    Log.e(TAG, responseString);
                loading.setVisibility(View.GONE);
                content.setVisibility(View.GONE);
                errorMassage.setVisibility(View.VISIBLE);
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
    }
}
