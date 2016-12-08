package ru.cproject.vesnaandroid.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import java.util.ArrayList;

import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.activities.events.SingleEventActivity;
import ru.cproject.vesnaandroid.activities.shops.SingleShopActivity;
import ru.cproject.vesnaandroid.activities.stocks.SingleStockActivity;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.data;
import static ru.cproject.vesnaandroid.R.id.stock;
import static ru.cproject.vesnaandroid.helpers.DinamicPermissionsHelper.CAMERA_REQUEST_CODE;

/**
 * Created by andro on 29.11.2016.
 */

public class QRCodeReaderActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {
    private static final String tcIndex = "vs";

    private Context context;
    private Boolean readCode = false;
    private ImageView circleButton;

    QRCodeReaderView decoderview;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST_CODE);
        } else {
            setContentView(R.layout.activity_qr_reader);
            decoderview = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
            decoderview.setOnQRCodeReadListener(this);
            context = this;
            decoderview.setBackCamera();
            circleButton = (ImageView) findViewById(R.id.circle);
            circleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        if (readCode) return;
        readCode = true;
        Log.e("QR data", text);
        if (text.startsWith("http://") || text.startsWith("https://")) {
            text = text.substring(text.indexOf("://") + 3);
        }
        Log.e("QR data", text);
        if (text.contains("m0p.ru")) {
            if (text.contains(tcIndex + ".m0p.ru")) {
                if (text.contains("adv")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(QRCodeReaderActivity.this);
                    builder.setTitle("Акция")
                            .setMessage("Скидки и подарки ждут Вас!");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                if (text.contains("coupone")) {
                    String id = getLastBitFromUrl(text);
                    try {
                        int stockId = Integer.valueOf(id);
                        Intent intent = new Intent(context, SingleStockActivity.class);
                        intent.putExtra("id", stockId);
                        context.startActivity(intent);
                    } catch (Exception e) {}
                }
                if (text.contains("shop")) {
                    String id = getLastBitFromUrl(text);
                    try {
                        int shopId = Integer.valueOf(id);
                        Intent intent = new Intent(context, SingleShopActivity.class);
                        intent.putExtra("id", shopId);
                        context.startActivity(intent);
                    } catch (Exception e) {}
                }
                if (text.contains("stock")) {
                    String id = getLastBitFromUrl(text);
                    try {
                        int stockId = Integer.valueOf(id);
                        Intent intent = new Intent(context, SingleStockActivity.class);
                        intent.putExtra("id", stockId);
                        context.startActivity(intent);
                    } catch (Exception e) {}
                }
                if (text.contains("event")) {
                    String id = getLastBitFromUrl(text);
                    try {
                        int eventId = Integer.valueOf(id);
                        Intent intent = new Intent(context, SingleEventActivity.class);
                        intent.putExtra("id", eventId);
                        context.startActivity(intent);
                    } catch (Exception e) {}
                }
                if (text.contains("route")) {
                    //TODO qr для карты
                }
            } else
                notAppAlert();
        } else
            notAppAlert();
    }


    private void notAppAlert () {
        AlertDialog.Builder builder = new AlertDialog.Builder(QRCodeReaderActivity.this);
        builder.setTitle("Ошибка")
                .setMessage("QR - код не относиться к данному приложению")
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public String getLastBitFromUrl(final String url){
        return url.replaceFirst(".*/([^/?]+).*", "$1");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            decoderview.startCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            decoderview.stopCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setContentView(R.layout.activity_qr_reader);
                    decoderview = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
                    decoderview.setOnQRCodeReadListener(this);
                    context = this;
                    decoderview.setBackCamera();
                    circleButton = (ImageView) findViewById(R.id.circle);
                    circleButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });
                    onResume();
                } else {
                    finish();
                }
            }
        }
    }
}
