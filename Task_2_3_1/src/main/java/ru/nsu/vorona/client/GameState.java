package ru.nsu.vorona.client;

import java.util.ArrayList;
import java.util.List;

/**
 * Состояние игры от сервера
 */
public class GameState {
    private final int viewerId;
    private final int rows;
    private final int cols;
    private final List<Cell> foods;
    private final List<PlayerState> players;
    private final List<LeaderboardItem> leaderboard;

    /**
     * Создаёт состояние игры
     */
    public GameState(
            int viewerId,
            int rows,
            int cols,
            List<Cell> foods,
            List<PlayerState> players,
            List<LeaderboardItem> leaderboard
    ) {
        this.viewerId = viewerId;
        this.rows = rows;
        this.cols = cols;
        this.foods = foods;
        this.players = players;
        this.leaderboard = leaderboard;
    }

    /**
     * Разбирает строку состояния
     *
     * @param line строка сервера
     * @return состояние игры
     */
    public static GameState parse(String line) {
        String[] parts = line.split("\\|", -1);

        int viewerId = Integer.parseInt(parts[1]);
        int rows = Integer.parseInt(parts[2]);
        int cols = Integer.parseInt(parts[3]);

        List<Cell> foods = parseCells(parts[4]);
        List<PlayerState> players = parsePlayers(parts[5]);
        List<LeaderboardItem> leaderboard = parseLeaderboard(parts[6]);

        return new GameState(viewerId, rows, cols, foods, players, leaderboard);
    }

    /**
     * Разбирает клетки
     *
     * @param text строка клеток
     * @return список клеток
     */
    private static List<Cell> parseCells(String text) {
        List<Cell> cells = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return cells;
        }

        for (String part : text.split(";")) {
            String[] coords = part.split(",");
            cells.add(new Cell(Integer.parseInt(coords[0]), Integer.parseInt(coords[1])));
        }

        return cells;
    }

    /**
     * Разбирает игроков
     *
     * @param text строка игроков
     * @return список игроков
     */
    private static List<PlayerState> parsePlayers(String text) {
        List<PlayerState> players = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return players;
        }

        for (String chunk : text.split("#")) {
            String[] data = chunk.split(",", 6);

            int id = Integer.parseInt(data[0]);
            String nickname = data[1];
            int score = Integer.parseInt(data[2]);
            boolean alive = Boolean.parseBoolean(data[3]);
            int respawnSecondsLeft = Integer.parseInt(data[4]);
            List<Cell> body = parseCells(data[5]);

            players.add(new PlayerState(id, nickname, score, alive, respawnSecondsLeft, body));
        }

        return players;
    }

    /**
     * Разбирает лидерборд
     *
     * @param text строка лидерборда
     * @return список строк
     */
    private static List<LeaderboardItem> parseLeaderboard(String text) {
        List<LeaderboardItem> leaderboard = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return leaderboard;
        }

        for (String part : text.split(";")) {
            String[] data = part.split(",", 3);

            leaderboard.add(new LeaderboardItem(
                    Integer.parseInt(data[0]),
                    data[1],
                    Integer.parseInt(data[2])
            ));
        }

        return leaderboard;
    }

    public int getViewerId() {
        return viewerId;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public List<Cell> getFoods() {
        return foods;
    }

    public List<PlayerState> getPlayers() {
        return players;
    }

    public List<LeaderboardItem> getLeaderboard() {
        return leaderboard;
    }

    public PlayerState getViewer() {
        for (PlayerState player : players) {
            if (player.getId() == viewerId) {
                return player;
            }
        }

        return null;
    }
}