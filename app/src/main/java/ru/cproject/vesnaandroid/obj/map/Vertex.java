package ru.cproject.vesnaandroid.obj.map;

import android.support.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Bitizen on 07.12.16.
 */
public class Vertex extends RealmObject {

    @PrimaryKey
    private int id;

    private float x;
    private float y;

    private String type;
    private int routeId;

    private int shopId;
    private String shopName;
    private String cats;
    private String park;

    public String getPark() {
        return park;
    }

    public void setPark(String park) {
        this.park = park;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String name) {
        this.shopName = name;
    }

    public String getCats() {
        return cats;
    }

    public void setCats(String cats) {
        this.cats = cats;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vertex vertex = (Vertex)obj;
        return this.id == vertex.getId();
    }

    @Override
    public String toString() {
        return "ID: " + this.getId();
    }
}
