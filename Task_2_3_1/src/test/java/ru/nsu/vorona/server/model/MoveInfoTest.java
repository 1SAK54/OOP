package ru.nsu.vorona.server.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тесты будущего хода змейки
 */
class MoveInfoTest {

    /**
     * Проверяет создание хода
     */
    @Test
    void shouldCreateMoveInfo() {
        Cell cell = new Cell(3, 4);
        MoveInfo moveInfo = new MoveInfo(cell, true);

        assertEquals(cell, moveInfo.newHead());
        assertTrue(moveInfo.grow());
    }
}