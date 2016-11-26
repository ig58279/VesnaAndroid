package ru.cproject.vesnaandroid.activities.categories;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.activities.universal.ProtoMainActivity;
import ru.cproject.vesnaandroid.fragments.filter.CategoriesFragment;

/**
 * Created by Bitizen on 07.11.16.
 */

public class FilterActivity extends ProtoMainActivity {
    private static final String TAG = "FilterActivity";

    /**
     * Активити отвечает за работу с фильтрами.
     * Обязательное начальное условие - это extra.mod
     * mod - уточняет какой моуль просит сортировку, так как для разных модулей ответ может отличатся
     * Далле мы определяем что ответил сервер - main категории или же уже фасеты
     * Если сервер ответил main, то отображаем первый фрагмент
     * Если нет, то отображаем 2 фрагмент
     */

    private int style;

    private String mod;

    private List<String> categories = new ArrayList<>(); // категории - относится к первому экрану
    private Map<String, String> facets; // отображаемые значения для вторго экрана
    private Map<String, String> chosenParams; // уже выбраные параметры
    private List<String> facetsPrams; // список возможных вариантов для фасета - 3 экран

    private int topStep = NONE;
    private int currentStep = NONE;
    private static final int NONE = -1;
    private static final int CATEGORY_CHOSE = 0;
    private static final int FACETS_CHOSE = 1;
    private static final int FACET_PARAM_CHOSE = 2;

    private boolean firstReqest = true;

    private FrameLayout fragmentFrame;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Intent intent = getIntent();
        style = intent.getIntExtra("style", R.style.AppTheme);
        setTheme(style);
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_categories, contentFrame);

        getSupportActionBar().setTitle("Фильтр");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mod = intent.getStringExtra("mod");

        fragmentFrame = (FrameLayout) findViewById(R.id.fragment_frame);

        requestCats();
    }

    @Override
    public void onBackPressed() {
        if (topStep == currentStep)
            finish();
        else
            stepBack();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void stepBack() {
        currentStep--;
        showFragment(currentStep);
    }

    private void showFragment(int fragment) {
        switch (fragment) {
            case CATEGORY_CHOSE:
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(fragmentFrame.getId(), new CategoriesFragment())
                        .commit();
                break;
        }
        currentStep = fragment;
    }

    private void requestCats() {
        AsyncHttpClient client = new AsyncHttpClient();
        JsonObject catsParams = new JsonObject();
        catsParams.addProperty("mod", mod);

        StringEntity entity = new StringEntity(catsParams.toString(), "UTF-8");

        client.post(this, ServerApi.CATS, entity, "application/json", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (responseString != null)
                    Log.e(TAG, "onFailure: " + responseString);
                else
                    throwable.printStackTrace();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, "onSuccess: " + responseString);
                JsonParser parser = new JsonParser();
                JsonObject response = parser.parse(responseString).getAsJsonObject();

                JsonArray names = response.get("names").getAsJsonArray();
                JsonObject vals = response.get("vals").getAsJsonObject();

                for (JsonElement e : names) {
                    JsonObject cat = e.getAsJsonObject();

                    Set<Map.Entry<String, JsonElement>> entry = cat.entrySet();

                    for (Map.Entry<String, JsonElement> j : entry){
                        if (j.getKey().equals("main")) {
                            getSupportActionBar().setTitle(j.getValue().getAsString());
                            JsonArray categoriesJson = vals.get(j.getKey()).getAsJsonArray();
                            for (JsonElement c : categoriesJson)
                                categories.add(c.getAsString());
                            if (firstReqest) {
                                showFragment(CATEGORY_CHOSE);
                                topStep = CATEGORY_CHOSE;
                                firstReqest = false;
                            }
                        } else {
                            facets.put(j.getKey(), j.getValue().getAsString());
                        }
                    }
                }
            }
        });
    }

    public List<String> getCategories() {
        return categories;
    }
}
