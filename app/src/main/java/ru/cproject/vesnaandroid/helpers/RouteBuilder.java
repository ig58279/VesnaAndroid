package ru.cproject.vesnaandroid.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.cproject.vesnaandroid.obj.map.Edge;
import ru.cproject.vesnaandroid.obj.map.Graph;
import ru.cproject.vesnaandroid.obj.map.Vertex;

/**
 * Created by Владислав on 14.12.16.
 */

public class RouteBuilder {

    private final List<Vertex> nodes;
    private final List<Edge> edges;
    private Set<Vertex> settledNodes;
    private Set<Vertex> unSettledNodes;
    private Map<Vertex, Set<Vertex>> predecessors;
    private Map<Vertex, Integer> distance;    //они же веса при вершинах

    public RouteBuilder(Graph graph) {
        this.nodes = new ArrayList<Vertex>(graph.getVertexes());
        this.edges = new ArrayList<Edge>(graph.getEdges());
    }

    public void execute(Vertex source) {           //инициализация весов ,выбор начальной точки
        settledNodes = new HashSet<Vertex>();
        unSettledNodes = new HashSet<Vertex>();
        distance = new HashMap<Vertex, Integer>();
        predecessors = new HashMap<Vertex, Set<Vertex>>();
        distance.put(source, 0);
        unSettledNodes.add(source);                  //помечаем нулевую вершину как непосещенную
        while (unSettledNodes.size() > 0) {
            Vertex node = getMinimum(unSettledNodes);       //получаем минимум среди всех непосещенных вершин(добавляем их в findMinimalDistance)
            settledNodes.add(node);
            unSettledNodes.remove(node);
            findMinimalDistances(node);
        }
        predecessors.size();
    }

    private void findMinimalDistances(Vertex node) {
        List<Vertex> adjacentNodes = getNeighbors(node);            //получаем смежные вершины
        for (Vertex target : adjacentNodes) {
            if (getDistance(target) > getDistance(node)             //если там стояло значение большее,чем расстояние до текущей вершины + от текущей до целевой
                    + getDistance(node, target)) {
                distance.put(target, getDistance(node)
                        + getDistance(node, target));
                if(predecessors.get(target)==null){                //добавляем текущую в предшествующие целевой
                    Set<Vertex> set = new HashSet<>();
                    set.add(node);
                    predecessors.put(target, set);
                }else{
                    Set<Vertex> set = predecessors.get(target);
                    set.add(node);
                }
                unSettledNodes.add(target);                      //добавляем в непосещенные
            }
        }

    }

    private int getDistance(Vertex node, Vertex target) {
        for (Edge edge : edges) {
            if (edge.getSource().equals(node)
                    && edge.getDestination().equals(target)) {
                return edge.getCost();
            }
            if (edge.getSource().equals(target)
                    && edge.getDestination().equals(node)) {
                return edge.getCost();
            }
        }
          throw new RuntimeException("Should not happen");
    }

    private List<Vertex> getNeighbors(Vertex node) {
        List<Vertex> neighbors = new ArrayList<Vertex>();
        for (Edge edge : edges) {
            if (edge.getSource().equals(node)
                    && !isSettled(edge.getDestination())) {    //едобавляем,если вершина не помечана,как посещенная
                neighbors.add(edge.getDestination());
            }
            if (edge.getDestination().equals(node)
                    && !isSettled(edge.getSource())) {    //едобавляем,если вершина не помечана,как посещенная
                neighbors.add(edge.getSource());
            }
        }
        return neighbors;
    }

    private Vertex getMinimum(Set<Vertex> vertexes) {
        Vertex minimum = null;
        for (Vertex vertex : vertexes) {
            if (minimum == null) {
                minimum = vertex;
            } else {
                if (getDistance(vertex) < getDistance(minimum)) {
                    minimum = vertex;
                }
            }
        }
        return minimum;
    }

    private boolean isSettled(Vertex vertex) {
        return settledNodes.contains(vertex);
    }

    private int getDistance(Vertex destination) {   //получает расстояние,иначе присваивается бесконечность
        Integer d = distance.get(destination);
        if (d == null) {
            return Integer.MAX_VALUE;
        } else {
            return d;
        }
    }

    /*
     * This method returns the path from the source to the selected target and
     * NULL if no path exists
     */
    public LinkedList<Vertex> getPath(Vertex target) {
        LinkedList<Vertex> path = new LinkedList<Vertex>();
        Vertex step = target;
        // check if a path exists
        if (predecessors.get(step) == null) {
            return null;
        }
        path.add(step);
        while (predecessors.get(step) != null) {
            for(Vertex vertex : predecessors.get(step)){
                step = vertex;
                path.add(step);
            }
        }
        // Put it into the correct order
        Collections.reverse(path);
        return path;
    }




}
