package ru.cproject.vesnaandroid.obj;

/**
 * Created by andro on 22.11.2016.
 */

public class Search {

    private int id;
    private String type;
    private String name;
    private String imageURL;

    public Search(int id, String type, String name, String imageURL) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.imageURL = imageURL;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
