package ru.cproject.vesnaandroid.helpers;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import junit.framework.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.ServerApi;
import ru.cproject.vesnaandroid.Settings;
import ru.cproject.vesnaandroid.activities.QRCodeReaderActivity;
import ru.cproject.vesnaandroid.activities.events.MainEventsActivity;
import ru.cproject.vesnaandroid.activities.films.MainFilmsActivity;
import ru.cproject.vesnaandroid.activities.map.MapActivity;
import ru.cproject.vesnaandroid.activities.shops.MainShopsActivity;
import ru.cproject.vesnaandroid.activities.stocks.MainStocksActivity;
import ru.cproject.vesnaandroid.helpers.wheel.OnWheelChangedListener;
import ru.cproject.vesnaandroid.helpers.wheel.SimpleAdapter;
import ru.cproject.vesnaandroid.helpers.wheel.WheelView;
import ru.cproject.vesnaandroid.obj.Category;
import ru.cproject.vesnaandroid.obj.Shop;
import ru.cproject.vesnaandroid.obj.mall.Function;
import ru.cproject.vesnaandroid.obj.mall.MallInfo;
import ru.cproject.vesnaandroid.obj.map.Vertex;

import static ru.cproject.vesnaandroid.R.id.stock;
import static ru.cproject.vesnaandroid.helpers.DinamicPermissionsHelper.CAMERA_REQUEST_CODE;

/**
 * Created by Bitizen on 27.10.16.
 */

public class ViewCreatorHelper {

    public static void createMenu(final Context context, TableLayout menu) {

        SharedPreferences mallInfo = context.getSharedPreferences(Settings.MALL_INFO, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Function[] functions = gson.fromJson(mallInfo.getString(Settings.MallInfo.FUNCTIONAL, ""), Function[].class);
        List<MenuElement> menuElements = new ArrayList<>();
        for (Function function : functions) {
            MenuElement element = new MenuElement(context, function);
            menuElements.add(element);
        }
        TableRow row = null;
        for (int i = 0; i < menuElements.size(); i++) {
            if (!menuElements.get(i).isHome())
                continue;
            if (row == null || row.getChildCount() == 3) {
                row = new TableRow(context);
                row.setGravity(Gravity.LEFT);
                menu.addView(row);
            }
            View itemMenu = LayoutInflater.from(context).inflate(R.layout.item_menu, row, false);
            ViewGroup wrapper = (ViewGroup) itemMenu.findViewById(R.id.wrapper);
            ImageView icon = (ImageView) itemMenu.findViewById(R.id.icon);
            TextView title = (TextView) itemMenu.findViewById(R.id.title);
            icon.setImageResource(menuElements.get(i).getIconRes());
            title.setText(menuElements.get(i).getTitle());
            wrapper.setOnClickListener(menuElements.get(i).getOnClickListener());
            row.addView(itemMenu);
        }
    }

    public static Spannable spannableText(Context context, List<Category> categories, String type, int color) {
    //TODO Разобраться с type
        String catsString = "";
        if (categories.size() != 0) {
            catsString = categories.get(0).getCategories();
            for (int i = 1; i < categories.size(); i++) {
                catsString += "  &  " + categories.get(i).getCategories();
            }
        }

        Spannable text = new SpannableString(catsString + "\n");
        while (catsString.contains("&")) {
            Drawable dot = ContextCompat.getDrawable(context, R.drawable.item_categories_circle);
            dot.setBounds(0, 0, dot.getIntrinsicWidth(), dot.getIntrinsicHeight());
            dot.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            ImageSpan image = new ImageSpan(dot, ImageSpan.ALIGN_BASELINE);
            text.setSpan(image, catsString.indexOf("&"), catsString.indexOf("&") + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            text.setSpan(new ForegroundColorSpan(color), 0, catsString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            catsString = catsString.replaceFirst("&", "*");
        }
        return text;
    }

    public static View createShopCard(final Context context, ViewGroup content, final Shop shop, int color) {
        View shopView = LayoutInflater.from(context).inflate(R.layout.tab_shop, content, false);
        TextView categories = (TextView) shopView.findViewById(R.id.categories);
        categories.setText(spannableText(context, shop.getCategories(), "", color));
        TextView shopName = (TextView) shopView.findViewById(R.id.shop_name);
        shopName.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        shopName.setText(shop.getName());
        LinearLayout info = (LinearLayout) shopView.findViewById(R.id.info);
        if (shop.getComplements() != null) {
            for (int i = 0; i < shop.getComplements().size(); i++) {
                View view = LayoutInflater.from(context).inflate(R.layout.info_complement, info, false);
                ImageView icon = (ImageView) view.findViewById(R.id.icon);
                TextView text = (TextView) view.findViewById(R.id.info);
                final int finalI = i;
                switch (shop.getComplements().get(i).getKey()) {
                    case "phone":
                        icon.setImageResource(R.drawable.ic_phone);
                        text.setText(shop.getComplements().get(i).getParametr());
                        text.setBackgroundResource(R.drawable.active_info_background);
                        info.addView(view);
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + shop.getComplements().get(finalI).getParametr()));
                                context.startActivity(intent);
                            }
                        });
                        break;
                    case "site":
                        icon.setImageResource(R.drawable.ic_site);
                        text.setText(shop.getComplements().get(i).getParametr());
                        text.setBackgroundResource(R.drawable.active_info_background);
                        info.addView(view);
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(shop.getComplements().get(finalI).getParametr()));
                                context.startActivity(i);
                            }
                        });
                        break;
                    case "mode":
                        icon.setImageResource(R.drawable.ic_mode_watch);
                        text.setText(shop.getComplements().get(i).getParametr());
                        info.addView(view);
                }
            }
        }

        ImageView logo = (ImageView) shopView.findViewById(R.id.logo);
        Picasso
                .with(context)
                .load(ServerApi.getImgUrl(shop.getLogo(), true))
                .fit()
                .centerInside()
                .into(logo);
        return shopView;
    }

    private static class MenuElement {

        private int iconRes;
        private String title;
        private View.OnClickListener onClickListener;
        private boolean isHome;

        void fillLettersArrayList(ArrayList<String> arrayList){
            String[] letters = {"А","Б","В","Г","Д","Е","Ж","З","И","К","Л"};   //я у мамы индус
            for(int i = 0;i<letters.length;i++){
                arrayList.add(letters[i]);
            }
        }

        ArrayList<String> getNumbersArrayList(String letterPick){
            ArrayList<String> stringArrayList = new ArrayList<>();
            int maxIterationCount;
            switch(letterPick){
                case "А":
                    maxIterationCount = 7;
                    break;

                case "Б":

                    maxIterationCount = 7;
                    break;

                case "В":

                    maxIterationCount = 18;
                    break;

                case "Г":

                    maxIterationCount = 22;
                    break;

                case "Д":

                    maxIterationCount = 22;
                    break;

                case "Е":

                    maxIterationCount = 22;
                    break;

                case "Ж":

                    maxIterationCount = 23;
                    break;

                default:

                    maxIterationCount = 48;

            }

            for(int i = 0;i < maxIterationCount;i++){
                if (letterPick.equals("А") || letterPick.equals("Б")){
                    stringArrayList.add(String.valueOf(i+3));

                }else{
                    stringArrayList.add(String.valueOf(i+1));
                }

            }

            if (letterPick.equals("Б")){
                stringArrayList.add(String.valueOf(14));
            }
            if (letterPick.equals("В")){
                stringArrayList.remove(10);
                stringArrayList.remove(11);
            }
            if (letterPick.equals("Д") || letterPick.equals("Е") || letterPick.equals("Ж")){
                stringArrayList.add(String.valueOf(32));
            }
            if (letterPick.equals("Е") || letterPick.equals("Ж")){
                stringArrayList.add(String.valueOf(33));
                stringArrayList.add(String.valueOf(34));
                stringArrayList.add(String.valueOf(35));
                stringArrayList.add(String.valueOf(36));
            }
            if (letterPick.equals("Ж")){
                stringArrayList.add(String.valueOf(26));
                stringArrayList.add(String.valueOf(27));
            }

            return stringArrayList;
        }

        MenuElement(final Context context, final Function function) {
            switch (function.getType()) {
                case "map":
                    this.iconRes = R.drawable.ic_map;
                    this.title = "Карта";
                    this.onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            context.startActivity(new Intent(context, MapActivity.class));
                        }
                    };
                    break;
                case "shops":
                    this.iconRes = R.drawable.ic_shops;
                    this.title = "Магазины";
                    this.onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, MainShopsActivity.class);
                            intent.putExtra("style", R.style.ShopsTheme);
                            intent.putExtra("mod", "shops");
                            context.startActivity(intent);
                        }
                    };
                    break;
                case "films":
                    this.iconRes = R.drawable.ic_cinema;
                    this.title = "Кино";
                    this.onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, MainFilmsActivity.class);
                            context.startActivity(intent);
                        }
                    };
                    break;
                case "food":
                    this.iconRes = R.drawable.ic_food;
                    this.title = "Рестораны";
                    this.onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, MainShopsActivity.class);
                            intent.putExtra("style", R.style.FoodTheme);
                            intent.putExtra("category", "Рестораны");
                            intent.putExtra("mod", "food");
                            context.startActivity(intent);
                        }
                    };
                    break;
                case "qr":
                    this.iconRes = R.drawable.ic_qr;
                    this.title = "Cканер QR";
                    this.onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            context.startActivity(new Intent(context, QRCodeReaderActivity.class));
                        }
                    };
                    break;
                case "parking":
                    this.iconRes = R.drawable.ic_parking;
                    this.title = "Парковка";
                    this.onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String parkingPosition = context.getSharedPreferences(Settings.COMMON,Context.MODE_PRIVATE).getString(Settings.Common.parkingPosition,"");
                            if(!parkingPosition.isEmpty()) {
                                new AlertDialog.Builder(context)
                                        .setTitle("")
                                        .setPositiveButton("ОК",null)
                                        .setMessage("Ваше парковочное место: " + parkingPosition)
                                        .show();
                            }else{
                                View outerView = LayoutInflater.from(context).inflate(R.layout.parking_dialog, null);
                                final WheelView letterWv = (WheelView) outerView.findViewById(R.id.letter_wv);
                                final WheelView numberWv = (WheelView) outerView.findViewById(R.id.number_wv);

                                final ArrayList<String> lettersArrayList = new ArrayList<>();

                                fillLettersArrayList(lettersArrayList);
                                letterWv.setViewAdapter(new SimpleAdapter(lettersArrayList,context));
                                letterWv.setVisibleItems(3);
                                letterWv.setCurrentItem(0);
                                letterWv.addChangingListener(new OnWheelChangedListener() {
                                    @Override
                                    public void onChanged(WheelView wheel, int oldValue, int newValue) {
                                        numberWv.setViewAdapter(new SimpleAdapter(getNumbersArrayList(lettersArrayList.get(newValue)),context));
                                        numberWv.setVisibleItems(3);
                                        numberWv.setCurrentItem(0);
                                    }
                                });
                                numberWv.setViewAdapter(new SimpleAdapter(getNumbersArrayList("А"),context));
                                numberWv.setVisibleItems(3);
                                numberWv.setCurrentItem(0);

                                new AlertDialog.Builder(context)
                                        .setTitle("Выберите парковочное место")
                                        .setView(outerView)
                                        .setNegativeButton("Отмена",null)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String parkingPlace = lettersArrayList.get(letterWv.getCurrentItem()) + "-" + getNumbersArrayList(lettersArrayList.get(letterWv.getCurrentItem())).get(numberWv.getCurrentItem());
                                                Intent intent = new Intent(context, MapActivity.class);
                                                intent.putExtra("fromParking",parkingPlace);
                                                context.startActivity(intent);
                                            }
                                        })
                                        .show();
                            }

                        }
                    };
                    break;
                case "events":
                    this.iconRes = R.drawable.ic_events;
                    this.title = "События";
                    this.onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, MainEventsActivity.class));
                        }
                    };
                    break;
                case "fun":
                    this.iconRes = R.drawable.ic_fun;
                    this.title = "Развлечения";
                    this.onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, MainShopsActivity.class);
                            intent.putExtra("style", R.style.FunTheme);
                            intent.putExtra("category", "Развлечения");
                            intent.putExtra("mod", "fun");
                            context.startActivity(intent);
                        }
                    };
                    break;
                case "stocks":
                    this.iconRes = R.drawable.ic_sale;
                    this.title = "Акции";
                    this.onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            context.startActivity(new Intent(context, MainStocksActivity.class));
                        }
                    };
                    break;
                default:
                    isHome = false;
            }
            this.isHome = function.isHome();


        }

        public int getIconRes() {
            return iconRes;
        }

        public void setIconRes(int iconRes) {
            this.iconRes = iconRes;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public View.OnClickListener getOnClickListener() {
            return onClickListener;
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public boolean isHome() {
            return isHome;
        }

        public void setHome(boolean home) {
            isHome = home;
        }
    }



}
