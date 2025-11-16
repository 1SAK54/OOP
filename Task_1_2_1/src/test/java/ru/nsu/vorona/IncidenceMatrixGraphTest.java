package ru.nsu.vorona;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

/**
 * Тесты для реализации графа через матрицу инцидентности.
 */
class IncidenceMatrixGraphTest {

    @Test
    void testAddVertex() {
        Graph<String> graph = new IncidenceMatrixGraph<>();
        assertTrue(graph.addVertex("A"));
        assertTrue(graph.addVertex("B"));
        assertFalse(graph.addVertex("A"));
        assertEquals(2, graph.vertexCount());
    }

    @Test
    void testAddManyVertices() {
        Graph<Integer> graph = new IncidenceMatrixGraph<>();

        for (int i = 0; i < 15; i++) {
            assertTrue(graph.addVertex(i));
        }

        assertEquals(15, graph.vertexCount());
    }

    @Test
    void testRemoveVertex() {
        Graph<String> graph = new IncidenceMatrixGraph<>();
        graph.addVertex("A");
        graph.addVertex("B");

        assertTrue(graph.removeVertex("A"));
        assertFalse(graph.removeVertex("A"));
        assertEquals(1, graph.vertexCount());
    }

    @Test
    void testRemoveVertexWithEdges() {
        Graph<String> graph = new IncidenceMatrixGraph<>();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");

        graph.removeVertex("B");

        assertEquals(2, graph.vertexCount());
        assertEquals(0, graph.edgeCount());
    }

    @Test
    void testAddEdge() {
        Graph<String> graph = new IncidenceMatrixGraph<>();
        graph.addVertex("A");
        graph.addVertex("B");

        assertTrue(graph.addEdge("A", "B"));
        assertFalse(graph.addEdge("A", "B"));
        assertEquals(1, graph.edgeCount());
    }

    @Test
    void testAddManyEdges() {
        Graph<Integer> graph = new IncidenceMatrixGraph<>();

        for (int i = 0; i < 15; i++) {
            graph.addVertex(i);
        }

        for (int i = 0; i < 14; i++) {
            graph.addEdge(i, i + 1);
        }

        assertEquals(14, graph.edgeCount());
    }

    @Test
    void testAddEdgeNonexistentVertex() {
        Graph<String> graph = new IncidenceMatrixGraph<>();
        graph.addVertex("A");

        assertThrows(IllegalArgumentException.class, () -> graph.addEdge("A", "B"));
    }

    @Test
    void testRemoveEdge() {
        Graph<String> graph = new IncidenceMatrixGraph<>();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addEdge("A", "B");

        assertTrue(graph.removeEdge("A", "B"));
        assertFalse(graph.removeEdge("A", "B"));
        assertEquals(0, graph.edgeCount());
    }

    @Test
    void testHasEdge() {
        Graph<String> graph = new IncidenceMatrixGraph<>();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addEdge("A", "B");

        assertTrue(graph.hasEdge("A", "B"));
        assertFalse(graph.hasEdge("B", "A"));
    }

    @Test
    void testGetNeighbors() {
        Graph<String> graph = new IncidenceMatrixGraph<>();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");

        List<String> neighbors = graph.getNeighbors("A");
        assertEquals(2, neighbors.size());
        assertTrue(neighbors.contains("B"));
        assertTrue(neighbors.contains("C"));
    }

    @Test
    void testGetNeighborsEmpty() {
        Graph<String> graph = new IncidenceMatrixGraph<>();
        graph.addVertex("A");

        List<String> neighbors = graph.getNeighbors("A");
        assertTrue(neighbors.isEmpty());
    }

    @Test
    void testGetNeighborsNonexistentVertex() {
        Graph<String> graph = new IncidenceMatrixGraph<>();

        assertThrows(IllegalArgumentException.class, () -> graph.getNeighbors("A"));
    }

    @Test
    void testGetVertices() {
        Graph<String> graph = new IncidenceMatrixGraph<>();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");

        List<String> vertices = graph.getVertices();
        assertEquals(3, vertices.size());
        assertTrue(vertices.contains("A"));
        assertTrue(vertices.contains("B"));
        assertTrue(vertices.contains("C"));
    }

    @Test
    void testVertexCount() {
        Graph<String> graph = new IncidenceMatrixGraph<>();
        assertEquals(0, graph.vertexCount());

        graph.addVertex("A");
        assertEquals(1, graph.vertexCount());
    }

    @Test
    void testEdgeCount() {
        Graph<String> graph = new IncidenceMatrixGraph<>();
        graph.addVertex("A");
        graph.addVertex("B");

        assertEquals(0, graph.edgeCount());

        graph.addEdge("A", "B");
        assertEquals(1, graph.edgeCount());
    }

    @Test
    void testSelfLoop() {
        Graph<String> graph = new IncidenceMatrixGraph<>();
        graph.addVertex("A");
        graph.addEdge("A", "A");

        assertTrue(graph.hasEdge("A", "A"));
        assertEquals(1, graph.edgeCount());
    }

    @Test
    void testEmptyGraph() {
        Graph<String> graph = new IncidenceMatrixGraph<>();

        assertEquals(0, graph.vertexCount());
        assertEquals(0, graph.edgeCount());
    }

    @Test
    void testToString() {
        Graph<String> graph = new IncidenceMatrixGraph<>();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addEdge("A", "B");

        String str = graph.toString();
        assertTrue(str.contains("A"));
        assertTrue(str.contains("B"));
        assertTrue(str.contains("Incidence Matrix"));
    }

    @Test
    void testEquals() {
        Graph<String> graph1 = new IncidenceMatrixGraph<>();
        Graph<String> graph2 = new IncidenceMatrixGraph<>();

        graph1.addVertex("A");
        graph1.addVertex("B");
        graph1.addEdge("A", "B");

        graph2.addVertex("A");
        graph2.addVertex("B");
        graph2.addEdge("A", "B");

        assertEquals(graph1, graph2);
    }

    @Test
    void testNotEquals() {
        Graph<String> graph1 = new IncidenceMatrixGraph<>();
        Graph<String> graph2 = new IncidenceMatrixGraph<>();

        graph1.addVertex("A");
        graph2.addVertex("B");

        assertNotEquals(graph1, graph2);
    }
}
