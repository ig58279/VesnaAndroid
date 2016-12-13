package ru.cproject.vesnaandroid.activities.map;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qozix.tileview.TileView;
import com.qozix.tileview.hotspots.HotSpot;
import com.qozix.tileview.paths.CompositePathView;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.Settings;
import ru.cproject.vesnaandroid.activities.universal.ProtoMainActivity;
import ru.cproject.vesnaandroid.helpers.map.GraphHelper;
import ru.cproject.vesnaandroid.helpers.ResponseParser;
import ru.cproject.vesnaandroid.obj.map.Edge;
import ru.cproject.vesnaandroid.obj.map.StyleInfo;
import ru.cproject.vesnaandroid.obj.map.Vertex;
import ru.cproject.vesnaandroid.obj.responses.MapResponse;

/**
 * Created by Bitizen on 23.11.16.
 */

public class MapActivity extends ProtoMainActivity {
    private static final String TAG = "MapActivity";

    private TileView mapView;

    private int width;
    private int height;
    private StyleInfo styleInfo;

    private View currentMarker;

    private Vertex startVertex;
    private Vertex endVertex;


    private static float RESPONSE_RADIUS = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_map, contentFrame);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Карта");

        mapView = (TileView) findViewById(R.id.tile_view);

        loadNewMapData();
    }

    private void loadNewMapData() {
        final ProgressDialog loading = ProgressDialog.show(this, "", "Загрузка карты...", true);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        final SharedPreferences mapInfo = getSharedPreferences(Settings.MAP_INFO, MODE_PRIVATE);
//        if (mapInfo.contains(Settings.MapInfo.VERSION))
//            params.put("v", mapInfo.getLong(Settings.MapInfo.VERSION, 0));

        client.get(ServerApi.ROUTES, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (responseString != null)
                    Log.e(TAG, responseString);
                else
                    throwable.printStackTrace();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString);
                if (responseString != null && !responseString.equals("")) {
                    MapResponse response = ResponseParser.parseMapResponse(responseString);
                    Gson gson = new Gson();
                    SharedPreferences.Editor editor = mapInfo.edit();
                    editor
                            .putLong(Settings.MapInfo.VERSION, response.getVersion())
                            .putInt(Settings.MapInfo.WIDTH, response.getWidth())
                            .putInt(Settings.MapInfo.HEIGHT, response.getHeight())
                            .putString(Settings.MapInfo.STYLE_INFO, gson.toJson(response.getStyleInfo()));
                    editor.apply();

                    Realm realm = Realm.getDefaultInstance();

                    realm.beginTransaction();

                    realm.deleteAll();
                    realm.copyToRealm(response.getVerstexs());
                    realm.copyToRealm(response.getEdges());

                    realm.commitTransaction();

                    // TODO Кеэширование тайлов
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                    StrictMode.setThreadPolicy(policy);
//                    MapImagesLoader.loadTilesAndIcons(MapActivity.this, response.getWidth(), response.getHeight(), response.getStyleInfo());
                }
            }

            @Override
            public void onFinish() {
                loading.dismiss();
                initMap();
            }
        });
    }

    private void initMap() {
        SharedPreferences mapInfo = getSharedPreferences(Settings.MAP_INFO, MODE_PRIVATE);
        width = mapInfo.getInt(Settings.MapInfo.WIDTH, 0);
        height = mapInfo.getInt(Settings.MapInfo.HEIGHT, 0);
        Gson gson = new Gson();
        styleInfo = gson.fromJson(mapInfo.getString(Settings.MapInfo.STYLE_INFO, null), StyleInfo.class);
        mapView.setSize(width, height);
        float z = 1;
        for (int i = 3; i >= 0; i--) {
            mapView.addDetailLevel(z, i + "/%d-%d.png");
            z /= 2;
        }
        mapView.setScale(z);

        HotSpot hotSpot = new HotSpot();
        hotSpot.set(0, 0, width, height);
        hotSpot.setHotSpotTapListener(new HotSpot.HotSpotTapListener() {
            @Override
            public void onHotSpotTap(HotSpot hotSpot, int x, int y) {
                float mapX = x / mapView.getScale();
                float mapY = y / mapView.getScale();
                Log.d(TAG, "Tap: " + mapX + " " + mapY );

                Realm realm = Realm.getDefaultInstance();

                RealmQuery<Vertex> query = realm.where(Vertex.class);

                float mapResonseRadius = RESPONSE_RADIUS * mapView.getScale();

                query.between("x", mapX - mapResonseRadius, mapX + mapResonseRadius);
                query.between("y", mapY - mapResonseRadius, mapY + mapResonseRadius);

                RealmResults<Vertex> results = query.findAll();
                if (results.size() != 0) {
                    Vertex vertex = results.get(0);
                    double d = Math.sqrt(Math.pow(mapX - vertex.getX(), 2) + Math.pow(mapY - vertex.getY(), 2));
                    if (results.size() != 1) {
                        for (Vertex v : results) {
                            double d1 = Math.sqrt(Math.pow(v.getX() - vertex.getX(), 2) + Math.pow(v.getY() - vertex.getY(), 2));
                            if (d > d1)
                                vertex = v;
                        }
                    }
                    mapView.slideToAndCenter(vertex.getX(), vertex.getY());
                    showMarker(vertex);
                }
                realm.close();
            }
        });
        mapView.addHotSpot(hotSpot);


        Realm realm = Realm.getDefaultInstance();

        RealmQuery<Vertex> query = realm.where(Vertex.class);

        query.equalTo("type", "escalator");
        query.or().equalTo("type", "lift");

        RealmResults<Vertex> results = query.findAll();

        RealmQuery<Edge> eQuery = realm.where(Edge.class);
        eQuery.equalTo("fromId", results.get(0).getId());
        eQuery.or().equalTo("toId", results.get(0).getId());
        for (int i = 1; i < results.size(); i++) {
            eQuery.or().equalTo("fromId", results.get(i).getId());
            eQuery.or().equalTo("toId", results.get(i).getId());
        }

        RealmResults<Edge> edges = eQuery.findAll();
        CompositePathView.DrawablePath drawablePath = new CompositePathView.DrawablePath();

        for (Edge e: edges) {
            Path path = new Path();
            Vertex begin = null;
            for (Vertex vertex : results) {
                if (e.getFromId() == vertex.getId() || e.getToId() == vertex.getId()) {
                    path.moveTo(vertex.getX(), vertex.getY());
                    begin = vertex;
                    break;
                }
            }

            for (Vertex vertex : results) {
                if (e.getFromId() == vertex.getId() || e.getToId() == vertex.getId() && !begin.equals(vertex)) {
                    path.moveTo(vertex.getX(), vertex.getY());
                    break;
                }
            }

            drawablePath.path = path;
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(styleInfo.getPipeWidth());
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.RED);
            drawablePath.paint = paint;
            mapView.drawPath(drawablePath);
        }

        mapView.setShouldRenderWhilePanning(true);

    }

    private void showMarker(final Vertex v) {
        mapView.removeMarker(currentMarker);
        currentMarker = getLayoutInflater().inflate(R.layout.marker_point, null);
        if (v.getType() != null && v.getType().equals("shops")) {
            TextView shopName = (TextView) currentMarker.findViewById(R.id.shop_name);
            shopName.setText(v.getShopName());
            TextView shopCat = (TextView) currentMarker.findViewById(R.id.shop_categories);
            shopCat.setText(v.getCats());
        } else {
            ViewGroup shopInfo = (ViewGroup) currentMarker.findViewById(R.id.shop_info);
            shopInfo.setVisibility(View.GONE);
        }
        final TextView startButton = (TextView) currentMarker.findViewById(R.id.start_route);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVertex = v;
                tryMakeRoute();
                mapView.removeMarker(currentMarker);
            }
        });
        TextView endButton = (TextView) currentMarker.findViewById(R.id.end_route);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endVertex = v;
                tryMakeRoute();
                mapView.removeMarker(currentMarker);
            }
        });
        mapView.addMarker(currentMarker, v.getX(), v.getY(), -0.5f, -1f);
    }

    private void tryMakeRoute() {
        if (startVertex != null && endVertex != null) {
            Realm realm = Realm.getDefaultInstance();

            RealmQuery<Vertex> vQuery = realm.where(Vertex.class);
            RealmResults<Vertex> vertexes = vQuery.findAllSorted("id");

            List<Vertex> vertexesList = new ArrayList<>();
            for (Vertex v : vertexes) {
                Vertex vertex = new Vertex();
                vertex.setId(v.getId());
                vertex.setX(v.getX());
                vertex.setY(v.getY());
                vertexesList.add(vertex);
            }

            Vertex startV = new Vertex();
            startV.setId(startVertex.getId());
            startV.setX(startVertex.getX());
            startV.setY(startVertex.getY());

            Vertex endV = new Vertex();
            endV.setId(endVertex.getId());
            endV.setX(endVertex.getX());
            endV.setY(endVertex.getY());

            CompositePathView.DrawablePath drawablePath = new CompositePathView.DrawablePath();
            drawablePath.path = GraphHelper.getRoute(GraphHelper.buildPath(vertexesList, startV, endV));
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(styleInfo.getLineWidth());
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(styleInfo.getLineColor());
            drawablePath.paint = paint;
            mapView.drawPath(drawablePath);
        }
    }
}
