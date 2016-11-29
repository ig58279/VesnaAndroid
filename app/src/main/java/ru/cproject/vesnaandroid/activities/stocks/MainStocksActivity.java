package ru.cproject.vesnaandroid.activities.stocks;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.content.Context;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.activities.universal.ProtoMainActivity;
import ru.cproject.vesnaandroid.adapters.StocksAdapter;
import ru.cproject.vesnaandroid.helpers.EndlessRecyclerOnScrollListener;
import ru.cproject.vesnaandroid.helpers.ResponseParser;
import ru.cproject.vesnaandroid.obj.Stock;

import static android.R.attr.defaultValue;
import static android.R.attr.onClick;
import static android.R.attr.showDefault;
import static ru.cproject.vesnaandroid.R.color.colorTextGray;
import static ru.cproject.vesnaandroid.R.id.sort_image;
import static ru.cproject.vesnaandroid.R.id.sort_text;
import static ru.cproject.vesnaandroid.R.layout.activity_main;

/**
 * Created by Bitizen on 26.10.16.
 */

public class MainStocksActivity extends ProtoMainActivity {
    public static final String TAG = MainStocksActivity.class.getSimpleName();

    private static int LIMIT = 20;

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

    private RecyclerView stocksView;
    private List<Stock> stockList = new ArrayList<>();
    private StocksAdapter adapter;
    private EndlessRecyclerOnScrollListener scrollListener;

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

        sort = (ViewGroup) findViewById(R.id.sort);
        sortImage = (ImageView) findViewById(sort_image);
        sortText = (TextView) findViewById(sort_text);
        stocksView = (RecyclerView) findViewById(R.id.stocks_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter = new StocksAdapter(this, stockList);
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
    }

    private void loadStocks() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("mod", "stocks");
        params.put("offset", stockList.size());
        params.put("count", LIMIT);
        switch (typeOfSort) {
            case NAME_ASC:
                params.put("sort", "name asc");
                break;
            case NAME_DESC:
                params.put("sort", "name desc");
                break;
            case SPECIAL_ASC:
                params.put("sort", "special asc");
                break;
        }
        params.setUseJsonStreamer(true);
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            boolean isConnected = activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("mod", "stocks");
                params.put("offset", stockList.size());
                params.put("count", LIMIT);
                params.setUseJsonStreamer(true);

                client.post(ServerApi.GET_STOCKS, params, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        MainStocksActivity.this.onFailure(responseString);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.d(TAG, responseString);
                        List<Stock> stock = ResponseParser.parseStocks(responseString);
                        int prevSize = stockList.size();
                        for (Stock s : stock) stockList.add(s);
                        adapter.notifyItemRangeInserted(prevSize, stock.size());

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
        errorMassage.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
            }
        });
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
}
