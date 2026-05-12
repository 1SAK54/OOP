package ru.nsu.vorona.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты направлений сервера
 */
class DirectionTest {

    /**
     * Проверяет изменение строки и столбца
     */
    @Test
    void shouldStoreDeltas() {
        assertEquals(-1, Direction.UP.getRowDelta());
        assertEquals(0, Direction.UP.getColDelta());

        assertEquals(1, Direction.DOWN.getRowDelta());
        assertEquals(0, Direction.DOWN.getColDelta());

        assertEquals(0, Direction.LEFT.getRowDelta());
        assertEquals(-1, Direction.LEFT.getColDelta());

        assertEquals(0, Direction.RIGHT.getRowDelta());
        assertEquals(1, Direction.RIGHT.getColDelta());
    }

    /**
     * Проверяет противоположные направления
     */
    @Test
    void shouldDetectOppositeDirections() {
        assertTrue(Direction.UP.isOpposite(Direction.DOWN));
        assertTrue(Direction.DOWN.isOpposite(Direction.UP));
        assertTrue(Direction.LEFT.isOpposite(Direction.RIGHT));
        assertTrue(Direction.RIGHT.isOpposite(Direction.LEFT));

        assertFalse(Direction.UP.isOpposite(Direction.LEFT));
        assertFalse(Direction.RIGHT.isOpposite(Direction.DOWN));
    }
}