package ru.cproject.vesnaandroid.activities;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.adapters.holders.SearchAdapter;
import ru.cproject.vesnaandroid.obj.Search;

/**
 * Created by andro on 22.11.2016.
 */

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Search> list = new ArrayList<>();

    private SearchAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        int color = ContextCompat.getColor(this, R.color.colorTextBlack);
        toolbar.getNavigationIcon().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new SearchAdapter(this, list);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        for (int i = 0; i < 10; i++) {
            list.add(new Search(0, "stocks", "adidas", ""));
        }

        adapter.notifyDataSetChanged();
    }
}
