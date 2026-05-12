package ru.nsu.vorona.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тесты клетки сервера
 */
class CellTest {

    /**
     * Проверяет координаты клетки
     */
    @Test
    void shouldStoreCellCoordinates() {
        Cell cell = new Cell(4, 7);

        assertEquals(4, cell.row());
        assertEquals(7, cell.col());
    }

    /**
     * Проверяет равенство клеток
     */
    @Test
    void shouldCompareCellsByCoordinates() {
        Cell first = new Cell(2, 8);
        Cell second = new Cell(2, 8);

        assertEquals(first, second);
    }
}