package ru.nsu.vorona.server;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Модель сетевой игры
 */
public class MultiplayerGame {
    private static final long RESPAWN_DELAY_MS = 3000;

    private final int rows;
    private final int cols;
    private final int foodCount;
    private final int tickMs;
    private final Random random = new Random();

    private final Map<Integer, PlayerSnake> snakes = new LinkedHashMap<>();
    private final List<Cell> foods = new ArrayList<>();
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
        this.foodCount = foodCount;
        this.tickMs = tickMs;

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
        long now = System.currentTimeMillis();

        for (PlayerSnake snake : snakes.values()) {
            if (!snake.isAlive() && now >= snake.getRespawnTimeMillis()) {
                snake.respawn(randomFreeCell(), randomDirection());
            }
        }

        Map<PlayerSnake, MoveInfo> moves = new LinkedHashMap<>();

        for (PlayerSnake snake : snakes.values()) {
            if (snake.isAlive()) {
                snake.applyNextDirection();

                Cell newHead = snake.nextHead();
                boolean grow = foods.contains(newHead);
                moves.put(snake, new MoveInfo(newHead, grow));
            }
        }

        List<PlayerSnake> deadSnakes = findDeadSnakes(moves);

        for (Map.Entry<PlayerSnake, MoveInfo> entry : moves.entrySet()) {
            PlayerSnake snake = entry.getKey();
            MoveInfo move = entry.getValue();

            if (deadSnakes.contains(snake)) {
                snake.kill(RESPAWN_DELAY_MS);
                continue;
            }

            snake.move(move.newHead, move.grow);

            if (move.grow) {
                foods.remove(move.newHead);
                snake.increaseScore();
                addFood();
            }
        }
    }

    /**
     * Данные будущего хода змейки
     */
    private static class MoveInfo {
        private final Cell newHead;
        private final boolean grow;

        /**
         * Создаёт данные хода
         *
         * @param newHead новая голова
         * @param grow будет ли рост
         */
        private MoveInfo(Cell newHead, boolean grow) {
            this.newHead = newHead;
            this.grow = grow;
        }
    }

    /**
     * Находит змей, которые должны умереть на этом тике
     *
     * @param moves будущие ходы змей
     * @return список погибших змей
     */
    private List<PlayerSnake> findDeadSnakes(Map<PlayerSnake, MoveInfo> moves) {
        List<PlayerSnake> deadSnakes = new ArrayList<>();

        for (Map.Entry<PlayerSnake, MoveInfo> entry : moves.entrySet()) {
            PlayerSnake snake = entry.getKey();
            MoveInfo move = entry.getValue();

            if (isOutOfBounds(move.newHead) || isFutureBodyCollision(snake, move, moves)) {
                addDeadSnake(deadSnakes, snake);
            }
        }

        List<Map.Entry<PlayerSnake, MoveInfo>> entries = new ArrayList<>(moves.entrySet());

        for (int i = 0; i < entries.size(); i++) {
            for (int j = i + 1; j < entries.size(); j++) {
                PlayerSnake firstSnake = entries.get(i).getKey();
                PlayerSnake secondSnake = entries.get(j).getKey();

                Cell firstHead = entries.get(i).getValue().newHead;
                Cell secondHead = entries.get(j).getValue().newHead;

                if (firstHead.equals(secondHead)) {
                    addDeadSnake(deadSnakes, firstSnake);
                    addDeadSnake(deadSnakes, secondSnake);
                }

                if (firstHead.equals(secondSnake.getHead()) && secondHead.equals(firstSnake.getHead())) {
                    addDeadSnake(deadSnakes, firstSnake);
                    addDeadSnake(deadSnakes, secondSnake);
                }
            }
        }

        return deadSnakes;
    }

    /**
     * Проверяет столкновение с телом после будущего движения
     *
     * @param currentSnake текущая змейка
     * @param currentMove ход текущей змейки
     * @param moves будущие ходы всех змей
     * @return true, если есть столкновение
     */
    private boolean isFutureBodyCollision(
            PlayerSnake currentSnake,
            MoveInfo currentMove,
            Map<PlayerSnake, MoveInfo> moves
    ) {
        for (Map.Entry<PlayerSnake, MoveInfo> entry : moves.entrySet()) {
            PlayerSnake otherSnake = entry.getKey();

            if (!otherSnake.isAlive()) {
                continue;
            }

            MoveInfo otherMove = entry.getValue();
            List<Cell> body = otherSnake.getBody();
            int lastIndex = body.size() - 1;

            for (int i = 0; i < body.size(); i++) {
                boolean isTail = i == lastIndex;
                boolean tailWillMove = !otherMove.grow;

                if (isTail && tailWillMove) {
                    continue;
                }

                if (currentSnake == otherSnake && i == 0) {
                    continue;
                }

                if (currentMove.newHead.equals(body.get(i))) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Добавляет змейку в список погибших
     *
     * @param deadSnakes список погибших змей
     * @param snake змейка
     */
    private void addDeadSnake(List<PlayerSnake> deadSnakes, PlayerSnake snake) {
        if (!deadSnakes.contains(snake)) {
            deadSnakes.add(snake);
        }
    }

    /**
     * Проверяет выход за границы поля
     *
     * @param cell клетка
     * @return true, если клетка вне поля
     */
    private boolean isOutOfBounds(Cell cell) {
        return cell.row() < 0
                || cell.row() >= rows
                || cell.col() < 0
                || cell.col() >= cols;
    }

    /**
     * Проверяет, занята ли клетка живой змейкой
     *
     * @param cell клетка
     * @return true, если клетка занята
     */
    private boolean isCellOccupiedBySnake(Cell cell) {
        for (PlayerSnake snake : snakes.values()) {
            if (snake.isAlive() && snake.contains(cell)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Формирует строку состояния
     *
     * @param viewerId id клиента
     * @return строка состояния
     */
    public synchronized String toStateLine(int viewerId) {
        return "STATE"
                + "|" + viewerId
                + "|" + rows
                + "|" + cols
                + "|" + cellsToString(foods)
                + "|" + snakesToString()
                + "|" + leaderboardToString();
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

        return nickname
                .replace("|", "_")
                .replace(",", "_")
                .replace(";", "_")
                .replace("#", "_");
    }

    /**
     * Преобразует клетки в строку
     *
     * @param cells клетки
     * @return строка
     */
    private String cellsToString(List<Cell> cells) {
        return cells.stream()
                .map(cell -> cell.row() + "," + cell.col())
                .collect(Collectors.joining(";"));
    }

    /**
     * Преобразует змей в строку
     *
     * @return строка
     */
    private String snakesToString() {
        long now = System.currentTimeMillis();

        return snakes.values().stream()
                .map(snake -> snake.getId()
                        + "," + snake.getNickname()
                        + "," + snake.getScore()
                        + "," + snake.isAlive()
                        + "," + respawnSecondsLeft(snake, now)
                        + "," + cellsToString(new ArrayList<>(snake.getBody())))
                .collect(Collectors.joining("#"));
    }

    /**
     * Преобразует лидерборд в строку
     *
     * @return строка
     */
    private String leaderboardToString() {
        return snakes.values().stream()
                .sorted(Comparator.comparingInt(PlayerSnake::getScore).reversed())
                .map(snake -> snake.getId() + "," + snake.getNickname() + "," + snake.getScore())
                .collect(Collectors.joining(";"));
    }

    /**
     * Возвращает секунды до респавна
     *
     * @param snake змейка
     * @param now текущее время
     * @return секунды до респавна
     */
    private int respawnSecondsLeft(PlayerSnake snake, long now) {
        if (snake.isAlive()) {
            return 0;
        }

        long left = Math.max(0, snake.getRespawnTimeMillis() - now);
        return (int) Math.ceil(left / 1000.0);
    }

    /**
     * Удаляет игрока из игры
     *
     * @param playerId id игрока
     */
    public synchronized void removePlayer(int playerId) {
        snakes.remove(playerId);
    }
}