package ru.cproject.vesnaandroid.obj.responses;

import java.util.List;

import ru.cproject.vesnaandroid.obj.Film;
import ru.cproject.vesnaandroid.obj.Shop;

/**
 * Created by Bitizen on 02.11.16.
 */

public class FilmsResponse {

    private List<Film> items;
    private Shop cinema;

    public List<Film> getItems() {
        return items;
    }

    public void setItems(List<Film> items) {
        this.items = items;
    }

    public Shop getCinema() {
        return cinema;
    }

    public void setCinema(Shop cinema) {
        this.cinema = cinema;
    }
}
