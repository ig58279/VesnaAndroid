package ru.cproject.vesnaandroid.activities.films;

import android.content.Context;
import android.content.Intent;
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
import ru.cproject.vesnaandroid.helpers.ResponseParser;
import ru.cproject.vesnaandroid.obj.Film;
import ru.cproject.vesnaandroid.obj.Shop;
import ru.cproject.vesnaandroid.obj.responses.FilmsResponse;

/**
 * Created by Bitizen on 01.11.16.
 */

public class MainFilmsActivity extends ProtoMainActivity {
    private static final String TAG = "MainFilmsActivity";

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
        adapter = new FilmsAdapter(this, filmList, color);
        filmsView.setAdapter(adapter);
        filmsView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


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
                        int size = filmList.size();
                        cinema = response.getCinema();
                        for (int i = 0; i < films.size(); i++) {
                            filmList.add(films.get(i));
                        }
                        adapter.notifyItemRangeInserted(size, films.size());

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
    }
}
