package ru.nsu.vorona;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Тесты для топологической сортировки.
 */
class TopologicalSortTest {

    @Test
    void testSimpleTopologicalSort() {
        Graph<String> graph = new AdjacencyListGraph<>();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");

        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "D");
        graph.addEdge("C", "D");

        List<String> sorted = graph.topologicalSort();

        assertEquals(4, sorted.size());
        assertTrue(sorted.indexOf("A") < sorted.indexOf("B"));
        assertTrue(sorted.indexOf("A") < sorted.indexOf("C"));
        assertTrue(sorted.indexOf("B") < sorted.indexOf("D"));
        assertTrue(sorted.indexOf("C") < sorted.indexOf("D"));
    }

    @Test
    void testLinearTopologicalSort() {
        Graph<String> graph = new AdjacencyMatrixGraph<>();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");

        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("C", "D");

        List<String> sorted = graph.topologicalSort();

        assertEquals(List.of("A", "B", "C", "D"), sorted);
    }

    @Test
    void testDisconnectedGraph() {
        Graph<String> graph = new IncidenceMatrixGraph<>();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");

        graph.addEdge("A", "B");
        graph.addEdge("C", "D");

        List<String> sorted = graph.topologicalSort();

        assertEquals(4, sorted.size());
        assertTrue(sorted.indexOf("A") < sorted.indexOf("B"));
        assertTrue(sorted.indexOf("C") < sorted.indexOf("D"));
    }

    @Test
    void testSingleVertex() {
        Graph<String> graph = new AdjacencyListGraph<>();
        graph.addVertex("A");

        List<String> sorted = graph.topologicalSort();

        assertEquals(List.of("A"), sorted);
    }

    @Test
    void testEmptyGraph() {
        Graph<String> graph = new AdjacencyListGraph<>();

        List<String> sorted = graph.topologicalSort();

        assertTrue(sorted.isEmpty());
    }

    @Test
    void testGraphWithCycle() {
        Graph<String> graph = new AdjacencyListGraph<>();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");

        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("C", "A");

        assertThrows(IllegalStateException.class, graph::topologicalSort);
    }

    @Test
    void testSelfLoopCycle() {
        Graph<String> graph = new AdjacencyMatrixGraph<>();
        graph.addVertex("A");
        graph.addEdge("A", "A");

        assertThrows(IllegalStateException.class, graph::topologicalSort);
    }

    @Test
    void testComplexGraph() {
        Graph<String> graph = new IncidenceMatrixGraph<>();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");
        graph.addVertex("E");

        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "D");
        graph.addEdge("C", "D");
        graph.addEdge("D", "E");

        List<String> sorted = graph.topologicalSort();

        assertEquals(5, sorted.size());
        assertEquals("A", sorted.get(0));
        assertEquals("E", sorted.get(4));
    }

    @Test
    void testDiamondDependency() {
        Graph<String> graph = new AdjacencyListGraph<>();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");

        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "D");
        graph.addEdge("C", "D");

        List<String> sorted = graph.topologicalSort();

        assertEquals("A", sorted.get(0));
        assertEquals("D", sorted.get(3));
    }

    @Test
    void testTopologicalSortWithNumbers() {
        Graph<Integer> graph = new AdjacencyMatrixGraph<>();
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);

        graph.addEdge(1, 2);
        graph.addEdge(2, 3);

        List<Integer> sorted = graph.topologicalSort();

        assertEquals(List.of(1, 2, 3), sorted);
    }
}
