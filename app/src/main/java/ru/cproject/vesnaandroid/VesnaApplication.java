package ru.cproject.vesnaandroid;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import cz.msebera.android.httpclient.Header;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
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
        Fabric.with(this, new Crashlytics());
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            boolean isConnected = activeNetwork.isConnectedOrConnecting();
            // TODO: 04.11.16 резервный json
            // TODO: 26.11.16 кеширование 
            if (isConnected) {
                SyncHttpClient client = new SyncHttpClient();
                client.setMaxRetriesAndTimeout(1, 5000);
                RequestParams params = new RequestParams();

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                client.get(ServerApi.MALL, params, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        if (responseString != null)
                            Log.e(TAG, responseString);
                        else
                            throwable.printStackTrace();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.d(TAG, responseString);
                        MallResponse response = ResponseParser.parseMall(responseString);
                        Gson gson = new Gson();
                        SharedPreferences.Editor editor = getSharedPreferences(Settings.MALL_INFO, MODE_PRIVATE).edit();
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
