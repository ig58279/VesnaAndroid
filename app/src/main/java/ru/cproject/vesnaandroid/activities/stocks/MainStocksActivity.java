package ru.cproject.vesnaandroid.activities.stocks;

import android.content.Context;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import ru.cproject.vesnaandroid.adapters.StocksAdapter;
import ru.cproject.vesnaandroid.helpers.EndlessRecyclerOnScrollListener;
import ru.cproject.vesnaandroid.helpers.ResponseParser;
import ru.cproject.vesnaandroid.obj.Stock;

/**
 * Created by Bitizen on 26.10.16.
 */

public class MainStocksActivity extends ProtoMainActivity {
    public static final String TAG = MainStocksActivity.class.getSimpleName();

    private static int LIMIT = 20;

    private ViewGroup loading;
    private ViewGroup errorMassage;
        private Button retry;
    private ViewGroup content;

    private ViewGroup sort;

    private RecyclerView stocksView;
    private List<Stock> stockList = new ArrayList<>();
    private StocksAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_main_stocks, contentFrame);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Акции");

        loading = (ViewGroup) findViewById(R.id.progress);
        errorMassage = (ViewGroup) findViewById(R.id.error_message);
        retry = (Button) findViewById(R.id.retry);
        content = (ViewGroup) findViewById(R.id.content);

        sort = (ViewGroup) findViewById(R.id.sort);
        stocksView = (RecyclerView) findViewById(R.id.stocks_view);
        retry.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter = new StocksAdapter(this, stockList);
        stocksView.setAdapter(adapter);
        stocksView.setLayoutManager(linearLayoutManager);
        stocksView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadStocks();
            }
        });
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

        loadStocks();
    }

    private void loadStocks() {
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
    }
}
