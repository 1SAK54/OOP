package ru.nsu.vorona;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Утилитный класс для чтения графа из файла.
 */
public final class GraphReader {

    /**
     * Читает граф из файла в формате:
     * Строка 1: количество вершин
     * Строка 2: список вершин (через пробел)
     * Остальные строки: рёбра (from to).
     *
     * @param filename имя файла
     * @return загруженный граф
     * @throws IOException при ошибке чтения файла
     */
    public static Graph<String> readGraph(String filename) throws IOException {
        Graph<String> graph = new AdjacencyListGraph<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            int vertexCount = Integer.parseInt(reader.readLine().trim());

            if (vertexCount > 0) {
                String verticesLine = reader.readLine();
                if (verticesLine != null && !verticesLine.trim().isEmpty()) {
                    String[] vertices = verticesLine.trim().split("\\s+");
                    for (String vertex : vertices) {
                        if (!vertex.isEmpty()) {
                            graph.addVertex(vertex);
                        }
                    }
                }
            } else {
                reader.readLine();
            }

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    String[] parts = line.split("\\s+");
                    if (parts.length == 2) {
                        graph.addEdge(parts[0], parts[1]);
                    }
                }
            }
        }

        return graph;
    }

    private GraphReader() {
        throw new UnsupportedOperationException("Utility class");
    }
}
