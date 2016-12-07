package ru.cproject.vesnaandroid.obj.map;

import java.util.List;

/**
 * Created by Bitizen on 07.12.16.
 */

public class MapData {

    private List<Vertex> vertexList;
    private List<Edge> edgeList;
    private StyleInfo style;
    private int width;
    private int height;
    private long version;

    public List<Vertex> getVertexList() {
        return vertexList;
    }

    public void setVertexList(List<Vertex> vertexList) {
        this.vertexList = vertexList;
    }

    public List<Edge> getEdgeList() {
        return edgeList;
    }

    public void setEdgeList(List<Edge> edgeList) {
        this.edgeList = edgeList;
    }

    public StyleInfo getStyle() {
        return style;
    }

    public void setStyle(StyleInfo style) {
        this.style = style;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
