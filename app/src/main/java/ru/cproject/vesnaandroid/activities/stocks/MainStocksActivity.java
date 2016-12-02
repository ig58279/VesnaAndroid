package ru.cproject.vesnaandroid.activities.stocks;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import ru.cproject.vesnaandroid.activities.shops.MainShopsActivity;
import ru.cproject.vesnaandroid.activities.universal.ProtoMainActivity;
import ru.cproject.vesnaandroid.adapters.SearchAdapter;
import ru.cproject.vesnaandroid.adapters.StocksAdapter;
import ru.cproject.vesnaandroid.helpers.EndlessRecyclerOnScrollListener;
import ru.cproject.vesnaandroid.helpers.ResponseParser;
import ru.cproject.vesnaandroid.helpers.RetryInterface;
import ru.cproject.vesnaandroid.obj.Stock;

import static android.R.attr.category;
import static android.R.attr.defaultValue;
import static android.R.attr.mode;
import static android.R.attr.onClick;
import static android.R.attr.showDefault;
import static ru.cproject.vesnaandroid.R.color.colorTextGray;
import static ru.cproject.vesnaandroid.R.id.sort_image;
import static ru.cproject.vesnaandroid.R.id.sort_text;
import static ru.cproject.vesnaandroid.R.id.stock;
import static ru.cproject.vesnaandroid.R.layout.activity_main;

/**
 * Created by Bitizen on 26.10.16.
 */

public class MainStocksActivity extends ProtoMainActivity implements RetryInterface {
    public static final String TAG = MainStocksActivity.class.getSimpleName();

    private static final int LIMIT = 1;

    private String[] test = {"По умолчанию", "По алфавиту (а-я)", "По алфавиту (я-а)", "Сначала эксклюзивные"};

    private int typeOfSort = 0;
    private static final int DEFAULT = 0;
    private static final int NAME_ASC = 1;
    private static final int NAME_DESC = 2;
    private static final int SPECIAL_ASC = 3;

    private ViewGroup loading;
    private ViewGroup errorMassage;
        private Button retry;
    private ViewGroup content;

    private ViewGroup sort;
    private ImageView sortImage;
    private TextView sortText;

    private TextView categoriesView;
    private ImageView openCategories;

    private RecyclerView stocksView;
    private List<Stock> stockList = new ArrayList<>();
    private StocksAdapter adapter;
    private EndlessRecyclerOnScrollListener scrollListener;

    private JsonObject cats;
    private String catsString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_main_stocks, contentFrame);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Акции");

        loading = (ViewGroup) findViewById(R.id.progress);
        errorMassage = (ViewGroup) findViewById(R.id.error_message);
        retry = (Button) findViewById(R.id.retry);
        retry.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);//TODO изменить для версий до 21
        content = (ViewGroup) findViewById(R.id.content);
        categoriesView = (TextView) findViewById(R.id.categories);
        openCategories = (ImageView) findViewById(R.id.category_arrow);

        sort = (ViewGroup) findViewById(R.id.sort);
        sortImage = (ImageView) findViewById(sort_image);
        sortText = (TextView) findViewById(sort_text);
        stocksView = (RecyclerView) findViewById(R.id.stocks_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter = new StocksAdapter(this, stockList, ContextCompat.getColor(this, R.color.colorPrimaryStocks), this);
        scrollListener = new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadStocks();
            }
        };
        stocksView.setAdapter(adapter);
        stocksView.setLayoutManager(linearLayoutManager);
        stocksView.addOnScrollListener(scrollListener);
        stocksView.setHasFixedSize(false);

        openCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainStocksActivity.this, FilterActivity.class);
                intent.putExtra("style", R.style.StockTheme);
                intent.putExtra("mod", mode);
                startActivityForResult(intent, 0);
            }
        });

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorMassage.setVisibility(View.GONE);
                content.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                loadStocks();
            }
        });

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortAlert();
            }
        });

        loadStocks();
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
        stockList.clear();
        adapter.notifyDataSetChanged();
        scrollListener.resetState();
        loadStocks();
        showCats();
    }

    private void showCats() {
        catsString = "Акции";
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

    private void loadStocks() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            boolean isConnected = activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                AsyncHttpClient client = new AsyncHttpClient();
                JsonObject params = new JsonObject();
                params.addProperty("mod", "stocks");
                params.addProperty("offset", stockList.size());
                params.addProperty("count", LIMIT);
                switch (typeOfSort) {
                    case NAME_ASC:
                        params.addProperty("sort", "name asc");
                        break;
                    case NAME_DESC:
                        params.addProperty("sort", "name desc");
                        break;
                    case SPECIAL_ASC:
                        params.addProperty("sort", "special asc");
                        break;
                }
                if (cats != null)
                    params.add("cats", cats);

                StringEntity entity = new StringEntity(params.toString(), "UTF-8");

                client.post(this, ServerApi.GET_STOCKS, entity, "application/json", new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        MainStocksActivity.this.onFailure(responseString);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.d(TAG, responseString);
                        List<Stock> stock = ResponseParser.parseStocks(responseString);
                        if (stock.size() != 0) {
                            adapter.setState(StocksAdapter.LOADING);
                            for (Stock s : stock) stockList.add(s);
                        } else {
                            adapter.setState(StocksAdapter.DEFAULT);
                        }
                        adapter.notifyDataSetChanged();

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
        if (stockList.size() != 0) {
            adapter.setState(StocksAdapter.ERROR);
            adapter.notifyDataSetChanged();
        } else {
            loading.setVisibility(View.GONE);
            content.setVisibility(View.GONE);
            errorMassage.setVisibility(View.VISIBLE);
        }
    }

    private void sortAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainStocksActivity.this);
        alertDialogBuilder.setTitle("Сортировка")
                .setItems(test, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        typeOfSort = i;
                        if (typeOfSort != 0) {
                            sortImage.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                            sortText.setTextColor(color);
                            sortText.setText(test[i]);
                        } else {
                            sortImage.clearColorFilter();
                            sortText.setTextColor(ContextCompat.getColor(MainStocksActivity.this, R.color.colorTextGray));
                            sortText.setText("Сортировка");
                        }
                        stockList.clear();
                        adapter.notifyDataSetChanged();
                        scrollListener.resetState();
                        loadStocks();
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void retry() {
        adapter.setState(StocksAdapter.LOADING);
        adapter.notifyItemChanged(stockList.size());
        loadStocks();
    }
}
