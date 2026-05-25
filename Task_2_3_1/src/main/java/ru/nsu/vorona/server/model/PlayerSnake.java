package ru.nsu.vorona.server.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Змейка игрока
 */
public class PlayerSnake {
    private final int id;
    private final String nickname;
    private final LinkedList<Cell> body = new LinkedList<>();

    private Direction direction;
    private int score;
    private boolean alive = true;
    private long respawnTimeMillis;
    private Direction nextDirection;

    /**
     * Создаёт змейку игрока
     *
     * @param id номер игрока
     * @param nickname ник игрока
     * @param start стартовая клетка
     * @param direction начальное направление
     */
    public PlayerSnake(int id, String nickname, Cell start, Direction direction) {
        this.id = id;
        this.nickname = nickname;
        this.direction = direction;
        body.add(start);
    }

    /**
     * Возвращает id игрока
     *
     * @return id игрока
     */
    public int getId() {
        return id;
    }

    /**
     * Возвращает ник игрока
     *
     * @return ник игрока
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Возвращает голову змейки
     *
     * @return клетка головы
     */
    public Cell getHead() {
        return body.getFirst();
    }

    /**
     * Возвращает тело змейки
     *
     * @return клетки тела
     */
    public List<Cell> getBody() {
        return Collections.unmodifiableList(body);
    }

    /**
     * Возвращает счёт игрока
     *
     * @return счёт игрока
     */
    public int getScore() {
        return score;
    }

    /**
     * Увеличивает счёт игрока
     */
    public void increaseScore() {
        score++;
    }

    /**
     * Проверяет, жив ли игрок
     *
     * @return true, если игрок жив
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Возвращает время респавна
     *
     * @return время респавна
     */
    public long getRespawnTimeMillis() {
        return respawnTimeMillis;
    }

    /**
     * Меняет направление
     *
     * @param newDirection новое направление
     */
    public void changeDirection(Direction newDirection) {
        if (!alive) {
            return;
        }

        if (nextDirection != null) {
            return;
        }

        if (direction.isOpposite(newDirection)) {
            return;
        }

        nextDirection = newDirection;
    }

    /**
     * Применяет отложенное направление
     */
    public void applyNextDirection() {
        if (nextDirection != null) {
            direction = nextDirection;
            nextDirection = null;
        }
    }

    /**
     * Возвращает следующую голову
     *
     * @return новая клетка головы
     */
    public Cell nextHead() {
        Cell head = getHead();
        return new Cell(
                head.row() + direction.getRowDelta(),
                head.col() + direction.getColDelta()
        );
    }

    /**
     * Двигает змейку
     *
     * @param newHead новая голова
     * @param grow нужно ли расти
     */
    public void move(Cell newHead, boolean grow) {
        body.addFirst(newHead);

        if (!grow) {
            body.removeLast();
        }
    }

    /**
     * Убивает змейку
     *
     * @param respawnDelayMs задержка респавна
     */
    public void kill(long respawnDelayMs) {
        alive = false;
        respawnTimeMillis = System.currentTimeMillis() + respawnDelayMs;
    }

    /**
     * Возрождает змейку
     *
     * @param start новая стартовая клетка
     * @param direction новое направление
     */
    public void respawn(Cell start, Direction direction) {
        body.clear();
        body.add(start);
        this.direction = direction;
        nextDirection = null;
        alive = true;
        respawnTimeMillis = 0;
    }

    /**
     * Проверяет клетку
     *
     * @param cell клетка
     * @return true, если змейка занимает клетку
     */
    public boolean contains(Cell cell) {
        return body.contains(cell);
    }
}