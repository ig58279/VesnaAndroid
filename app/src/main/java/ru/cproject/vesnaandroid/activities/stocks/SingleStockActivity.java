package ru.cproject.vesnaandroid.activities.stocks;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.activities.universal.ProtoSingleActivity;
import ru.cproject.vesnaandroid.helpers.ResponseParser;
import ru.cproject.vesnaandroid.helpers.TabBar;
import ru.cproject.vesnaandroid.helpers.ViewCreatorHelper;
import ru.cproject.vesnaandroid.obj.Stock;

/**
 * Created by Bitizen on 28.10.16.
 */

public class SingleStockActivity extends ProtoSingleActivity {
    private static final String TAG = SingleStockActivity.class.getSimpleName();

    private View sliderShadow;
    private View background;

    private SliderLayout slider;
    private PagerIndicator pagerIndicator;

    private TextView[] tabs;
    private View[] layouts;
    private ViewGroup content;

    private TabBar tabBar;

    private int id;
    private Stock stock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.header_slider, headerFrame);
        getLayoutInflater().inflate(R.layout.activity_single_stock, contentFrame);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Весна");

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;
        drawerBack.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        Intent intent = getIntent();
        if (intent.hasExtra("id"))
            id = intent.getIntExtra("id", 0);
        else
            finish();

        slider = (SliderLayout) findViewById(R.id.slider);
        pagerIndicator = (PagerIndicator) findViewById(R.id.pager_indicator);
        tabs = new TextView[]{
                (TextView) findViewById(R.id.stock),
                (TextView) findViewById(R.id.map),
                (TextView) findViewById(R.id.shop)
        };
        content = (ViewGroup) findViewById(R.id.content);


        loadStock();
    }

    private void loadStock() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id", id);

        client.get(ServerApi.GET_STOCK, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (responseString != null)
                    Log.e(TAG, responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString);
                stock = ResponseParser.parseStock(responseString);
                showInfo();
            }
        });

    }

    private void showInfo() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;

        getSupportActionBar().setTitle(stock.getTitle());


        View stockView = getLayoutInflater().inflate(R.layout.tab_stock_description, content, false);
        Button getCoupon = (Button) stockView.findViewById(R.id.get_coupon);
        getCoupon.setVisibility(stock.isSpecial()? View.VISIBLE : View.GONE);
        TextView timestamp = (TextView) stockView.findViewById(R.id.timestamp);
        timestamp.setText(stock.getDate());
        TextView description = (TextView) stockView.findViewById(R.id.description);
        description.setText(stock.getContent());

        View mapView = getLayoutInflater().inflate(R.layout.tab_map, content, false);

        View shopView = ViewCreatorHelper.createShopCard(this, content, stock.getShop(), color);


        layouts = new View[]{
                stockView,
                mapView,
                shopView
        };

        tabBar = new TabBar(tabs, layouts, content, color, ContextCompat.getColor(this, R.color.colorTextGray));


        pagerIndicator.setIndicatorStyleResource(R.drawable.pager_indicator_active, R.drawable.pager_indicator_inactive);
        slider.setCustomIndicator(pagerIndicator);

        for (int i = 0; i < stock.getPhotos().size(); i++) {
            DefaultSliderView slide = new DefaultSliderView(this);
            slide
                    .image(ServerApi.getImgUrl(stock.getPhotos().get(i), false))
                    .setScaleType(BaseSliderView.ScaleType.CenterInside);

            slider.addSlider(slide);
        }
        if (stock.getPhotos().size() < 2) {
            pagerIndicator.setVisibility(View.GONE);

            slider.stopAutoCycle();
            slider.setPagerTransformer(false, new BaseTransformer() {@Override protected void onTransform(View view, float position) {}});
        }
    }


}
