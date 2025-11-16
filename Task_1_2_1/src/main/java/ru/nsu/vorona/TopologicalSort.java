package ru.nsu.vorona;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Алгоритм топологической сортировки графа (алгоритм Кана).
 */
public final class TopologicalSort {

    /**
     * Выполняет топологическую сортировку графа.
     *
     * @param graph граф для сортировки
     * @param <V> тип вершин
     * @return список вершин в топологическом порядке
     * @throws IllegalStateException если граф содержит цикл
     */
    public static <V> List<V> sort(Graph<V> graph) {
        Map<V, Integer> inDegree = new HashMap<>();
        List<V> vertices = graph.getVertices();

        for (V vertex : vertices) {
            inDegree.put(vertex, 0);
        }

        for (V vertex : vertices) {
            for (V neighbor : graph.getNeighbors(vertex)) {
                inDegree.put(neighbor, inDegree.get(neighbor) + 1);
            }
        }

        Queue<V> queue = new LinkedList<>();
        for (V vertex : vertices) {
            if (inDegree.get(vertex) == 0) {
                queue.add(vertex);
            }
        }

        List<V> result = new ArrayList<>();

        while (!queue.isEmpty()) {
            V current = queue.poll();
            result.add(current);

            for (V neighbor : graph.getNeighbors(current)) {
                int degree = inDegree.get(neighbor) - 1;
                inDegree.put(neighbor, degree);

                if (degree == 0) {
                    queue.add(neighbor);
                }
            }
        }

        if (result.size() != vertices.size()) {
            throw new IllegalStateException("Graph contains a cycle");
        }

        return result;
    }

    private TopologicalSort() {
        throw new UnsupportedOperationException("Utility class");
    }
}
