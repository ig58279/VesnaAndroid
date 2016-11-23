package ru.cproject.vesnaandroid.activities.map;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.qozix.tileview.TileView;

import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.activities.universal.ProtoMainActivity;

/**
 * Created by Bitizen on 23.11.16.
 */

public class MapActivity extends ProtoMainActivity {

    private TileView mapView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_map, contentFrame);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Карта");

        mapView = (TileView) findViewById(R.id.tile_view);

        mapView.setSize(1001, 1606);
        mapView.setScaleLimits(0.1f, 1f);
        mapView.addDetailLevel(0.1f, "0/%d-%d.png", 256, 256);
        mapView.addDetailLevel(0.3f, "1/%d-%d.png", 256, 256);
        mapView.addDetailLevel(0.6f, "2/%d-%d.png", 256, 256);
        mapView.addDetailLevel(1f, "3/%d-%d.png", 256, 256);
        mapView.setScale(1f);

    }
}
