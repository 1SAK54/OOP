package ru.nsu.vorona;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Тесты для чтения графа из файла.
 */
class GraphReaderTest {

    @Test
    void testReadSimpleGraph(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("graph.txt");
        Files.writeString(file, """
                4
                A B C D
                A B
                A C
                B D
                C D
                """);

        Graph<String> graph = GraphReader.readGraph(file.toString());

        assertEquals(4, graph.vertexCount());
        assertEquals(4, graph.edgeCount());
        assertTrue(graph.hasEdge("A", "B"));
        assertTrue(graph.hasEdge("A", "C"));
        assertTrue(graph.hasEdge("B", "D"));
        assertTrue(graph.hasEdge("C", "D"));
    }

    @Test
    void testReadEmptyGraph(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.writeString(file, """
                0
                
                """);

        Graph<String> graph = GraphReader.readGraph(file.toString());

        assertEquals(0, graph.vertexCount());
        assertEquals(0, graph.edgeCount());
    }

    @Test
    void testReadSingleVertex(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("single.txt");
        Files.writeString(file, """
                1
                A
                """);

        Graph<String> graph = GraphReader.readGraph(file.toString());

        assertEquals(1, graph.vertexCount());
        assertEquals(0, graph.edgeCount());
        assertTrue(graph.getVertices().contains("A"));
    }

    @Test
    void testReadGraphWithSelfLoop(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("selfloop.txt");
        Files.writeString(file, """
                2
                A B
                A A
                A B
                """);

        Graph<String> graph = GraphReader.readGraph(file.toString());

        assertEquals(2, graph.vertexCount());
        assertEquals(2, graph.edgeCount());
        assertTrue(graph.hasEdge("A", "A"));
        assertTrue(graph.hasEdge("A", "B"));
    }

    @Test
    void testReadNonexistentFile() {
        assertThrows(IOException.class, () -> GraphReader.readGraph("nonexistent.txt"));
    }

    @Test
    void testReadComplexGraph(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("complex.txt");
        Files.writeString(file, """
                5
                A B C D E
                A B
                A C
                B D
                C D
                D E
                """);

        Graph<String> graph = GraphReader.readGraph(file.toString());

        assertEquals(5, graph.vertexCount());
        assertEquals(5, graph.edgeCount());
    }
}
