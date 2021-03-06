package ru.cproject.vesnaandroid.obj.map;

import io.realm.RealmObject;

/**
 * Created by Bitizen on 07.12.16.
 */
public class Edge extends RealmObject {

    private int fromId;
    private int toId;
    private boolean bi;
    private int cost;
    private Vertex source;
    private Vertex destination;

    public Vertex getSource() {
        return source;
    }

    public void setSource(Vertex source) {
        this.source = source;
    }

    public Vertex getDestination() {
        return destination;
    }

    public void setDestination(Vertex destination) {
        this.destination = destination;
    }

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public int getToId() {
        return toId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public boolean isBi() {
        return bi;
    }

    public void setBi(boolean bi) {
        this.bi = bi;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
