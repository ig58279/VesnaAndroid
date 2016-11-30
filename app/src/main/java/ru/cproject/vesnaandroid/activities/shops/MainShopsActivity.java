package ru.cproject.vesnaandroid.activities.shops;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.activities.categories.FilterActivity;
import ru.cproject.vesnaandroid.activities.universal.ProtoMainActivity;
import ru.cproject.vesnaandroid.adapters.ShopsAdapter;
import ru.cproject.vesnaandroid.helpers.EndlessRecyclerOnScrollListener;
import ru.cproject.vesnaandroid.helpers.ResponseParser;
import ru.cproject.vesnaandroid.obj.Category;
import ru.cproject.vesnaandroid.obj.Shop;

import static android.R.attr.category;
import static android.R.attr.entries;

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

    private TextView categoriesView;
    private ImageView openCategories;

    private RecyclerView shopView;
    private ShopsAdapter adapter;
    private List<Shop> shopList = new ArrayList<>();
    private EndlessRecyclerOnScrollListener scrollListener;

    private int style;
    private String mode;

    private JsonObject cats;
    private String catsString;

    private String category;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Intent intent = getIntent();
        style = intent.getIntExtra("style", R.style.AppTheme);
        setTheme(style);
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_main_shops, contentFrame);

        mode = intent.getStringExtra("mod");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        category = intent.getExtras().getString("category", "Магазины");
        getSupportActionBar().setTitle(category);

        loading = (ViewGroup) findViewById(R.id.progress);
        errorMessage = (ViewGroup) findViewById(R.id.error_message);
        retry = (Button) findViewById(R.id.retry);
        retry.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);//TODO изменить для версий до 21
        content = (ViewGroup) findViewById(R.id.content);

        openCategories = (ImageView) findViewById(R.id.open_categories);
        categoriesView = (TextView) findViewById(R.id.categories);
        shopView = (RecyclerView) findViewById(R.id.shops_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        adapter = new ShopsAdapter(this, shopList, color, style);
        scrollListener = new EndlessRecyclerOnScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadShops();
            }
        };
        shopView.setAdapter(adapter);
        shopView.setLayoutManager(gridLayoutManager);

        shopView.addOnScrollListener(scrollListener);

        openCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainShopsActivity.this, FilterActivity.class);
                intent.putExtra("style", style);
                intent.putExtra("mod", mode);
                startActivityForResult(intent, 0);
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
        showCats();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            JsonParser parser = new JsonParser();
            String result = data.getStringExtra("result");
            cats = parser.parse(result).getAsJsonObject();
            Log.e(TAG, result);
        }
        if (requestCode == 0 && resultCode == RESULT_CANCELED) {
            cats = null;
        }
        shopList.clear();
        adapter.notifyDataSetChanged();
        scrollListener.resetState();
        loadShops();
        showCats();
    }

    private void showCats() {
        catsString = category;
        if (cats != null) {
            Set<Map.Entry<String, JsonElement>> entries = cats.entrySet();
            for (Map.Entry<String, JsonElement> e : entries) {
                JsonArray cat = e.getValue().getAsJsonArray();
                for (int i = 0; i < cat.size(); i++)
                    catsString += " & " + cat.get(i).getAsString();
            }
        } else {
           catsString += " & Все";
        }
        Spannable text = new SpannableString(catsString.toUpperCase());
        while (catsString.contains("&")) {
            Drawable arrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_right);
            arrow.setBounds(0, 0, arrow.getIntrinsicWidth(), arrow.getIntrinsicHeight());
            ImageSpan image = new ImageSpan(arrow, ImageSpan.ALIGN_BOTTOM);
            text.setSpan(image, catsString.indexOf("&"), catsString.indexOf("&") + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            catsString = catsString.replaceFirst("&", "*");
        }
        categoriesView.setTransformationMethod(null);
        categoriesView.setText(text);
    }

    private void loadShops() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            boolean isConnected = activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                AsyncHttpClient client = new AsyncHttpClient();

                JsonObject params = new JsonObject();
                params.addProperty("mod", mode);
                params.addProperty("count", LIMIT);
                params.addProperty("offset", shopList.size());
                if (cats != null)
                    params.add("cats", cats);

                StringEntity entity = new StringEntity(params.toString(), "UTF-8");

                client.post(this, ServerApi.GET_SHOPS + mode, entity, "application/json", new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        MainShopsActivity.this.onFailure(responseString);
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
            } else
                onFailure(null);
        } else
            onFailure(null);
    }
    private void onFailure(@Nullable String responseString) {
        if (responseString != null)
            Log.e(TAG, responseString);
        loading.setVisibility(View.GONE);
        content.setVisibility(View.GONE);
        errorMessage.setVisibility(View.VISIBLE);
    }
}
