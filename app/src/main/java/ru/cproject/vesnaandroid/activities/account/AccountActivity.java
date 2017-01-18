package ru.cproject.vesnaandroid.activities.account;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import cz.msebera.android.httpclient.Consts;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.Settings;
import ru.cproject.vesnaandroid.adapters.MiniShopAdapter;
import ru.cproject.vesnaandroid.adapters.MiniStocksAdapter;
import ru.cproject.vesnaandroid.helpers.ResponseParser;
import ru.cproject.vesnaandroid.helpers.RetryInterface;
import ru.cproject.vesnaandroid.obj.Shop;
import ru.cproject.vesnaandroid.obj.User;

import static android.view.View.GONE;

public class AccountActivity extends AppCompatActivity implements RetryInterface {

    private ImageView photoImageView;
    private TextView nameTextView;
    private TextView pointsTextView;
    private TextView shopTextView;
    private TextView shareTextView;
    private TextView couponTextView;
    private ImageView logoutImageView;
    private ViewGroup progress;
    private ViewGroup errorMessage;
    private ImageView back;
        private Button retry;
    private RecyclerView recyclerView;
    private User user;
    protected int color;
    private String id;
    private RelativeLayout mainContent;

    private MiniStocksAdapter miniStocksAdapter;
    private MiniShopAdapter miniShopAdapter;

    private SharedPreferences settingsSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        settingsSharedPrefs = getSharedPreferences(Settings.REGISTRATION_INFO, MODE_PRIVATE);
        id = settingsSharedPrefs.getString(Settings.RegistrationInfo.ID,"");

        if (id.isEmpty()) {
            Toast.makeText(AccountActivity.this, "Отсутствует идентификатор пользователя", Toast.LENGTH_SHORT).show();
            finish();
        }

        photoImageView = (ImageView) findViewById(R.id.photo_image_view);
        nameTextView = (TextView) findViewById(R.id.name_text_view);
        pointsTextView = (TextView) findViewById(R.id.points_text_view);
        logoutImageView = (ImageView) findViewById(R.id.logout);
        shopTextView = (TextView) findViewById(R.id.shop_button_text_view);
        shareTextView = (TextView) findViewById(R.id.share_button_text_view);
        mainContent = (RelativeLayout) findViewById(R.id.main_content);

        couponTextView = (TextView) findViewById(R.id.coupon_button_text_view);
        back = (ImageView) findViewById(R.id.back);
        recyclerView = (RecyclerView) findViewById(R.id.account_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TypedValue typedValue = new TypedValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
            color = typedValue.data;
        } else {
            // TODO вычисление цвета для андроидов нижу 21
            color = ContextCompat.getColor(this, R.color.colorPrimary);
        }
        progress = (ViewGroup) findViewById(R.id.progress);
        errorMessage = (ViewGroup) findViewById(R.id.error_message);
        retry = (Button) findViewById(R.id.retry);
        retry.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorMessage.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                getAndShowAccountInfo();
            }
        });



        logoutImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        shopTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopTextView.setTextColor(ContextCompat.getColor(AccountActivity.this,R.color.colorPrimary));
                shareTextView.setTextColor(ContextCompat.getColor(AccountActivity.this,R.color.colorTextGray));
                couponTextView.setTextColor(ContextCompat.getColor(AccountActivity.this,R.color.colorTextGray));
                if(user.getShops() != null){
                    miniShopAdapter = new MiniShopAdapter(AccountActivity.this,user.getShops(),color,R.style.AppTheme);
                    recyclerView.setAdapter(miniShopAdapter);
                }
            }
        });

        shareTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTextView.setTextColor(ContextCompat.getColor(AccountActivity.this,R.color.colorPrimary));
                shopTextView.setTextColor(ContextCompat.getColor(AccountActivity.this,R.color.colorTextGray));
                couponTextView.setTextColor(ContextCompat.getColor(AccountActivity.this,R.color.colorTextGray));
                if(user.getStocks() != null) {
                    miniStocksAdapter = new MiniStocksAdapter(AccountActivity.this, user.getStocks(), color, AccountActivity.this);
                    recyclerView.setAdapter(miniStocksAdapter);
                }
            }
        });

        couponTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                couponTextView.setTextColor(ContextCompat.getColor(AccountActivity.this,R.color.colorPrimary));
                shareTextView.setTextColor(ContextCompat.getColor(AccountActivity.this,R.color.colorTextGray));
                shopTextView.setTextColor(ContextCompat.getColor(AccountActivity.this,R.color.colorTextGray));
            }
        });


        getAndShowAccountInfo();

    }

        private void showAccountInfo(User user){
            if(user.getRole().equals("buyer")){
                if(user.getPbs()!=null)pointsTextView.setText(user.getPbs());
            }
            if(user.getRole().equals("seller")){
                if(user.getPss()!=null)pointsTextView.setText(user.getPss());
            }
            nameTextView.setText(user.getFname());
            final int radius = 16;
            final int margin = 0;
            final Transformation transformation = new RoundedCornersTransformation(radius, margin, RoundedCornersTransformation.CornerType.ALL);
            if(user.getPhoto()!=null && !user.getPhoto().isEmpty()){
                Picasso.with(AccountActivity.this).load(settingsSharedPrefs.getString(Settings.RegistrationInfo.PHOTO,""))
                        .resize(250,250)
                        .transform(transformation)
                        .error(getBaseContext().getResources().getDrawable(R.drawable.ic_small_placeholder))
                        .into(photoImageView);
            }else{
                String photo = settingsSharedPrefs.getString(Settings.RegistrationInfo.PHOTO,null);
                if(photo != null && !photo.isEmpty()){
                    Picasso.with(AccountActivity.this).load(settingsSharedPrefs.getString(Settings.RegistrationInfo.PHOTO,""))
                            .resize(250,250)
                            .transform(transformation)
                            .error(getBaseContext().getResources().getDrawable(R.drawable.ic_small_placeholder))
                            .into(photoImageView);
                }else {
                    Picasso.with(AccountActivity.this).load(R.drawable.ic_small_placeholder)
                            .resize(250, 250)
                            .transform(transformation)
                            .into(photoImageView);
                }
            }
    }

    private void getAndShowAccountInfo(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            boolean isConnected = activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
                RequestParams requestParams = new RequestParams();
                requestParams.add("id", settingsSharedPrefs.getString(Settings.RegistrationInfo.ID, ""));
                asyncHttpClient.get(ServerApi.GET_USER, requestParams, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                       AccountActivity.this.onFailure(responseString);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        if (responseString != null) Log.e("accountSuc", responseString);
                        progress.setVisibility(View.GONE);
                        errorMessage.setVisibility(View.GONE);
                        user = ResponseParser.parseUser(responseString);
                        mainContent.setVisibility(View.VISIBLE);
                        showAccountInfo(user);

                        if (getIntent().getBooleanExtra("forCoupons", false))
                            couponTextView.performClick();
                        else shopTextView.performClick();


                    }
                });
            }else onFailure(null);
        }else onFailure(null);
    }

    private void onFailure (@Nullable String responseString) {
        if (responseString != null) Log.e("accountFail", responseString);
        progress.setVisibility(View.GONE);
        errorMessage.setVisibility(View.VISIBLE);
        Toast.makeText(AccountActivity.this, "Произошла ошибка!", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void retry() {

    }

    private void logout(){
        new AlertDialog.Builder(this)
                .setMessage("Выйти из личного кабинета?")
                .setNegativeButton("Нет",null)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        settingsSharedPrefs.edit()
                                .remove(Settings.RegistrationInfo.ID)
                                .remove(Settings.RegistrationInfo.PBS)
                                .remove(Settings.RegistrationInfo.PSS)
                                .remove(Settings.RegistrationInfo.NAME)
                                .remove(Settings.RegistrationInfo.PHONE)
                                .remove(Settings.RegistrationInfo.ROLE)
                                .remove(Settings.RegistrationInfo.SURNAME)
                                .apply();
                        finish();
                    }
                })
                .show();
    }

}
