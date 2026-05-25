package ru.nsu.vorona.server.service;

import ru.nsu.vorona.server.model.Cell;
import ru.nsu.vorona.server.model.Direction;
import ru.nsu.vorona.server.model.MoveInfo;
import ru.nsu.vorona.server.model.PlayerSnake;
import ru.nsu.vorona.server.network.GameStateEncoder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Модель сетевой игры
 */
public class MultiplayerGame {
    private static final long RESPAWN_DELAY_MS = 3000;

    private final int rows;
    private final int cols;
    private final int tickMs;

    private final Random random = new Random();
    private final Map<Integer, PlayerSnake> snakes = new ConcurrentHashMap<>();
    private final List<Cell> foods = new ArrayList<>();
    private final CollisionDetector collisionDetector;
    private final GameStateEncoder gameStateEncoder = new GameStateEncoder();

    private int nextPlayerId = 1;

    /**
     * Создаёт игру
     *
     * @param rows число строк
     * @param cols число столбцов
     * @param foodCount число еды
     * @param tickMs задержка хода
     */
    public MultiplayerGame(int rows, int cols, int foodCount, int tickMs) {
        this.rows = rows;
        this.cols = cols;
        this.tickMs = tickMs;
        this.collisionDetector = new CollisionDetector(rows, cols);

        while (foods.size() < foodCount) {
            addFood();
        }
    }

    /**
     * Добавляет игрока
     *
     * @param nickname ник игрока
     * @return id игрока
     */
    public synchronized int addPlayer(String nickname) {
        int id = nextPlayerId++;
        Cell start = randomFreeCell();
        Direction direction = randomDirection();

        snakes.put(id, new PlayerSnake(id, sanitize(nickname), start, direction));
        return id;
    }

    /**
     * Удаляет игрока
     *
     * @param playerId id игрока
     */
    public synchronized void removePlayer(int playerId) {
        snakes.remove(playerId);
    }

    /**
     * Возвращает число игроков
     *
     * @return число игроков
     */
    public synchronized int getPlayerCount() {
        return snakes.size();
    }

    /**
     * Меняет направление игрока
     *
     * @param playerId id игрока
     * @param direction направление
     */
    public synchronized void changeDirection(int playerId, Direction direction) {
        PlayerSnake snake = snakes.get(playerId);

        if (snake != null) {
            snake.changeDirection(direction);
        }
    }

    /**
     * Обновляет игру
     */
    public synchronized void update() {
        respawnDeadSnakes();

        Map<PlayerSnake, MoveInfo> moves = buildMoves();
        Set<PlayerSnake> deadSnakes = collisionDetector.findDeadSnakes(moves);

        applyMoves(moves, deadSnakes);
    }

    /**
     * Формирует строку состояния
     *
     * @param viewerId id клиента
     * @return строка состояния
     */
    public synchronized String toStateLine(int viewerId) {
        return gameStateEncoder.encode(viewerId, rows, cols, foods, snakes.values());
    }

    /**
     * Возвращает задержку хода
     *
     * @return задержка
     */
    public int getTickMs() {
        return tickMs;
    }

    /**
     * Возрождает мёртвых змей, если прошло время ожидания
     */
    private void respawnDeadSnakes() {
        long now = System.currentTimeMillis();

        for (PlayerSnake snake : snakes.values()) {
            if (!snake.isAlive() && now >= snake.getRespawnTimeMillis()) {
                snake.respawn(randomFreeCell(), randomDirection());
            }
        }
    }

    /**
     * Создаёт будущие ходы живых змей
     *
     * @return будущие ходы
     */
    private Map<PlayerSnake, MoveInfo> buildMoves() {
        Map<PlayerSnake, MoveInfo> moves = new LinkedHashMap<>();

        for (PlayerSnake snake : snakes.values()) {
            if (snake.isAlive()) {
                snake.applyNextDirection();

                Cell newHead = snake.nextHead();
                boolean grow = foods.contains(newHead);

                moves.put(snake, new MoveInfo(newHead, grow));
            }
        }

        return moves;
    }

    /**
     * Применяет ходы змей
     *
     * @param moves будущие ходы
     * @param deadSnakes погибшие змеи
     */
    private void applyMoves(Map<PlayerSnake, MoveInfo> moves, Set<PlayerSnake> deadSnakes) {
        for (Map.Entry<PlayerSnake, MoveInfo> entry : moves.entrySet()) {
            PlayerSnake snake = entry.getKey();
            MoveInfo move = entry.getValue();

            if (deadSnakes.contains(snake)) {
                snake.kill(RESPAWN_DELAY_MS);
                continue;
            }

            snake.move(move.newHead(), move.grow());

            if (move.grow()) {
                foods.remove(move.newHead());
                snake.increaseScore();
                addFood();
            }
        }
    }

    /**
     * Добавляет еду
     */
    private void addFood() {
        foods.add(randomFreeCell());
    }

    /**
     * Возвращает случайную свободную клетку
     *
     * @return свободная клетка
     */
    private Cell randomFreeCell() {
        for (int attempt = 0; attempt < 1000; attempt++) {
            Cell cell = new Cell(random.nextInt(rows), random.nextInt(cols));

            if (isFree(cell)) {
                return cell;
            }
        }

        throw new IllegalStateException("No free cells");
    }

    /**
     * Проверяет свободную клетку
     *
     * @param cell клетка
     * @return true, если клетка свободна
     */
    private boolean isFree(Cell cell) {
        if (foods.contains(cell)) {
            return false;
        }

        for (PlayerSnake snake : snakes.values()) {
            if (snake.isAlive() && snake.contains(cell)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Возвращает случайное направление
     *
     * @return направление
     */
    private Direction randomDirection() {
        Direction[] values = Direction.values();
        return values[random.nextInt(values.length)];
    }

    /**
     * Очищает ник от разделителей протокола
     *
     * @param nickname ник
     * @return безопасный ник
     */
    private String sanitize(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            return "Player";
        }

        String safe = nickname
                .replace("|", "_")
                .replace(",", "_")
                .replace(";", "_")
                .replace("#", "_");

        return safe.length() > 16 ? safe.substring(0, 16) : safe;
    }
}