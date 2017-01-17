package ru.cproject.vesnaandroid.activities.films;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.activities.shops.SingleShopActivity;
import ru.cproject.vesnaandroid.activities.universal.ProtoMainActivity;
import ru.cproject.vesnaandroid.adapters.FilmsAdapter;
import ru.cproject.vesnaandroid.helpers.EndlessRecyclerOnScrollListener;
import ru.cproject.vesnaandroid.helpers.ResponseParser;
import ru.cproject.vesnaandroid.helpers.RetryInterface;
import ru.cproject.vesnaandroid.obj.Film;
import ru.cproject.vesnaandroid.obj.Shop;
import ru.cproject.vesnaandroid.obj.responses.FilmsResponse;

/**
 * Created by Bitizen on 01.11.16.
 */

public class MainFilmsActivity extends ProtoMainActivity {
    private static final String TAG = "MainFilmsActivity";

    private ViewGroup loading;
    private ViewGroup errorMessage;
    private Button retry;
    private ViewGroup content;

    private ViewGroup about;

    private RecyclerView filmsView;
    private List<Film> filmList = new ArrayList<>();
    private FilmsAdapter adapter;

    private int sort = 0;

    private Shop cinema;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_main_films, contentFrame);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Люксор");

        about = (ViewGroup) findViewById(R.id.about);
        filmsView = (RecyclerView) findViewById(R.id.films_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter = new FilmsAdapter(this, filmList, ContextCompat.getColor(this, R.color.colorPrimaryCinema));
        filmsView.setAdapter(adapter);
        filmsView.setLayoutManager(linearLayoutManager);
        filmsView.setHasFixedSize(false);

        loading = (ViewGroup) findViewById(R.id.progress);
        errorMessage = (ViewGroup) findViewById(R.id.error_message);
        retry = (Button) findViewById(R.id.retry);
        retry.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);//TODO изменить для версий до 21
        content = (ViewGroup) findViewById(R.id.content);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorMessage.setVisibility(View.GONE);
                content.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                loadFilms();
            }
        });

        loadFilms();
    }

    private void loadFilms() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            boolean isConnected = activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, sort);
                //        params.put("sort", (long) (calendar.getTimeInMillis() / 1000));

                client.get(ServerApi.GET_FILMS, params, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        MainFilmsActivity.this.onFailure(responseString);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.d(TAG, responseString);

                        FilmsResponse response = ResponseParser.parseFilms(responseString);
                        cinema = response.getCinema();
                        List<Film> films = response.getItems();
                        cinema = response.getCinema();
                        if(films != null)
                        for (Film f : films) filmList.add(f);
                        adapter.notifyDataSetChanged();

                        loading.setVisibility(View.GONE);
                        content.setVisibility(View.VISIBLE);

                        about.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(MainFilmsActivity.this, SingleShopActivity.class);
                                Gson gson = new Gson();
                                intent.putExtra("shop", gson.toJson(cinema));
                                intent.putExtra("style", R.style.FilmsTheme);
                                intent.putExtra("background", color);
                                startActivity(intent);
                            }
                        });
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
