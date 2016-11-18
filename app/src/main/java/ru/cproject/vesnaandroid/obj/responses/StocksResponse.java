package ru.cproject.vesnaandroid.obj.responses;

import java.util.List;

import ru.cproject.vesnaandroid.obj.Category;
import ru.cproject.vesnaandroid.obj.Stock;

/**
 * Created by Bitizen on 31.10.16.
 */

public class StocksResponse {

    private List<Category> categories;
    private List<Stock> items;

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Stock> getItems() {
        return items;
    }

    public void setItems(List<Stock> items) {
        this.items = items;
    }
}
