package ru.cproject.vesnaandroid.activities.universal;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TableLayout;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.activities.SearchActivity;
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

    protected int color;

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

        TypedValue typedValue = new TypedValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
            color = typedValue.data;
        } else {
            // TODO вычисление цвета для андроидов нижу 21
            color = ContextCompat.getColor(this, R.color.colorPrimary); // не решение
        }
        drawerBack.setBackgroundColor(color);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO: 28.11.16 для магазинов нужно или закрывать меню(если в тот же пункт) или открывать по новой(если не)
        drawer.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
