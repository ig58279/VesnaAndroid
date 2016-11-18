package ru.cproject.vesnaandroid.activities.shops;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.activities.categories.CategoriesActivity;
import ru.cproject.vesnaandroid.activities.universal.ProtoMainActivity;
import ru.cproject.vesnaandroid.adapters.ShopsAdapter;
import ru.cproject.vesnaandroid.helpers.ResponseParser;
import ru.cproject.vesnaandroid.obj.Category;
import ru.cproject.vesnaandroid.obj.Shop;
import ru.cproject.vesnaandroid.obj.responses.ShopsResponse;

/**
 * Created by Bitizen on 29.10.16.
 */

public class MainShopsActivity extends ProtoMainActivity {
    private static final String TAG = "MainShopsActivity";

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Intent intent = getIntent();
        style = intent.getIntExtra("style", R.style.AppTheme);
        setTheme(style);
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_main_shops, contentFrame);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String category = intent.getExtras().getString("category", "Магазины");
        getSupportActionBar().setTitle(category);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;
        drawerBack.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        loading = (ViewGroup) findViewById(R.id.progress);
        errorMessage = (ViewGroup) findViewById(R.id.error_message);
        retry = (Button) findViewById(R.id.retry);
        content = (ViewGroup) findViewById(R.id.content);

        filter = (ViewGroup) findViewById(R.id.filter);
        categoriesView = (TextView) findViewById(R.id.categories);
        shopView = (RecyclerView) findViewById(R.id.shops_view);
        adapter = new ShopsAdapter(this, shopList, color, style);
        shopView.setAdapter(adapter);
        shopView.setLayoutManager(new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false));


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
        client.get(ServerApi.GET_SHOPS, params, new TextHttpResponseHandler() {
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

                ShopsResponse response = ResponseParser.parseShops(responseString);
                List<Shop> buf = response.getShops();
                int size = shopList.size();
                for (int i = 0; i < buf.size(); i++) {
                    shopList.add(buf.get(i));
                }
                adapter.notifyItemRangeInserted(size, buf.size());

                List<Category> categories = response.getCategories();
                if (categories != null && categories.size() != 0) {
                    String categoriesString = categories.get(0).getName();
                    for (int i = 1; i < categories.size(); i++)
                        categoriesString += " # " + categories.get(i).getName();
                    SpannableString span = new SpannableString(categoriesString);
                    Drawable arrow = ContextCompat.getDrawable(MainShopsActivity.this, R.drawable.ic_category_separator);
                    float dpi = getResources().getDisplayMetrics().density;
                    arrow.setBounds(0, (int)(4 * dpi), arrow.getIntrinsicWidth(), arrow.getIntrinsicHeight());
                    while (categoriesString.contains("#")) {
                        span.setSpan(new ImageSpan(arrow), categoriesString.indexOf("#"), categoriesString.indexOf("#") + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        categoriesString = categoriesString.replaceFirst("#", "!");
                    }
                    categoriesView.setTransformationMethod(null);
                    categoriesView.setText(span);
                }

                choose = response.getChoose();
                filter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainShopsActivity.this, CategoriesActivity.class);
                        Gson gson = new Gson();
                        intent.putExtra("categories", gson.toJson(choose.toArray(), Category[].class));
                        intent.putExtra("style", style);
                        startActivity(intent);
                    }
                });

                loading.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
            }
        });
    }
}
