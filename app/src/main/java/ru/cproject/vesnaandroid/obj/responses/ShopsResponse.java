package ru.cproject.vesnaandroid.obj.responses;

import java.util.List;

import ru.cproject.vesnaandroid.obj.Category;
import ru.cproject.vesnaandroid.obj.Shop;
import ru.cproject.vesnaandroid.obj.Show;

/**
 * Created by Bitizen on 01.11.16.
 */

public class ShopsResponse {

    private List<Category> categories;
    private List<Category> choose;
    private List<Show> shows;
    private List<Shop> shops;

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Category> getChoose() {
        return choose;
    }

    public void setChoose(List<Category> choose) {
        this.choose = choose;
    }

    public List<Show> getShows() {
        return shows;
    }

    public void setShows(List<Show> shows) {
        this.shows = shows;
    }

    public List<Shop> getShops() {
        return shops;
    }

    public void setShops(List<Shop> shops) {
        this.shops = shops;
    }
}
