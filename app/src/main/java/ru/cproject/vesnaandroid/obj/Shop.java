package ru.cproject.vesnaandroid.obj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bitizen on 29.10.16.
 */

public class Shop {

    private int id;
    private String logo;
    private String name = "";
    private String content;
    private boolean like;

    private List<String> photos = new ArrayList<>();
    private List<Complement> complements;
    private List<Stock> stocks = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();

    public Shop() {}

    public Shop(int id, String logo, String name) {
        this.id = id;
        this.logo = logo;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public List<Stock> getStocks() {
        return stocks;
    }

    public void setStocks(List<Stock> stocks) {
        this.stocks = stocks;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public List<Complement> getComplements() {
        return complements;
    }

    public void setComplements(List<Complement> complements) {
        this.complements = complements;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }


    public static class Complement {

        private String key;
        private String parametr;

        public Complement() {}

        public Complement(String key, String parametr) {
            this.key = key;
            this.parametr = parametr;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getParametr() {
            return parametr;
        }

        public void setParametr(String parametr) {
            this.parametr = parametr;
        }
    }
}
