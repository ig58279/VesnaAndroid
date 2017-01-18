package ru.cproject.vesnaandroid.activities.map;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qozix.tileview.TileView;
import com.qozix.tileview.hotspots.HotSpot;
import com.qozix.tileview.paths.CompositePathView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.Settings;
import ru.cproject.vesnaandroid.activities.universal.ProtoMainActivity;
import ru.cproject.vesnaandroid.adapters.ShopsAdapter;
import ru.cproject.vesnaandroid.helpers.ExternalStorageBitmapProviderAssets;
import ru.cproject.vesnaandroid.helpers.ResponseParser;
import ru.cproject.vesnaandroid.helpers.RouteBuilder;
import ru.cproject.vesnaandroid.obj.map.Edge;
import ru.cproject.vesnaandroid.obj.map.Graph;
import ru.cproject.vesnaandroid.obj.map.StyleInfo;
import ru.cproject.vesnaandroid.obj.map.Vertex;
import ru.cproject.vesnaandroid.obj.responses.MapResponse;

import static ru.cproject.vesnaandroid.R.id.shop;

/**
 * Created by Bitizen on 23.11.16.
 */

public class MapActivity extends ProtoMainActivity {
    private static final String TAG = "MapActivity";

    private TileView mapView;

    private int width;
    private int height;
    private StyleInfo styleInfo;

    private View currentMarker;   //маркер, появляющийся при нажатии на карту
    private ImageView startMarker;  //звездочка
    private ImageView endMarker;
    private View startPopUpMarker;  //надпись "старт"
    private View finishPopUpMarker;
    private View popUpMarker;      //маркер с одним текствью, используется при переходе с QR или магазина

    private Vertex startVertex;
    private Vertex endVertex;

    DownloadMapTask downloadMapTask;

    private static float RESPONSE_RADIUS = 100;
    ArrayList<File> mapFiles;

    private SharedPreferences mapInfo;
    private MapResponse response;

    List<Vertex> vertexList;
    List<Edge> edgeList;

    CompositePathView.DrawablePath lastDrawPath;

    /**
     * Содержат ID точки при переходе из QR/Shop activity
     */
    private int fromQRPointId;      //intent key : fromQR
    private int fromShopPointId;    //intent key : fromShop


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_map, contentFrame);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Карта");

        Intent intent = getIntent();
        fromQRPointId = intent.getIntExtra("fromQR",-1);
        fromShopPointId = intent.getIntExtra("fromShop",-1);

        mapView = (TileView) findViewById(R.id.tile_view);
        mapView.setBitmapProvider(new ExternalStorageBitmapProviderAssets());

        mapInfo = getSharedPreferences(Settings.MAP_INFO, MODE_PRIVATE);

        loadNewMapData();
    }

    private void loadNewMapData() {
        final ProgressDialog loadingProgressDialog = ProgressDialog.show(this, "", "Загрузка карты...", true);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

         if (mapInfo.contains(Settings.MapInfo.VERSION))
           params.put("v", mapInfo.getLong(Settings.MapInfo.VERSION, 0));

        client.get(ServerApi.ROUTES, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                initMap();
                if (responseString != null)
                    Log.e(TAG, responseString);
                else
                    throwable.printStackTrace();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (responseString != null && !responseString.equals("")) {
                    Log.e(TAG, responseString);
                    response = ResponseParser.parseMapResponse(responseString);
                    Gson gson = new Gson();

                    if(mapInfo.contains(Settings.MapInfo.VERSION)){
                        long version = mapInfo.getLong(Settings.MapInfo.VERSION,0);
                        if(version != response.getVersion())loadMapToCacheDir(response.getHeight(),response.getWidth(),version);
                        else initMap();
                    }else{
                        loadMapToCacheDir(response.getHeight(),response.getWidth(),0);
                    }

                    SharedPreferences.Editor editor = mapInfo.edit();
                    editor.putString(Settings.MapInfo.STYLE_INFO, gson.toJson(response.getStyleInfo()));
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
                loadingProgressDialog.dismiss();
            }
        });
    }

    /**
     * Загружает png файлы с сервера,для вычисления максимального значения x и y используется формула 2^(3-z), где z - зум.
     * @param height высота карты
     * @param width  ширина карты
     */

    private void loadMapToCacheDir(int height,int width,long mapVersion) {
        HashSet<String> urlSet = new HashSet<>();
        for (int i = 0; i <= 3; i++) {
            final float maxX = Math.round((float) width / (Math.pow(2, 3 - i)) / 256);
            final float maxY = Math.round((float) height / (Math.pow(2, 3 - i)) / 256);
            for (int x = 0; x < maxX; x++) {
                final int currentX = x;
                for (int y = 0; y < maxY; y++) {
                    final int currentY = x;
                    Log.e("mapUrl", ServerApi.getTileUrl(i, x, y, mapVersion));
                    urlSet.add(ServerApi.getTileUrl(i, x, y, mapVersion));
                }
            }
        }
        downloadMapTask = new DownloadMapTask();
        downloadMapTask.execute(urlSet);
    }



    private void initMap() {
        Realm realm = Realm.getDefaultInstance();
        vertexList = realm.copyFromRealm(realm.where(Vertex.class).findAll());
        edgeList = realm.copyFromRealm(realm.where(Edge.class).findAll());
        if(vertexList.isEmpty() || edgeList.isEmpty()){
            Toast.makeText(MapActivity.this,"Отсутсвует соединение с интернетом",Toast.LENGTH_SHORT).show();
            onBackPressed();
        }else {

            SharedPreferences mapInfo = getSharedPreferences(Settings.MAP_INFO, MODE_PRIVATE);
            width = mapInfo.getInt(Settings.MapInfo.WIDTH, 0);
            height = mapInfo.getInt(Settings.MapInfo.HEIGHT, 0);
            Gson gson = new Gson();
            styleInfo = gson.fromJson(mapInfo.getString(Settings.MapInfo.STYLE_INFO, null), StyleInfo.class);
            mapView.setSize(width, height);

            float z = 1;
            for (int i = 3; i >= 0; i--) {
                mapView.addDetailLevel(z, MapActivity.this.getExternalFilesDir(null).getPath() + "map/" + i + "/%d-%d.png");
                z /= 2;
            }
            mapView.setScale(z);

            HotSpot hotSpot = new HotSpot();
            hotSpot.set(0, 0, width, height);
            hotSpot.setHotSpotTapListener(new HotSpot.HotSpotTapListener() {
                @Override
                public void onHotSpotTap(HotSpot hotSpot, int x, int y) {
                    mapView.removeMarker(currentMarker);
                    float mapX = x / mapView.getScale();
                    float mapY = y / mapView.getScale();
                    Log.d(TAG, "Tap: " + mapX + " " + mapY);

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
                        showPathMarker(vertex);
                    }
                    realm.close();
                }
            });
            mapView.addHotSpot(hotSpot);


            vertexList = realm.copyFromRealm(realm.where(Vertex.class).findAll());
            edgeList = realm.copyFromRealm(realm.where(Edge.class).findAll());

            RealmQuery<Vertex> query = realm.where(Vertex.class);

            query.equalTo("type", "escalator");
            query.or().equalTo("type", "lift");

            RealmResults<Vertex> escalatorsAndLiftsResults = query.findAll();

            showLiftAndEscalatorIcons(escalatorsAndLiftsResults);


            RealmQuery<Edge> eQuery = realm.where(Edge.class);
            eQuery.equalTo("fromId", escalatorsAndLiftsResults.get(0).getId());
            eQuery.or().equalTo("toId", escalatorsAndLiftsResults.get(0).getId());
            for (int i = 1; i < escalatorsAndLiftsResults.size(); i++) {
                eQuery.or().equalTo("fromId", escalatorsAndLiftsResults.get(i).getId());
                eQuery.or().equalTo("toId", escalatorsAndLiftsResults.get(i).getId());
            }

            RealmResults<Edge> edges = eQuery.findAll();

            CompositePathView.DrawablePath drawablePath;

            for (Edge e : edges) {
                Path path = new Path();
                for (Vertex vertex : escalatorsAndLiftsResults) {
                    if (e.getFromId() == vertex.getId()) {
                        path.moveTo(vertex.getX(), vertex.getY());
                        Vertex destinationVertex = new Vertex();
                        for (Vertex vertex2 : escalatorsAndLiftsResults) {
                            if (vertex2.getId() == e.getToId()) destinationVertex = vertex2;
                        }
                        if ((destinationVertex.getX() == 0 && destinationVertex.getY() == 0) || (vertex.getX() == 0 && vertex.getY() == 0)) {       //на случай если приходят нулевые точки
                            break;
                        }
                        drawablePath = new CompositePathView.DrawablePath();
                        path.lineTo(destinationVertex.getX(), destinationVertex.getY());
                        drawablePath.path = path;
                        Paint paint = new Paint();
                        paint.setAntiAlias(true);
                        paint.setStrokeWidth(styleInfo.getPipeWidth());
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setColor(styleInfo.getPipeColor());
                        drawablePath.paint = paint;
                        mapView.drawPath(drawablePath);
                        break;
                    }
                }

            /*

            for (Vertex vertex : escalatorsAndLiftsResults) {
                if (e.getFromId() == vertex.getId() || e.getToId() == vertex.getId() && !begin.equals(vertex)) {
                    path.moveTo(vertex.getX(), vertex.getY());
                    break;
                }
            }*/


            }

            mapView.setShouldRenderWhilePanning(true);

            if(fromQRPointId != -1){
                for(Vertex v : vertexList){
                    if(v.getId() == fromQRPointId){
                        goToPointAndShowMarker(v,"Вы здесь");
                        break;
                    }
                }
            }
            if(fromShopPointId != -1){
                for(Vertex v : vertexList){
                    if(v.getType() != null && v.getType().equals("shops")) {
                        if (v.getShopId() == fromShopPointId) {
                            goToPointAndShowMarker(v, v.getShopName() != null ? v.getShopName() : "Магазин");
                            break;
                        }
                    }
                }
            }
        }

    }

    /**
     *
     * @param v
     * @param text
     */
    private void goToPointAndShowMarker(final Vertex v,String text){
        mapView.setScale(0.45f);
        popUpMarker = getLayoutInflater().inflate(R.layout.marker_point_start, null);
        TextView textView = (TextView)popUpMarker.findViewById(R.id.shop_name);
        textView.setText(text);
        mapView.addMarker(popUpMarker, v.getX(), v.getY(), -0.5f, -1f);
        mapView.moveToMarker(popUpMarker,false);
    }

    /**
     * Показывает маркер создания маршрута в указанной точке
     * @param v
     */
    private void showPathMarker(final Vertex v) {
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
                if(startMarker != null){
                    mapView.removeMarker(startMarker);
                    if(startPopUpMarker != null)mapView.removeMarker(startPopUpMarker);
                }
               /* if(lastDrawPath != null)
                    mapView.removePath(lastDrawPath);*/
                startMarker = new ImageView(MapActivity.this);
                Glide
                        .with(getBaseContext())
                        .load(ServerApi.getImgUrl(styleInfo.getStartIcon(), false))
                        .sizeMultiplier(0.035f)
                        .into(startMarker);
                mapView.addMarker(startMarker,v.getX(),v.getY(),-0.5f,-0.5f);
                startPopUpMarker = getLayoutInflater().inflate(R.layout.marker_point_start, null);
                mapView.addMarker(startPopUpMarker, v.getX(), v.getY(), -0.5f, -1f);
                startVertex = v;
             /*   if (endVertex == null) {
                    if(endMarker != null)mapView.removeMarker(endMarker);
                    if(finishPopUpMarker != null)mapView.removeMarker(finishPopUpMarker);
                }*/
                tryMakeRoute();
                mapView.removeMarker(currentMarker);
            }
        });
        TextView endButton = (TextView) currentMarker.findViewById(R.id.end_route);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(endMarker != null){
                    mapView.removeMarker(endMarker);
                    if(finishPopUpMarker != null)mapView.removeMarker(finishPopUpMarker);
                }
               /* if(lastDrawPath != null)
                    mapView.removePath(lastDrawPath);*/
                endMarker = new ImageView(MapActivity.this);
                Glide
                        .with(getBaseContext())
                        .load(ServerApi.getImgUrl(styleInfo.getEndIcon(), false))
                        .sizeMultiplier(0.035f)
                        .into(endMarker);
                mapView.addMarker(endMarker,v.getX(),v.getY(),-0.5f,-0.5f);

                finishPopUpMarker = getLayoutInflater().inflate(R.layout.marker_point_finish, null);
                mapView.addMarker(finishPopUpMarker, v.getX(), v.getY(), -0.5f, -1f);

                endVertex = v;
               /* if (startVertex == null) {
                    if(startMarker != null)mapView.removeMarker(startMarker);
                    if(startPopUpMarker != null)mapView.removeMarker(startPopUpMarker);
                }*/
                tryMakeRoute();
                mapView.removeMarker(currentMarker);

            }
        });
        mapView.addMarker(currentMarker, v.getX(), v.getY(), -0.5f, -1f);
    }



    private void tryMakeRoute() {

        if (startVertex != null && endVertex != null) {

            Toast.makeText(MapActivity.this,"Загрузка...",Toast.LENGTH_SHORT).show();

            if(lastDrawPath != null)
            mapView.removePath(lastDrawPath);

            SparseArray<Vertex> sparseArray = new SparseArray<>();   //для получения точки по её id
            for(Vertex vertex : vertexList){
                sparseArray.append(vertex.getId(),vertex);
            }

            for(int i = 0;i < edgeList.size(); i++){
                Edge edge = edgeList.get(i);
                if(sparseArray.get(edge.getFromId())!=null && sparseArray.get(edge.getToId())!=null){
                    Vertex fromVertex = sparseArray.get(edge.getFromId());
                    Vertex toVertex = sparseArray.get(edge.getToId());
                    edge.setSource(fromVertex);
                    edge.setDestination(toVertex);
                    if(fromVertex.getType() != null && toVertex.getType() != null) {
                        if (fromVertex.getType().equals("lift") && toVertex.getType().equals("lift")) {
                            Log.e("Lift Cost",String.valueOf(edge.getCost()));
                            edge.setCost(3);
                        }
                        if (fromVertex.getType().equals("escalator") && toVertex.getType().equals("escalator")) {
                            Log.e("Escalator Cost",String.valueOf(edge.getCost()));
                            edge.setCost(3);
                        }
                    }
                }
            }

            Graph graph = new Graph(vertexList, edgeList);
            RouteBuilder routeBuilder = new RouteBuilder(graph);
            routeBuilder.execute(vertexList.get(startVertex.getId()));
            LinkedList<Vertex> pathList = routeBuilder.getPath(vertexList.get(endVertex.getId()));

            if(pathList!=null) {
                for (Vertex vertex : pathList) {
                    Log.e("Path:", vertex.toString());
                }
                lastDrawPath = new CompositePathView.DrawablePath();

                Path path = new Path();

                for(int i = 0;i < pathList.size()-1;i++){
                    Vertex from = pathList.get(i);
                    Vertex to = pathList.get(i+1);
                    for(Edge edge : edgeList){
                        if((edge.getSource().getId() == from.getId() && edge.getDestination().getId() == to.getId()) || (edge.getSource().getId() == to.getId() && edge.getDestination().getId() ==from.getId())){
                            path.moveTo(from.getX(),from.getY());
                            path.lineTo(to.getX(),to.getY());
                        }
                    }
                }
                lastDrawPath.path = path;
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setStrokeWidth(styleInfo.getLineWidth());
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(styleInfo.getLineColor());
                lastDrawPath.paint = paint;
                mapView.drawPath(lastDrawPath);

            }else{
                Log.e("Path:"," NULL");
            }

        }
    }

    /**
     * Отображает иконки лифтов и эскалаторов и кэширует их
     * @param results выборка точек лифтов и эскалаторов из БД
     */
    private void showLiftAndEscalatorIcons(RealmResults<Vertex> results){
        for(Vertex vertex : results){
            if(vertex.getType().equals("lift")){
                ImageView imageView = new ImageView(this);
                Glide
                        .with(getBaseContext())
                        .load(ServerApi.getImgUrl(styleInfo.getLiftIcon(), false))
                        .sizeMultiplier(0.055f)
                        .into(imageView);
                mapView.addMarker(imageView,vertex.getX(),vertex.getY(),-0.5f,-0.5f);
            }
            if(vertex.getType().equals("escalator")){
                ImageView imageView = new ImageView(this);
                Glide
                        .with(getBaseContext())
                        .load(ServerApi.getImgUrl(styleInfo.getEscalatorIcon(), false))
                        .sizeMultiplier(0.055f)
                        .into(imageView);
                mapView.addMarker(imageView,vertex.getX(),vertex.getY(),-0.5f,-0.5f);
            }
        }
    }

    void deleteMapFiles(){
        File mapDirectory = new File(MapActivity.this.getExternalFilesDir(null).getPath() + "map");
        if(mapDirectory.exists() ) deleteRecursive(mapDirectory);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private static void copyFileUsingFileStreams(File source, File dest)
            throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } finally {
            if(input!=null)input.close();
            if(output!=null)output.close();
        }
    }


    class DownloadMapTask extends AsyncTask<Set<String>, Integer, Void> {
        String externalPathDir;
        String currentUrl;
        int cnt = 0;
        ProgressDialog progressDialog = new ProgressDialog(MapActivity.this);

        @Override
        protected void onCancelled() {
            super.onCancelled();
            deleteMapFiles();
            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            externalPathDir = MapActivity.this.getExternalFilesDir(null).getPath();
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("");
            progressDialog.setMessage("Загрузка карты");
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    new AlertDialog.Builder(MapActivity.this)
                            .setTitle("")
                            .setMessage("Отменить загрузку карты?")
                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    downloadMapTask.cancel(true);
                                    deleteMapFiles();
                                    MapActivity.this.onBackPressed();
                                }
                            })
                            .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    downloadMapTask.progressDialog.show();
                                }
                            })
                            .create()
                            .show();
                }
            });
            progressDialog.show();
            Log.e("AsyncTask","PreExecute");
        }

        @Override
        protected Void doInBackground(Set<String>... params) {
            Set<String> urls = params[0];
            mapFiles = new ArrayList<>();
            progressDialog.setMax(urls.size());
           // final File dirFile = new File(MapActivity.this.getExternalFilesDir(null).getPath() + "/1", "4");
            SyncHttpClient syncHttpClient = new SyncHttpClient();
            FileAsyncHttpResponseHandler fileAsyncHttpResponseHandler = new FileAsyncHttpResponseHandler(MapActivity.this) {

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, File file) {
                    if (file != null){
                        Log.e("File", "downloaded");
                        String shortUrl = currentUrl.substring(52).replace(".png","");
                        String[] separated = shortUrl.split("/");
                        int z = Integer.valueOf(separated[0]);
                        int x = Integer.valueOf(separated[1]);
                        int y = Integer.valueOf(separated[2].substring(0,separated[2].indexOf("?")));
                        File dirFile = new File(externalPathDir + "map", "/"+z);
                        File destFile;
                        if(!dirFile.exists()){
                            if(dirFile.mkdirs()){
                                Log.e("File", "Directory created ");
                            }
                            else Log.e("File", "Directory isn't created ");  // TODO: 26.12.16 Обрабатывать
                        }
                        destFile = new File(dirFile,x+"-"+y+".png");
                        try{
                            copyFileUsingFileStreams(file,destFile);
                        }catch (IOException e){
                            Log.e("CopyFile","Не пошло"+e.getMessage());
                        }
                    }
                    publishProgress(cnt++);
                          /* MapActivity.this.getExternalFilesDir();
                           File ile = new File()*/
                }
            };
            fileAsyncHttpResponseHandler.setUseSynchronousMode(true);
            for(String url : urls){
                currentUrl = url;
                syncHttpClient.get(url, fileAsyncHttpResponseHandler);
            }
            return null;
        }



        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
            Log.e("AsyncTask","Downloaded " + values[0] + " files");
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            cnt = 0;
            saveMapInfoAndRestartMap();
            Log.e("AsyncTask","End");
        }
    }

    void deleteRecursive(File dir) {
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            Log.e("DeleteRecursive", "Start");
            for (int i = 0; i < children.length; i++)
            {
                File temp = new File(dir, children[i]);
                if (temp.isDirectory())
                {
                    Log.e("DeleteRecursive", "Recursive Call" + temp.getPath());
                    deleteRecursive(temp);
                }
                else
                {
                    Log.e("DeleteRecursive", "Delete File" + temp.getPath());
                    boolean b = temp.delete();
                    if (!b)
                    {
                        Log.e("DeleteRecursive", "DELETE FAIL");
                    }
                }
            }

        }
        dir.delete();
    }

    /**
     * Сохраняет данные о карте и перезагружает её
     */
    private void saveMapInfoAndRestartMap(){
        Gson gson = new Gson();
        SharedPreferences.Editor editor = mapInfo.edit();
        editor
                .putLong(Settings.MapInfo.VERSION, response.getVersion())
                .putInt(Settings.MapInfo.WIDTH, response.getWidth())
                .putInt(Settings.MapInfo.HEIGHT, response.getHeight());
        editor.apply();
        initMap();
    }





}
