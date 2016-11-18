package ru.cproject.vesnaandroid.activities.stocks;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.activities.universal.ProtoMainActivity;
import ru.cproject.vesnaandroid.adapters.StocksAdapter;
import ru.cproject.vesnaandroid.helpers.ResponseParser;
import ru.cproject.vesnaandroid.obj.Stock;
import ru.cproject.vesnaandroid.obj.responses.StocksResponse;

/**
 * Created by Bitizen on 26.10.16.
 */

public class MainStocksActivity extends ProtoMainActivity {
    public static final String TAG = MainStocksActivity.class.getSimpleName();

    private static int LIMIT = 20;

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

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;
        drawerBack.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        sort = (ViewGroup) findViewById(R.id.sort);
        stocksView = (RecyclerView) findViewById(R.id.stocks_view);
        adapter = new StocksAdapter(this, stockList);
        stocksView.setAdapter(adapter);
        stocksView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        stocksView.setHasFixedSize(false);

        loadStocks();
    }

    private void loadStocks() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("skip", stockList.size());
        params.put("limit", LIMIT);
        params.put("sort", "a"); // TODO sort
        // TODO token
        // TODO cid

        client.get(ServerApi.GET_STOCKS, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (responseString != null)
                    Log.e(TAG, responseString);
                else
                    Log.e(TAG, "Connection error");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString);
                StocksResponse response = ResponseParser.parseStocks(responseString);
                int size = stockList.size();
                List<Stock> loadedStocks = response.getItems();
                for (int i = 0; i < loadedStocks.size(); i++)
                    stockList.add(loadedStocks.get(i));
                adapter.notifyItemRangeInserted(size, stockList.size());
            }
        });

    }
}
