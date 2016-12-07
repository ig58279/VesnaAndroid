package ru.cproject.vesnaandroid.activities.map;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.qozix.tileview.TileView;
import com.qozix.tileview.hotspots.HotSpot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.activities.universal.ProtoMainActivity;
import ru.cproject.vesnaandroid.helpers.ResponseParser;
import ru.cproject.vesnaandroid.obj.map.MapData;
import ru.cproject.vesnaandroid.obj.map.Vertex;

/**
 * Created by Bitizen on 23.11.16.
 */

public class MapActivity extends ProtoMainActivity {

    private TileView mapView;

    private MapData mapData;

    private static final int REACT_RANGE = 20000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_map, contentFrame);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Карта");

        mapView = (TileView) findViewById(R.id.tile_view);

        mapView.setSize(5120 / 2, 6144 / 2);
        mapView.addDetailLevel(3, "0/%d-%d.png", 256, 256);
        mapView.addDetailLevel(2, "1/%d-%d.png", 256, 256);
        mapView.addDetailLevel(1, "2/%d-%d.png", 256, 256);
        mapView.addDetailLevel(0, "3/%d-%d.png", 256, 256);

        StringBuilder buf = new StringBuilder();
        try {
            InputStream json = null;
            json = getAssets().open("map.json");
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;

            while ((str = in.readLine()) != null) {
                buf.append(str);
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mapData = ResponseParser.parseMapInfo(buf.toString());

        for (Vertex v : mapData.getVertexList()) {
            HotSpot hotspot = new HotSpot();
            hotspot.set(new Rect((int) (v.getX() / 2 - REACT_RANGE), (int)(v.getY() / 2 - REACT_RANGE), (int)(v.getX() / 2 + REACT_RANGE), (int)(v.getY() / 2 + REACT_RANGE)));
            hotspot.setTag("TAP");
            mapView.addHotSpot(hotspot);
        }

        mapView.setHotSpotTapListener(new HotSpot.HotSpotTapListener() {
            @Override
            public void onHotSpotTap(HotSpot hotSpot, int x, int y) {
                Log.e("hotSpot", (String) hotSpot.getTag());
            }
        });
    }
}
