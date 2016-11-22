package ru.cproject.vesnaandroid.activities.categories;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.activities.universal.ProtoMainActivity;
import ru.cproject.vesnaandroid.obj.Category;

/**
 * Created by Bitizen on 07.11.16.
 */

public class CategoriesActivity extends ProtoMainActivity {

    private RecyclerView categoriesView;
    private Category[] categories;

    private int style;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Intent intent = getIntent();
        style = intent.getIntExtra("style", R.style.AppTheme);
        setTheme(style);
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_categories, contentFrame);

        getSupportActionBar().setTitle("Категории");


    }
}
