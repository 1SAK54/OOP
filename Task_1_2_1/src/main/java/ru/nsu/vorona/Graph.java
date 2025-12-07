package ru.nsu.vorona;

import java.util.List;

/**
 * Интерфейс для представления ориентированного графа.
 *
 * @param <V> тип вершин графа
 */
public interface Graph<V> {

    /**
     * Добавляет вершину в граф.
     *
     * @param vertex вершина для добавления
     * @return true, если вершина была добавлена; false, если уже существует
     */
    boolean addVertex(V vertex);

    /**
     * Удаляет вершину из графа вместе со всеми инцидентными рёбрами.
     *
     * @param vertex вершина для удаления
     * @return true, если вершина была удалена; false, если не существует
     */
    boolean removeVertex(V vertex);

    /**
     * Добавляет ориентированное ребро из from в to.
     *
     * @param from начальная вершина
     * @param to   конечная вершина
     * @return true, если ребро было добавлено; false, если уже существует
     */
    boolean addEdge(V from, V to);

    /**
     * Удаляет ориентированное ребро из from в to.
     *
     * @param from начальная вершина
     * @param to   конечная вершина
     * @return true, если ребро было удалено; false, если не существует
     */
    boolean removeEdge(V from, V to);

    /**
     * Возвращает список всех соседей (смежных вершин) для заданной вершины.
     *
     * @param vertex вершина
     * @return список соседей (вершин, в которые есть рёбра из данной)
     */
    List<V> getNeighbors(V vertex);

    /**
     * Возвращает список всех вершин графа.
     *
     * @return список вершин
     */
    List<V> getVertices();

    /**
     * Проверяет существование ребра между вершинами.
     *
     * @param from начальная вершина
     * @param to   конечная вершина
     * @return true, если ребро существует
     */
    boolean hasEdge(V from, V to);

    /**
     * Возвращает количество вершин в графе.
     *
     * @return количество вершин
     */
    int vertexCount();

    /**
     * Возвращает количество рёбер в графе.
     *
     * @return количество рёбер
     */
    int edgeCount();

    /**
     * Выполняет топологическую сортировку вершин графа.
     *
     * @return список вершин в топологическом порядке
     * @throws IllegalStateException если граф содержит цикл
     */
    List<V> topologicalSort();
}
