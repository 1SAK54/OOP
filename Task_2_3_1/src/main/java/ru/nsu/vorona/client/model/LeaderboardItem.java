package ru.nsu.vorona.client.model;

/**
 * Строка лидерборда
 */
public class LeaderboardItem {
    private final int playerId;
    private final String nickname;
    private final int score;

    /**
     * Создаёт строку лидерборда
     *
     * @param playerId id игрока
     * @param nickname ник игрока
     * @param score счёт игрока
     */
    public LeaderboardItem(int playerId, String nickname, int score) {
        this.playerId = playerId;
        this.nickname = nickname;
        this.score = score;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getNickname() {
        return nickname;
    }

    public int getScore() {
        return score;
    }
}