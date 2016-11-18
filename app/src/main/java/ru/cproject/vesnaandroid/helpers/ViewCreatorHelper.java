package ru.cproject.vesnaandroid.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ru.cproject.vesnaandroid.R;
import ru.cproject.vesnaandroid.Settings;
import ru.cproject.vesnaandroid.activities.events.MainEventsActivity;
import ru.cproject.vesnaandroid.activities.films.MainFilmsActivity;
import ru.cproject.vesnaandroid.activities.shops.MainShopsActivity;
import ru.cproject.vesnaandroid.activities.stocks.MainStocksActivity;
import ru.cproject.vesnaandroid.obj.Category;
import ru.cproject.vesnaandroid.obj.Shop;
import ru.cproject.vesnaandroid.obj.mall.Function;

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

    public static View createShopCard(Context context, ViewGroup content, Shop shop, int color) {
        View shopView = LayoutInflater.from(context).inflate(R.layout.tab_shop, content, false);
        TextView shopName = (TextView) shopView.findViewById(R.id.shop_name);
        shopName.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        shopName.setText(shop.getName());
        LinearLayout info = (LinearLayout) shopView.findViewById(R.id.info);
        if (shop.getComplements() != null) {
            for (int i = 0; i < shop.getComplements().size(); i++) {
                View view = LayoutInflater.from(context).inflate(R.layout.info_complement, info, false);
                ImageView icon = (ImageView) view.findViewById(R.id.icon);
                TextView text = (TextView) view.findViewById(R.id.info);

                switch (shop.getComplements().get(i).getKey()) {
                    case "phone":
                        icon.setImageResource(R.drawable.ic_phone);
                        text.setText(shop.getComplements().get(i).getParametr());
                        text.setBackgroundResource(R.drawable.active_info_background);
                        info.addView(view);
                }
            }
        }
        RecyclerView tags = (RecyclerView) shopView.findViewById(R.id.tags);
        tags.setHasFixedSize(false);
        tags.setNestedScrollingEnabled(false);
        List<Category> categories = shop.getCategories(); // TODO: 09.11.16 ресайклер убери 
        tags.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        return shopView;
    }

    private static class MenuElement {

        private int iconRes;
        private String title;
        private View.OnClickListener onClickListener;
        private boolean isHome;

        MenuElement(final Context context, final Function function) {
            switch (function.getType()) {
                case "map":
                    this.iconRes = R.drawable.ic_map;
                    this.title = "Карта";
                    this.onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // TODO map
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
                            // TODO: 04.11.16 qr
                        }
                    };
                    break;
                case "parking":
                    this.iconRes = R.drawable.ic_parking;
                    this.title = "Парковка";
                    this.onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // TODO парковка
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
                    throw new IllegalArgumentException("No such type: " + function.getType()); // TODO: 04.11.16 можно как-то переделать
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
