package ru.cproject.vesnaandroid.activities.universal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TableLayout;


import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.helpers.ViewCreatorHelper;

/**
 * Created by Bitizen on 26.10.16.
 */

public abstract class ProtoMainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    protected FrameLayout contentFrame;

    private SlidingUpPanelLayout drawer;
    protected ViewGroup drawerBack;

    private TableLayout menu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proto_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        contentFrame = (FrameLayout) findViewById(R.id.content_frame);
        drawer = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        drawerBack = (ViewGroup) findViewById(R.id.drawer_back);
        menu = (TableLayout) findViewById(R.id.menu);
        setSupportActionBar(toolbar);

        float dpi = getResources().getDisplayMetrics().density;
        drawer.setPanelHeight((int)(43 * dpi));
        ViewCreatorHelper.createMenu(this, menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
