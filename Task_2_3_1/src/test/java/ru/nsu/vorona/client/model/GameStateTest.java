package ru.nsu.vorona.client.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты состояния игры
 */
class GameStateTest {

    /**
     * Проверяет разбор строки состояния сервера
     */
    @Test
    void shouldParseGameState() {
        String line = "STATE|2|40|60|1,2;3,4|"
                + "1,Alice,5,true,0,10,10;10,11#"
                + "2,Bob,3,false,2,20,20|"
                + "1,Alice,5;2,Bob,3";

        GameState state = GameState.parse(line);

        assertEquals(2, state.getViewerId());
        assertEquals(40, state.getRows());
        assertEquals(60, state.getCols());

        assertEquals(2, state.getFoods().size());
        assertEquals(new Cell(1, 2), state.getFoods().get(0));
        assertEquals(new Cell(3, 4), state.getFoods().get(1));

        assertEquals(2, state.getPlayers().size());

        PlayerState firstPlayer = state.getPlayers().get(0);
        assertEquals(1, firstPlayer.getId());
        assertEquals("Alice", firstPlayer.getNickname());
        assertEquals(5, firstPlayer.getScore());
        assertTrue(firstPlayer.isAlive());
        assertEquals(0, firstPlayer.getRespawnSecondsLeft());
        assertEquals(2, firstPlayer.getBody().size());

        PlayerState secondPlayer = state.getPlayers().get(1);
        assertEquals(2, secondPlayer.getId());
        assertEquals("Bob", secondPlayer.getNickname());
        assertEquals(3, secondPlayer.getScore());
        assertFalse(secondPlayer.isAlive());
        assertEquals(2, secondPlayer.getRespawnSecondsLeft());

        assertEquals(2, state.getLeaderboard().size());
        assertEquals("Alice", state.getLeaderboard().get(0).getNickname());
        assertEquals(5, state.getLeaderboard().get(0).getScore());
    }

    /**
     * Проверяет получение текущего игрока
     */
    @Test
    void shouldReturnViewerPlayer() {
        String line = "STATE|1|40|60|5,5|"
                + "1,Alice,7,true,0,10,10|"
                + "1,Alice,7";

        GameState state = GameState.parse(line);
        PlayerState viewer = state.getViewer();

        assertNotNull(viewer);
        assertEquals(1, viewer.getId());
        assertEquals("Alice", viewer.getNickname());
        assertEquals(7, viewer.getScore());
    }

    /**
     * Проверяет отсутствие текущего игрока
     */
    @Test
    void shouldReturnNullWhenViewerNotFound() {
        String line = "STATE|3|40|60|5,5|"
                + "1,Alice,7,true,0,10,10|"
                + "1,Alice,7";

        GameState state = GameState.parse(line);

        assertNull(state.getViewer());
    }

    /**
     * Проверяет пустые списки
     */
    @Test
    void shouldParseEmptyCollections() {
        String line = "STATE|1|40|60||||";

        GameState state = GameState.parse(line);

        assertEquals(1, state.getViewerId());
        assertTrue(state.getFoods().isEmpty());
        assertTrue(state.getPlayers().isEmpty());
        assertTrue(state.getLeaderboard().isEmpty());
    }
}