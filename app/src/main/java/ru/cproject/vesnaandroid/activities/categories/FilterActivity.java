package ru.cproject.vesnaandroid.activities.categories;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.activities.universal.ProtoMainActivity;
import ru.cproject.vesnaandroid.fragments.filter.CategoriesFragment;
import ru.cproject.vesnaandroid.fragments.filter.FacetParamFragment;
import ru.cproject.vesnaandroid.fragments.filter.FacetsFragment;
import ru.cproject.vesnaandroid.obj.Facet;

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

    private String catsTitle = "Фильтр";
    private String main;
    private List<String> categories = new ArrayList<>(); // категории - относится к первому экрану
    private List<Facet> facets = new ArrayList<>(); // отображаемые значения для вторго экрана
    private Map<String, String> chosenParams = new HashMap<>(); // уже выбраные параметры
    private Map<String, List<String>> facetsPrams = new HashMap<>(); // список возможных вариантов для фасета - 3 экран

    private int topStep = NONE;
    private int currentStep = NONE;
    private static final int NONE = -1;
    private static final int CATEGORY_CHOSE = 0;
    private static final int FACETS_CHOSE = 1;
    private static final int FACET_PARAM_CHOSE = 2;

    private boolean firstReqest = true;

    private FrameLayout fragmentFrame;

    private MenuItem ok;
    private boolean okVisibility = false;

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
        requestCats(null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        menu.findItem(R.id.ok).setVisible(okVisibility);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ok:
                formResult();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    private void formResult() {
        Intent returnIntent = new Intent();
        JsonObject result = new JsonObject();
        if (main != null) {
            JsonArray main = new JsonArray();
            main.add(this.main);
            result.add("main", main);
        }
        for (Map.Entry<String, String> e : chosenParams.entrySet()) {
            JsonArray cat = new JsonArray();
            cat.add(e.getValue());
            result.add(e.getKey(), cat);
        }
        returnIntent.putExtra("result", result.toString());
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    public void stepBack() {
        if (currentStep == FACETS_CHOSE)
            facets.clear();
        currentStep--;
        showFragment(currentStep, null, R.anim.transition_left_in, R.anim.transition_right_out);
    }

    private void showFragment(int fragment, String facet, int enterAnim, int exitAnim) {
        switch (fragment) {
            case CATEGORY_CHOSE:
                okVisibility = false;
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(enterAnim, exitAnim)
                        .replace(fragmentFrame.getId(), new CategoriesFragment())
                        .commit();
                break;
            case FACETS_CHOSE:
                okVisibility = true;
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(enterAnim, exitAnim)
                        .replace(fragmentFrame.getId(), new FacetsFragment())
                        .commit();
                break;
            case FACET_PARAM_CHOSE:
                okVisibility = false;
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(enterAnim, exitAnim)
                        .replace(fragmentFrame.getId(), FacetParamFragment.newInstance(facet))
                        .commit();
                break;
        }
        currentStep = fragment;
        invalidateOptionsMenu();
    }

    public void requestCats(@Nullable final String main, @Nullable final String facet) {
        SyncHttpClient client = new SyncHttpClient();
        JsonObject catsParams = new JsonObject();
        catsParams.addProperty("mod", mod);

        if (facet != null) {
            JsonArray facets = new JsonArray();
            facets.add(facet);
            catsParams.add("facet", facets);
        }

        if (main != null) {
            JsonObject cats = new JsonObject();
            JsonArray mainJson = new JsonArray();
            mainJson.add(main);
            cats.add("main", mainJson);
            catsParams.add("cats", cats);
        }

        StringEntity entity = new StringEntity(catsParams.toString(), "UTF-8");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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
                            catsTitle = j.getValue().getAsString();
                            JsonArray categoriesJson = vals.get(j.getKey()).getAsJsonArray();
                            for (JsonElement c : categoriesJson)
                                categories.add(c.getAsString());
                            if (firstReqest) {
                                showFragment(CATEGORY_CHOSE, null, R.anim.transition_right_in, R.anim.transition_left_out);
                                topStep = CATEGORY_CHOSE;
                                firstReqest = false;
                            }
                        } else {
                            if (firstReqest) {
                                showFragment(FACETS_CHOSE, null, R.anim.transition_right_in, R.anim.transition_left_out);
                                topStep = FACETS_CHOSE;
                                firstReqest = false;
                            }

                            if (!isFacetContain(j.getKey()))
                                facets.add(new Facet(j.getKey(), j.getValue().getAsString()));
                            if (facet != null && !facetsPrams.containsKey(facet)) {
                                List<String> facetParam = new ArrayList<>();
                                for (JsonElement facetString : vals.get(facet).getAsJsonArray())
                                    facetParam.add(facetString.getAsString());
                                facetsPrams.put(facet, facetParam);
                            }
                        }
                    }
                }
            }
        });
    }

    public void openFacetChose(String main) {
        requestCats(main, null);
        showFragment(FACETS_CHOSE, null, R.anim.transition_right_in, R.anim.transition_left_out);
    }

    public void openFacetParams(String facet) {
        requestCats(this.main, facet);
        showFragment(FACET_PARAM_CHOSE, facet, R.anim.transition_right_in, R.anim.transition_left_out);
    }


    public List<String> getCategories() {
        return categories;
    }

    public List<Facet> getFacets() {
        return facets;
    }

    public Map<String, String> getChosenParams() {
        return chosenParams;
    }

    public Map<String, List<String>> getFacetsPrams() {
        return facetsPrams;
    }

    public void setParam(String facet, String param) {
        chosenParams.put(facet, param);
    }

    public void setMain(String main) {
        this.main = main;
    }

    private boolean isFacetContain(String facet) {
        boolean isContain = false;
        for (Facet f : facets) {
            if (f.getFacet().equals(facet))  {
                isContain = true;
                break;
            }
        }
        return isContain;
    }
}
