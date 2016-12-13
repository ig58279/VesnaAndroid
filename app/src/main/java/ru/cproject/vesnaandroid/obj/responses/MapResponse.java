package ru.cproject.vesnaandroid.obj.responses;

import java.util.List;

import ru.cproject.vesnaandroid.obj.map.Edge;
import ru.cproject.vesnaandroid.obj.map.StyleInfo;
import ru.cproject.vesnaandroid.obj.map.Vertex;

/**
 * Created by Bitizen on 10.12.16.
 */

public class MapResponse {

    private List<Vertex> verstexs;
    private List<Edge> edges;
    private StyleInfo styleInfo;
    private int width;
    private int height;
    private long version;

    public List<Vertex> getVerstexs() {
        return verstexs;
    }

    public void setVerstexs(List<Vertex> verstexs) {
        this.verstexs = verstexs;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public StyleInfo getStyleInfo() {
        return styleInfo;
    }

    public void setStyleInfo(StyleInfo styleInfo) {
        this.styleInfo = styleInfo;
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
