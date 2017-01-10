package ru.cproject.vesnaandroid.activities.universal;

import android.app.ProgressDialog;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import cz.msebera.android.httpclient.Header;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.Settings;
import ru.cproject.vesnaandroid.helpers.ViewCreatorHelper;

/**
 * Created by Bitizen on 28.10.16.
 */

public abstract class ProtoSingleActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    protected ViewGroup headerFrame;

    protected FrameLayout contentFrame;

    private SlidingUpPanelLayout drawer;
    protected ViewGroup drawerBack;

    private TableLayout menu;

    protected ImageButton likeButton;

    protected int color;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proto_single);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        headerFrame = (ViewGroup) findViewById(R.id.header_frame);
        contentFrame = (FrameLayout) findViewById(R.id.content_frame);
        drawer = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        drawerBack = (ViewGroup) findViewById(R.id.drawer_back);
        menu = (TableLayout) findViewById(R.id.menu);
        likeButton = (ImageButton) findViewById(R.id.like);
        setSupportActionBar(toolbar);

        float dpi = getResources().getDisplayMetrics().density;
        drawer.setPanelHeight((int)(43 * dpi));
        ViewCreatorHelper.createMenu(this, menu);

        TypedValue typedValue = new TypedValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
            color = typedValue.data;
        } else {
            // TODO вычисление цвета для андроидов нижу 21
            color = ContextCompat.getColor(this, R.color.colorPrimary);
        }
        drawerBack.setBackgroundColor(color);
    }


    /**
     * Добавляет акцию или магазин в избранное или убирает из него
     * @param subjectId id магазина или акции
     * @param isLiked если true, то субъект добавляется в избранное, иначе - удаляется из избранного
     */
    protected void makeLikeOrDislike(String subjectId, final boolean isLiked){
        String url = isLiked ? ServerApi.LIKE : ServerApi.DISLIKE;
        final ProgressDialog progressDialog = ProgressDialog.show(getBaseContext(),"","Загрузка...",true);
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.add("usr", getSharedPreferences(Settings.REGISTRATION_INFO,MODE_PRIVATE).getString(Settings.RegistrationInfo.ID,""));
        requestParams.add("id",subjectId);
        asyncHttpClient.get(url, requestParams, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(),"Произошла ошибка.",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if(responseString != null) Log.e("sucLike",responseString);
                progressDialog.dismiss();
                displayLikeOrDislike(isLiked);
            }
        });
    }


    protected void displayLikeOrDislike(boolean isLiked){
        if(isLiked){
            likeButton.setBackground(getResources().getDrawable(R.drawable.ic_like));
            likeButton.setTag(R.drawable.ic_like);
        }
        else {
            likeButton.setBackground(getResources().getDrawable(R.drawable.dislike));
            likeButton.setTag(R.drawable.dislike);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
