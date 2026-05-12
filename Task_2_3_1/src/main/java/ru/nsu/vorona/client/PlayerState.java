package ru.nsu.vorona.client;

import java.util.List;

/**
 * Состояние игрока
 */
public class PlayerState {
    private final int id;
    private final String nickname;
    private final int score;
    private final boolean alive;
    private final int respawnSecondsLeft;
    private final List<Cell> body;

    /**
     * Создаёт состояние игрока
     *
     * @param id id игрока
     * @param nickname ник игрока
     * @param score счёт игрока
     * @param alive жив ли игрок
     * @param respawnSecondsLeft время до респавна
     * @param body тело змейки
     */
    public PlayerState(
            int id,
            String nickname,
            int score,
            boolean alive,
            int respawnSecondsLeft,
            List<Cell> body
    ) {
        this.id = id;
        this.nickname = nickname;
        this.score = score;
        this.alive = alive;
        this.respawnSecondsLeft = respawnSecondsLeft;
        this.body = body;
    }

    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public int getScore() {
        return score;
    }

    public boolean isAlive() {
        return alive;
    }

    public int getRespawnSecondsLeft() {
        return respawnSecondsLeft;
    }

    public List<Cell> getBody() {
        return body;
    }
}