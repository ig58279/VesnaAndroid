package ru.cproject.vesnaandroid.activities;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.adapters.SearchAdapter;
import ru.cproject.vesnaandroid.helpers.ResponseParser;
import ru.cproject.vesnaandroid.obj.Search;

/**
 * Created by andro on 22.11.2016.
 */

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private static final int LIMIT = 20;

    private RecyclerView recyclerView;
    private List<Search> list = new ArrayList<>();

    private EditText search;

    private SearchAdapter adapter;

    private AsyncHttpClient client = new AsyncHttpClient();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        int color = ContextCompat.getColor(this, R.color.colorTextBlack);
        toolbar.getNavigationIcon().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        search = (EditText) findViewById(R.id.search);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new SearchAdapter(this, list);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter.notifyDataSetChanged();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (search.getText().length() >= 2) {
                    reset();
                    search();
                } else {
                    reset();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void reset() {
        client.cancelAllRequests(true);
        list.clear();
        adapter.notifyDataSetChanged();
    }

    private void search() {
        JsonObject params = new JsonObject();
        params.addProperty("q", "*" + search.getText().toString() + "*");
        params.addProperty("offset", list.size());
        params.addProperty("count", LIMIT);
        JsonArray qf = new JsonArray();
        qf.add("name");
        params.add("qf", qf);

        StringEntity entry = new StringEntity(params.toString(), "UTF-8");

        client.post(this, ServerApi.SEARCH, entry, "application/json", new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (responseString != null)
                    Log.e(TAG, responseString);
                // TODO: 22.11.2016 Обработка ошибки
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString);

                List<Search> buf = ResponseParser.parseSearch(responseString);
                int prevSize = list.size();
                for (Search s: buf) list.add(s);
                adapter.notifyItemRangeInserted(prevSize, buf.size());
            }
        });
    }
}
