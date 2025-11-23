package ru.nsu.vorona;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Класс для чтения графа из потока в фиксированном формате.
 * Поддерживает AutoCloseable для корректного закрытия ресурсов.
 * Формат файла:
 * Строка 1: количество вершин (число)
 * Строка 2: список вершин через пробел
 * Остальные строки: рёбра в формате "from to"
 */
public final class GraphReader implements AutoCloseable {
    private final BufferedReader reader;

    /**
     * Создаёт GraphReader из Reader.
     *
     * @param reader источник данных
     */
    public GraphReader(Reader reader) {
        if (reader instanceof BufferedReader) {
            this.reader = (BufferedReader) reader;
        } else {
            this.reader = new BufferedReader(reader);
        }
    }

    /**
     * Создаёт GraphReader из имени файла.
     *
     * @param filename путь к файлу
     * @throws IOException если файл не найден
     */
    public GraphReader(String filename) throws IOException {
        this(new FileReader(filename));
    }

    /**
     * Читает граф из потока и заполняет переданный граф.
     *
     * @param graph граф для заполнения
     * @throws IOException при ошибке чтения
     * @throws IllegalArgumentException если формат файла неверен
     */
    public void read(Graph<String> graph) throws IOException {
        String vertexCountLine = reader.readLine();
        if (vertexCountLine == null) {
            throw new IllegalArgumentException("Empty file");
        }

        int vertexCount = Integer.parseInt(vertexCountLine.trim());

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
                } else {
                    throw new IllegalArgumentException(
                            "Invalid edge format: " + line + ". Expected: 'from to'"
                    );
                }
            }
        }
    }

    /**
     * Закрывает поток чтения.
     *
     * @throws IOException при ошибке закрытия
     */
    @Override
    public void close() throws IOException {
        reader.close();
    }
}
