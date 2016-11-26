package ru.cproject.vesnaandroid.activities.shops;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.activities.universal.ProtoMainActivity;
import ru.cproject.vesnaandroid.adapters.ShopsAdapter;
import ru.cproject.vesnaandroid.helpers.EndlessRecyclerOnScrollListener;
import ru.cproject.vesnaandroid.helpers.ResponseParser;
import ru.cproject.vesnaandroid.obj.Category;
import ru.cproject.vesnaandroid.obj.Shop;

/**
 * Created by Bitizen on 29.10.16.
 */

public class MainShopsActivity extends ProtoMainActivity {
    private static final String TAG = "MainShopsActivity";

    private static final int LIMIT = 21;

    private ViewGroup loading;
    private ViewGroup errorMessage;
        private Button retry;
    private ViewGroup content;

    private ViewGroup filter;
    private TextView categoriesView;

    private RecyclerView shopView;
    private ShopsAdapter adapter;
    private List<Shop> shopList = new ArrayList<>();

    private List<Category> choose;

    private int style;
    private String mode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Intent intent = getIntent();
        style = intent.getIntExtra("style", R.style.AppTheme);
        setTheme(style);
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_main_shops, contentFrame);

        mode = intent.getStringExtra("mod");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String category = intent.getExtras().getString("category", "Магазины");
        getSupportActionBar().setTitle(category);

        loading = (ViewGroup) findViewById(R.id.progress);
        errorMessage = (ViewGroup) findViewById(R.id.error_message);
        retry = (Button) findViewById(R.id.retry);
        content = (ViewGroup) findViewById(R.id.content);

        filter = (ViewGroup) findViewById(R.id.filter);
        categoriesView = (TextView) findViewById(R.id.categories);
        shopView = (RecyclerView) findViewById(R.id.shops_view);
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        adapter = new ShopsAdapter(this, shopList, color, style);
        shopView.setAdapter(adapter);
        shopView.setLayoutManager(gridLayoutManager);

        shopView.addOnScrollListener(new EndlessRecyclerOnScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadShops();
            }
        });

        categoriesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter.callOnClick();
            }
        });

        ViewGroup scroll = (ViewGroup) findViewById(R.id.category_view);
        scroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "click!");
                filter.callOnClick();
            }
        });

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorMessage.setVisibility(View.GONE);
                content.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                loadShops();
            }
        });

        loadShops();
    }

    private void loadShops() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("mod", mode);
        params.put("count", LIMIT);
        params.put("offset", shopList.size());
        params.setUseJsonStreamer(true);
        client.post(ServerApi.GET_SHOPS, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (responseString != null)
                    Log.e(TAG, responseString);
                loading.setVisibility(View.GONE);
                content.setVisibility(View.GONE);
                errorMessage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString);
                JsonParser parser = new JsonParser();
                JsonObject response = parser.parse(responseString).getAsJsonObject();

                String list = "list";
                if (response.has(list) && !response.get(list).isJsonNull()) {
                    List<Shop> buf = ResponseParser.parseShops(response.get(list).toString());
                    int prevSize = shopList.size();
                    for (Shop s : buf) shopList.add(s);
                    adapter.notifyItemRangeInserted(prevSize, buf.size());
                }

                loading.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
            }
        });
    }
}
