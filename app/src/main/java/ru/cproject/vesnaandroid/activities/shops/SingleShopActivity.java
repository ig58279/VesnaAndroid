package ru.cproject.vesnaandroid.activities.shops;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.squareup.picasso.Picasso;

import cz.msebera.android.httpclient.Header;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.activities.universal.ProtoSingleActivity;
import ru.cproject.vesnaandroid.adapters.MiniStocksAdapter;
import ru.cproject.vesnaandroid.helpers.ResponseParser;
import ru.cproject.vesnaandroid.helpers.TabBar;
import ru.cproject.vesnaandroid.helpers.TagsViewHelper;
import ru.cproject.vesnaandroid.obj.Shop;

/**
 * Created by Bitizen on 30.10.16.
 */

public class SingleShopActivity extends ProtoSingleActivity {
    private static final String TAG = "SingleShopActivity";

    private View cardBackground;

    private ViewGroup progress;
    private ViewGroup errorMessage;
        private Button retry;
    private ViewGroup contentView;

    private SliderLayout slider;
    private PagerIndicator pagerIndicator;

    private ImageView logo;
    private TextView name;
    private LinearLayout complements;
    private LinearLayout categoriesView;

    private TextView[] tabs;
    private View[] tabViews;
    private ViewGroup content;

    private TabBar tabBar;

    private int id;
    private Shop shop;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Intent intent = getIntent();
        setTheme(intent.getIntExtra("style", R.style.AppTheme));
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_single_shop, contentFrame);

        getSupportActionBar().setTitle("Весна");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;
        drawerBack.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        progress = (ViewGroup) findViewById(R.id.progress);
        errorMessage = (ViewGroup) findViewById(R.id.error_message);
        retry = (Button) findViewById(R.id.retry);
        contentView = (ViewGroup) findViewById(R.id.content_view);

        cardBackground = findViewById(R.id.card_background);
        logo = (ImageView) findViewById(R.id.logo);
        name = (TextView) findViewById(R.id.name);
        name.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        complements = (LinearLayout) findViewById(R.id.complements);
        categoriesView = (LinearLayout) findViewById(R.id.categories);

        tabs = new TextView[]{
                (TextView) findViewById(R.id.description),
                (TextView) findViewById(R.id.stocks),
                (TextView) findViewById(R.id.map)
        };
        content = (ViewGroup) findViewById(R.id.content);

        if (intent.hasExtra("id")) {
            id = intent.getIntExtra("id", 0);
            loadShop();
        } else if (intent.hasExtra("shop")) {
            Gson gson = new Gson();
            shop = gson.fromJson(intent.getStringExtra("shop"), Shop.class);
            showInfo();
        } else
            finish();

    }

    private void loadShop() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id", id);
        // TODO token
        Log.e(TAG, ServerApi.GET_SHOP);
        client.get(ServerApi.GET_SHOP, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (responseString != null)
                    Log.e(TAG, responseString);
                errorMessage.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                contentView.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString);
                shop = ResponseParser.parseShop(responseString);
                showInfo();
            }
        });

    }

    private void showInfo() {

        getLayoutInflater().inflate(R.layout.header_slider, headerFrame);

        slider = (SliderLayout) findViewById(R.id.slider);
        pagerIndicator = (PagerIndicator) findViewById(R.id.pager_indicator);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;

        getSupportActionBar().setTitle(shop.getName());

        Picasso
                .with(this)
                .load(ServerApi.getImgUrl(shop.getLogo(), false))
                .into(logo);

        name.setText(shop.getName());

        if (shop.getComplements() != null) {
            for (int i = 0; i < shop.getComplements().size(); i++) {
                Shop.Complement complement = shop.getComplements().get(i);
                View line = getLayoutInflater().inflate(R.layout.info_complement, complements, false);
                ImageView icon = (ImageView) line.findViewById(R.id.icon);
                TextView info = (TextView) line.findViewById(R.id.info);
                switch (complement.getKey()) {
                    case "phone":
                        icon.setImageResource(R.drawable.ic_phone);
                        info.setText(complement.getParametr());
                        info.setBackgroundResource(R.drawable.active_info_background);
                        complements.addView(line);
                        break;
                }
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TagsViewHelper.fillTagsView(categoriesView, shop.getCategories());

            }
        });


        pagerIndicator.setIndicatorStyleResource(R.drawable.pager_indicator_active, R.drawable.pager_indicator_inactive);
        slider.setCustomIndicator(pagerIndicator);

        View description = getLayoutInflater().inflate(R.layout.tab_shop_description, content, false);
        TextView text = (TextView) description.findViewById(R.id.description);
        text.setText(shop.getContent());

        View stocks = getLayoutInflater().inflate(R.layout.tab_shop_stocks, content, false);
        RecyclerView stocksView = (RecyclerView) stocks.findViewById(R.id.stocks_view);
        stocksView.setAdapter(new MiniStocksAdapter(this, shop.getStocks()));
        stocksView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        View map = getLayoutInflater().inflate(R.layout.tab_map, content, false);

        tabViews = new View[]{
                description,
                stocks,
                map
        };

        tabBar = new TabBar(tabs, tabViews, content, color, ContextCompat.getColor(this, R.color.colorTextGray));

        if (shop.getPhotos() != null) {
            for (int i = 0; i < shop.getPhotos().size(); i++) {
                DefaultSliderView slide = new DefaultSliderView(this);
                slide
                        .image(ServerApi.getImgUrl(shop.getPhotos().get(i),false))
                        .setScaleType(BaseSliderView.ScaleType.CenterInside);
                slider.addSlider(slide);
            }
            if (shop.getPhotos().size() < 2) {
                pagerIndicator.setVisibility(View.GONE);

                slider.stopAutoCycle();
                slider.setPagerTransformer(false, new BaseTransformer() {@Override protected void onTransform(View view, float position) {}});
            }
        }

        progress.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);

    }
}
