package ru.nsu.vorona;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Реализация графа через матрицу инцидентности.
 * Подходит для графов, где важно быстро находить рёбра и анализировать степени вершин.
 *
 * @param <V> тип вершин
 */
public class IncidenceMatrixGraph<V> implements Graph<V> {

    /**
     * Класс для представления ребра.
     */
    private static class Edge<V> {
        final V from;
        final V to;

        Edge(V from, V to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Edge)) {
                return false;
            }
            Edge<?> other = (Edge<?>) obj;
            return from.equals(other.from) && to.equals(other.to);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }
    }

    private final List<V> vertices;
    private final Map<V, Integer> vertexIndex;
    private final List<Edge<V>> edges;
    private int[][] incidenceMatrix;

    /**
     * Создаёт пустой граф.
     */
    public IncidenceMatrixGraph() {
        this.vertices = new ArrayList<>();
        this.vertexIndex = new HashMap<>();
        this.edges = new ArrayList<>();
        this.incidenceMatrix = new int[10][10];
    }

    @Override
    public boolean addVertex(V vertex) {
        if (vertexIndex.containsKey(vertex)) {
            return false;
        }

        if (vertices.size() >= incidenceMatrix.length) {
            resizeMatrix();
        }

        vertices.add(vertex);
        vertexIndex.put(vertex, vertices.size() - 1);
        return true;
    }

    @Override
    public boolean removeVertex(V vertex) {
        Integer index = vertexIndex.get(vertex);
        if (index == null) {
            return false;
        }

        List<Edge<V>> toRemove = new ArrayList<>();
        for (Edge<V> edge : edges) {
            if (edge.from.equals(vertex) || edge.to.equals(vertex)) {
                toRemove.add(edge);
            }
        }

        for (Edge<V> edge : toRemove) {
            removeEdge(edge.from, edge.to);
        }

        int lastIndex = vertices.size() - 1;
        if (index != lastIndex) {
            V lastVertex = vertices.get(lastIndex);
            vertices.set(index, lastVertex);
            vertexIndex.put(lastVertex, index);

            for (int j = 0; j < edges.size(); j++) {
                incidenceMatrix[index][j] = incidenceMatrix[lastIndex][j];
            }
        }

        vertices.remove(lastIndex);
        vertexIndex.remove(vertex);
        return true;
    }

    @Override
    public boolean addEdge(V from, V to) {
        Integer fromIndex = vertexIndex.get(from);
        Integer toIndex = vertexIndex.get(to);

        if (fromIndex == null || toIndex == null) {
            throw new IllegalArgumentException("Vertex not found");
        }

        Edge<V> edge = new Edge<>(from, to);
        if (edges.contains(edge)) {
            return false;
        }

        if (edges.size() >= incidenceMatrix[0].length) {
            resizeMatrix();
        }

        int edgeIndex = edges.size();
        edges.add(edge);

        incidenceMatrix[fromIndex][edgeIndex] = -1;
        incidenceMatrix[toIndex][edgeIndex] = 1;

        return true;
    }

    @Override
    public boolean removeEdge(V from, V to) {
        Edge<V> edge = new Edge<>(from, to);
        int edgeIndex = edges.indexOf(edge);

        if (edgeIndex == -1) {
            return false;
        }

        int lastEdgeIndex = edges.size() - 1;
        if (edgeIndex != lastEdgeIndex) {
            edges.set(edgeIndex, edges.get(lastEdgeIndex));

            for (int i = 0; i < vertices.size(); i++) {
                incidenceMatrix[i][edgeIndex] = incidenceMatrix[i][lastEdgeIndex];
                incidenceMatrix[i][lastEdgeIndex] = 0;
            }
        } else {
            for (int i = 0; i < vertices.size(); i++) {
                incidenceMatrix[i][edgeIndex] = 0;
            }
        }

        edges.remove(lastEdgeIndex);
        return true;
    }

    @Override
    public List<V> getNeighbors(V vertex) {
        Integer index = vertexIndex.get(vertex);
        if (index == null) {
            throw new IllegalArgumentException("Vertex not found");
        }

        List<V> neighbors = new ArrayList<>();
        for (int j = 0; j < edges.size(); j++) {
            if (incidenceMatrix[index][j] == -1) {
                Edge<V> edge = edges.get(j);
                neighbors.add(edge.to);
            }
        }

        return neighbors;
    }

    @Override
    public List<V> getVertices() {
        return new ArrayList<>(vertices);
    }

    @Override
    public boolean hasEdge(V from, V to) {
        return edges.contains(new Edge<>(from, to));
    }

    @Override
    public int vertexCount() {
        return vertices.size();
    }

    @Override
    public int edgeCount() {
        return edges.size();
    }

    @Override
    public List<V> topologicalSort() {
        return TopologicalSort.sort(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Graph)) {
            return false;
        }

        @SuppressWarnings("rawtypes")
        Graph other = (Graph) obj;

        if (vertexCount() != other.vertexCount() || edgeCount() != other.edgeCount()) {
            return false;
        }

        if (!other.getVertices().containsAll(vertices)) {
            return false;
        }

        for (V vertex : vertices) {
            List<V> myNeighbors = getNeighbors(vertex);

            try {
                @SuppressWarnings("unchecked")
                List<?> otherNeighbors = other.getNeighbors(vertex);

                if (myNeighbors.size() != otherNeighbors.size()) {
                    return false;
                }

                if (!otherNeighbors.containsAll(myNeighbors)) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertices, edges);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IncidenceMatrixGraph[vertices=").append(vertices.size())
                .append(", edges=").append(edges.size()).append("]:\n");

        for (V vertex : vertices) {
            sb.append(vertex).append(" -> ").append(getNeighbors(vertex)).append("\n");
        }

        sb.append("\nIncidence Matrix:\n");
        sb.append("     ");
        for (int j = 0; j < edges.size(); j++) {
            sb.append(String.format("e%-3d", j));
        }
        sb.append("\n");

        for (int i = 0; i < vertices.size(); i++) {
            sb.append(String.format("%-4s ", vertices.get(i)));
            for (int j = 0; j < edges.size(); j++) {
                sb.append(String.format("%-4d", incidenceMatrix[i][j]));
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private void resizeMatrix() {
        int newVertexSize = Math.max(incidenceMatrix.length * 2, vertices.size() + 1);
        int newEdgeSize = Math.max(incidenceMatrix[0].length * 2, edges.size() + 1);
        int[][] newMatrix = new int[newVertexSize][newEdgeSize];

        for (int i = 0; i < incidenceMatrix.length && i < vertices.size(); i++) {
            for (int j = 0; j < incidenceMatrix[0].length && j < edges.size(); j++) {
                newMatrix[i][j] = incidenceMatrix[i][j];
            }
        }

        incidenceMatrix = newMatrix;
    }
}
