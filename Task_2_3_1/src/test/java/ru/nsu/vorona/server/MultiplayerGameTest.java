package ru.nsu.vorona.server;

import org.junit.jupiter.api.Test;
import ru.nsu.vorona.client.GameState;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты серверной модели игры
 */
class MultiplayerGameTest {

    /**
     * Проверяет добавление игрока
     */
    @Test
    void shouldAddPlayerAndCreateStateLine() {
        MultiplayerGame game = new MultiplayerGame(20, 30, 3, 100);

        int playerId = game.addPlayer("Alice");
        GameState state = GameState.parse(game.toStateLine(playerId));

        assertEquals(playerId, state.getViewerId());
        assertEquals(20, state.getRows());
        assertEquals(30, state.getCols());
        assertEquals(3, state.getFoods().size());
        assertEquals(1, state.getPlayers().size());
        assertEquals("Alice", state.getPlayers().get(0).getNickname());
    }

    /**
     * Проверяет несколько игроков
     */
    @Test
    void shouldAddSeveralPlayers() {
        MultiplayerGame game = new MultiplayerGame(20, 30, 3, 100);

        int firstId = game.addPlayer("Alice");
        int secondId = game.addPlayer("Bob");

        assertEquals(2, game.getPlayerCount());

        GameState state = GameState.parse(game.toStateLine(firstId));

        assertEquals(firstId, state.getViewerId());
        assertEquals(2, state.getPlayers().size());
        assertTrue(state.getPlayers().stream().anyMatch(player -> player.getId() == secondId));
    }

    /**
     * Проверяет очистку ника от разделителей протокола
     */
    @Test
    void shouldSanitizeNickname() {
        MultiplayerGame game = new MultiplayerGame(20, 30, 3, 100);

        int playerId = game.addPlayer("A|B,C;D#E");
        GameState state = GameState.parse(game.toStateLine(playerId));

        assertEquals("A_B_C_D_E", state.getPlayers().get(0).getNickname());
    }

    /**
     * Проверяет ник по умолчанию
     */
    @Test
    void shouldUseDefaultNicknameWhenBlank() {
        MultiplayerGame game = new MultiplayerGame(20, 30, 3, 100);

        int playerId = game.addPlayer("   ");
        GameState state = GameState.parse(game.toStateLine(playerId));

        assertEquals("Player", state.getPlayers().get(0).getNickname());
    }

    /**
     * Проверяет удаление игрока
     */
    @Test
    void shouldRemovePlayer() {
        MultiplayerGame game = new MultiplayerGame(20, 30, 3, 100);

        int firstId = game.addPlayer("Alice");
        int secondId = game.addPlayer("Bob");

        game.removePlayer(secondId);

        GameState state = GameState.parse(game.toStateLine(firstId));

        assertEquals(1, game.getPlayerCount());
        assertEquals(1, state.getPlayers().size());
        assertEquals("Alice", state.getPlayers().get(0).getNickname());
        assertEquals(1, state.getLeaderboard().size());
    }

    /**
     * Проверяет изменение направления
     */
    @Test
    void shouldAcceptDirectionChange() {
        MultiplayerGame game = new MultiplayerGame(20, 30, 3, 100);

        int playerId = game.addPlayer("Alice");

        game.changeDirection(playerId, Direction.UP);
        game.update();

        GameState state = GameState.parse(game.toStateLine(playerId));

        assertFalse(state.getPlayers().isEmpty());
        assertEquals("Alice", state.getPlayers().get(0).getNickname());
    }

    /**
     * Проверяет формирование лидерборда
     */
    @Test
    void shouldCreateLeaderboard() {
        MultiplayerGame game = new MultiplayerGame(20, 30, 3, 100);

        int firstId = game.addPlayer("Alice");
        game.addPlayer("Bob");

        GameState state = GameState.parse(game.toStateLine(firstId));

        assertEquals(2, state.getLeaderboard().size());
        assertTrue(
                state.getLeaderboard().get(0).getScore()
                        >= state.getLeaderboard().get(1).getScore()
        );
    }

    /**
     * Проверяет, что update не ломает состояние игры
     */
    @Test
    void shouldUpdateGameState() {
        MultiplayerGame game = new MultiplayerGame(20, 30, 3, 100);

        int playerId = game.addPlayer("Alice");

        game.update();
        GameState state = GameState.parse(game.toStateLine(playerId));

        assertEquals(1, state.getPlayers().size());
        assertEquals(3, state.getFoods().size());
    }
}