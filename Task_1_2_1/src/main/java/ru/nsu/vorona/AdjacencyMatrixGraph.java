package ru.nsu.vorona;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Реализация графа через матрицу смежности.
 * Подходит для плотных графов с большим количеством рёбер.
 *
 * @param <V> тип вершин
 */
public class AdjacencyMatrixGraph<V> implements Graph<V> {
    private final List<V> vertices;
    private final Map<V, Integer> vertexIndex;
    private boolean[][] adjacencyMatrix;

    /**
     * Создаёт пустой граф.
     */
    public AdjacencyMatrixGraph() {
        this.vertices = new ArrayList<>();
        this.vertexIndex = new HashMap<>();
        this.adjacencyMatrix = new boolean[10][10];
    }

    @Override
    public boolean addVertex(V vertex) {
        if (vertexIndex.containsKey(vertex)) {
            return false;
        }

        if (vertices.size() >= adjacencyMatrix.length) {
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

        // Удаляем все рёбра, связанные с этой вершиной
        for (int i = 0; i < vertices.size(); i++) {
            adjacencyMatrix[index][i] = false;  // Исходящие рёбра
            adjacencyMatrix[i][index] = false;  // Входящие рёбра
        }

        int lastIndex = vertices.size() - 1;

        if (index != lastIndex) {
            V lastVertex = vertices.get(lastIndex);
            vertices.set(index, lastVertex);
            vertexIndex.put(lastVertex, index);

            // Копируем рёбра последней вершины
            for (int i = 0; i < lastIndex; i++) {
                adjacencyMatrix[index][i] = adjacencyMatrix[lastIndex][i];
                adjacencyMatrix[i][index] = adjacencyMatrix[i][lastIndex];
            }

            // Очищаем старую позицию последней вершины
            for (int i = 0; i < vertices.size(); i++) {
                adjacencyMatrix[lastIndex][i] = false;
                adjacencyMatrix[i][lastIndex] = false;
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

        if (adjacencyMatrix[fromIndex][toIndex]) {
            return false;
        }

        adjacencyMatrix[fromIndex][toIndex] = true;
        return true;
    }

    @Override
    public boolean removeEdge(V from, V to) {
        Integer fromIndex = vertexIndex.get(from);
        Integer toIndex = vertexIndex.get(to);

        if (fromIndex == null || toIndex == null) {
            return false;
        }

        if (!adjacencyMatrix[fromIndex][toIndex]) {
            return false;
        }

        adjacencyMatrix[fromIndex][toIndex] = false;
        return true;
    }

    @Override
    public List<V> getNeighbors(V vertex) {
        Integer index = vertexIndex.get(vertex);
        if (index == null) {
            throw new IllegalArgumentException("Vertex not found");
        }

        List<V> neighbors = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            if (adjacencyMatrix[index][i]) {
                neighbors.add(vertices.get(i));
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
        Integer fromIndex = vertexIndex.get(from);
        Integer toIndex = vertexIndex.get(to);

        if (fromIndex == null || toIndex == null) {
            return false;
        }

        return adjacencyMatrix[fromIndex][toIndex];
    }

    @Override
    public int vertexCount() {
        return vertices.size();
    }

    @Override
    public int edgeCount() {
        int count = 0;
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < vertices.size(); j++) {
                if (adjacencyMatrix[i][j]) {
                    count++;
                }
            }
        }
        return count;
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
        return Objects.hash(vertices, edgeCount());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph[vertices=").append(vertices.size())
                .append(", edges=").append(edgeCount()).append("]:\n");

        for (V vertex : vertices) {
            sb.append(vertex).append(" -> ").append(getNeighbors(vertex)).append("\n");
        }

        return sb.toString();
    }

    private void resizeMatrix() {
        int newSize = adjacencyMatrix.length * 2;
        boolean[][] newMatrix = new boolean[newSize][newSize];

        for (int i = 0; i < adjacencyMatrix.length; i++) {
            System.arraycopy(adjacencyMatrix[i], 0, newMatrix[i], 0, adjacencyMatrix.length);
        }

        adjacencyMatrix = newMatrix;
    }
}
