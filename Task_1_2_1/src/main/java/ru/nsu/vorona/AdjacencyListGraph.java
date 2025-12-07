package ru.nsu.vorona;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Реализация графа через список смежности.
 * Наиболее эффективна по памяти для разреженных графов.
 *
 * @param <V> тип вершин
 */
public class AdjacencyListGraph<V> implements Graph<V> {
    private final Map<V, Set<V>> adjacencyList;

    public AdjacencyListGraph() {
        this.adjacencyList = new HashMap<>();
    }

    @Override
    public boolean addVertex(V vertex) {
        if (adjacencyList.containsKey(vertex)) {
            return false;
        }
        adjacencyList.put(vertex, new HashSet<>());
        return true;
    }

    @Override
    public boolean removeVertex(V vertex) {
        if (!adjacencyList.containsKey(vertex)) {
            return false;
        }

        adjacencyList.remove(vertex);

        for (Set<V> neighbors : adjacencyList.values()) {
            neighbors.remove(vertex);
        }

        return true;
    }

    @Override
    public boolean addEdge(V from, V to) {
        if (!adjacencyList.containsKey(from) || !adjacencyList.containsKey(to)) {
            throw new IllegalArgumentException("Vertex not found");
        }

        return adjacencyList.get(from).add(to);
    }

    @Override
    public boolean removeEdge(V from, V to) {
        if (!adjacencyList.containsKey(from)) {
            return false;
        }

        return adjacencyList.get(from).remove(to);
    }

    @Override
    public List<V> getNeighbors(V vertex) {
        Set<V> neighbors = adjacencyList.get(vertex);
        if (neighbors == null) {
            throw new IllegalArgumentException("Vertex not found");
        }
        return new ArrayList<>(neighbors);
    }

    @Override
    public List<V> getVertices() {
        return new ArrayList<>(adjacencyList.keySet());
    }

    @Override
    public boolean hasEdge(V from, V to) {
        Set<V> neighbors = adjacencyList.get(from);
        return neighbors != null && neighbors.contains(to);
    }

    @Override
    public int vertexCount() {
        return adjacencyList.size();
    }

    @Override
    public int edgeCount() {
        return adjacencyList.values().stream().mapToInt(Set::size).sum();
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

        List<?> otherVertices = other.getVertices();
        if (!otherVertices.containsAll(adjacencyList.keySet())) {
            return false;
        }

        for (Map.Entry<V, Set<V>> entry : adjacencyList.entrySet()) {
            V vertex = entry.getKey();
            Set<V> myNeighbors = entry.getValue();

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
        return Objects.hash(adjacencyList);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph[vertices=").append(vertexCount())
                .append(", edges=").append(edgeCount()).append("]:\n");

        for (Map.Entry<V, Set<V>> entry : adjacencyList.entrySet()) {
            sb.append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
        }

        return sb.toString();
    }
}
