package ru.cproject.vesnaandroid;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import ru.cproject.vesnaandroid.helpers.ResponseParser;
import ru.cproject.vesnaandroid.obj.responses.MallResponse;

/**
 * Created by Bitizen on 04.11.16.
 */

public class VesnaApplication extends Application {
    private static final String TAG = "VesnaApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            boolean isConnected = activeNetwork.isConnectedOrConnecting();
            // TODO: 04.11.16 тест соединения и резервный json
            final SharedPreferences mallInfo = getSharedPreferences(Settings.MALL_INFO, MODE_PRIVATE);
            if (isConnected) {
                AsyncHttpClient client = new AsyncHttpClient();
                client.setMaxRetriesAndTimeout(1, 5000);
                RequestParams params = new RequestParams();

                client.get(ServerApi.MALL, params, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        if (responseString != null)
                            Log.e(TAG, responseString);

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.d(TAG, responseString);
                        MallResponse response = ResponseParser.parseMall(responseString);
                        Gson gson = new Gson();
                        SharedPreferences.Editor editor = mallInfo.edit();
                        editor.putString(Settings.MallInfo.MALL,
                                gson.toJson(response.getMallInfo()));
                        editor.putString(Settings.MallInfo.FUNCTIONAL,
                                gson.toJson(response.getFunctional().toArray()));
                        editor.putString(Settings.MallInfo.SHOWS,
                                gson.toJson(response.getShows().toArray()));
                        editor.apply();
                    }
                });
            }
        }
    }
}
