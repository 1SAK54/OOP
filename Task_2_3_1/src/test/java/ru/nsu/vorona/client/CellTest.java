package ru.nsu.vorona.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тесты клетки клиента
 */
class CellTest {

    /**
     * Проверяет координаты клетки
     */
    @Test
    void shouldStoreCellCoordinates() {
        Cell cell = new Cell(2, 5);

        assertEquals(2, cell.row());
        assertEquals(5, cell.col());
    }

    /**
     * Проверяет равенство клеток
     */
    @Test
    void shouldCompareCellsByCoordinates() {
        Cell first = new Cell(1, 3);
        Cell second = new Cell(1, 3);

        assertEquals(first, second);
    }
}