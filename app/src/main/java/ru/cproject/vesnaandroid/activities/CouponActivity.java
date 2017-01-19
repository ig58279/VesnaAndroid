package ru.cproject.vesnaandroid.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.Settings;
import ru.cproject.vesnaandroid.activities.universal.ProtoMainActivity;
import ru.cproject.vesnaandroid.adapters.CouponeAdapter;
import ru.cproject.vesnaandroid.helpers.ResponseParser;
import ru.cproject.vesnaandroid.helpers.RetryInterface;
import ru.cproject.vesnaandroid.obj.Coupon;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by andro on 19.01.2017.
 */

public class CouponActivity extends AppCompatActivity {
    private static final String TAG = "CouponActivity";

    private ImageView image;
    private TextView name;
    private Button back;

    private int id;
    private Coupon coupon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_coupon);

        image = (ImageView) findViewById(R.id.image);
        name = (TextView) findViewById(R.id.name);
        back = (Button) findViewById(R.id.back_button);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("id"))
            id = intent.getIntExtra("id", 0);
        else
            finish();

        loadCoupon();
    }

    private void loadCoupon() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            boolean isConnected = activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("id", id);
                params.put("usr", getSharedPreferences(Settings.REGISTRATION_INFO,MODE_PRIVATE).getString(Settings.RegistrationInfo.ID,""));

                client.get(ServerApi.GET_COUPON, params, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e(TAG, "onFailure: " + responseString);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.d(TAG, "onSuccess: " + responseString);
                        coupon = ResponseParser.parseCoupone(responseString);
                        showInfo();
                    }
                });
            }
        }
    }

    private void showInfo() {
        float dpi = getApplicationContext().getResources().getDisplayMetrics().density;
        Picasso
                .with(getApplicationContext())
                .load(ServerApi.getImgUrl(coupon.getImage(), false))
                .placeholder(R.drawable.ic_big_placeholder)
                .fit()
                .centerInside()
                .transform(new RoundedCornersTransformation((int)(4*dpi),0, RoundedCornersTransformation.CornerType.TOP))
                .into(image);

        name.setText(coupon.getName());
    }
}
