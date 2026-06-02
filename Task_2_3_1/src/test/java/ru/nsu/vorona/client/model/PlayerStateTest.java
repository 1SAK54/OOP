package ru.nsu.vorona.client.model;

import org.junit.jupiter.api.Test;
import ru.nsu.vorona.core.model.Cell;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тесты состояния игрока
 */
class PlayerStateTest {

    /**
     * Проверяет создание состояния игрока
     */
    @Test
    void shouldCreatePlayerState() {
        List<Cell> body = List.of(new Cell(1, 1), new Cell(1, 2));

        PlayerState state = new PlayerState(1, "Alice", 5, true, 0, body);

        assertEquals(1, state.getId());
        assertEquals("Alice", state.getNickname());
        assertEquals(5, state.getScore());
        assertTrue(state.isAlive());
        assertEquals(0, state.getRespawnSecondsLeft());
        assertEquals(body, state.getBody());
    }
}